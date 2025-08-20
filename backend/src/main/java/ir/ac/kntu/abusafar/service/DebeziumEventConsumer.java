package ir.ac.kntu.abusafar.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.ac.kntu.abusafar.document.TicketDocument;
import ir.ac.kntu.abusafar.repository.AdditionalServiceDAO;
import ir.ac.kntu.abusafar.repository.TicketDAO;
import ir.ac.kntu.abusafar.repository.impl.TicketDAOImpl;
import ir.ac.kntu.abusafar.repository.elasticsearch.TicketSearchRepository;
import ir.ac.kntu.abusafar.util.constants.enums.AgeRange;
import ir.ac.kntu.abusafar.util.constants.enums.ServiceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DebeziumEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DebeziumEventConsumer.class);

    private final ObjectMapper objectMapper;
    private final TicketSearchRepository elasticsearchRepository;
    private final TicketDAO ticketDAO;
    private final AdditionalServiceDAO additionalServiceDAO;

    @Autowired
    public DebeziumEventConsumer(ObjectMapper objectMapper,
                                 TicketSearchRepository elasticsearchRepository,
                                 TicketDAO ticketDAO,
                                 AdditionalServiceDAO additionalServiceDAO) {
        this.objectMapper = objectMapper;
        this.elasticsearchRepository = elasticsearchRepository;
        this.ticketDAO = ticketDAO;
        this.additionalServiceDAO = additionalServiceDAO;
    }

    @KafkaListener(topics = "abusafar-db-changes.public.tickets")
    public void handleTicketEvent(String payload) {
        try {
            if (payload == null) {
                LOGGER.warn("Received null payload from Kafka.");
                return;
            }

            JsonNode message = objectMapper.readTree(payload);
            JsonNode payloadNode = message;

            String operation = payloadNode.has("op") ? payloadNode.get("op").asText() : null;
            if (operation == null) {
                LOGGER.warn("Received a message without an 'op' field, skipping.");
                return;
            }

            if ("d".equals(operation)) {
                JsonNode beforeNode = payloadNode.get("before");
                if (beforeNode != null && !beforeNode.isNull()) {
                    String idToDelete = generateDocumentId(beforeNode);
                    elasticsearchRepository.deleteById(idToDelete);
                    LOGGER.info("Deleted document from Elasticsearch with ID: {}", idToDelete);
                }
                return;
            }

            JsonNode afterNode = payloadNode.get("after");
            if (afterNode == null || afterNode.isNull()) {
                return; // Skip messages that aren't create/update
            }

            Long tripId = afterNode.get("trip_id").asLong();
            AgeRange age = AgeRange.valueOf(afterNode.get("age").asText());

            Optional<TicketDAOImpl.DenormalizedTicketData> dataOpt = ticketDAO.findFullyJoinedTicket(tripId, age);

            if (dataOpt.isPresent()) {
                TicketDocument doc = mapToTicketDocument(dataOpt.get());
                elasticsearchRepository.save(doc);
                LOGGER.info("Successfully indexed document in Elasticsearch with ID: {}", doc.getId());
            } else {
                LOGGER.warn("Could not find full data in PostgreSQL for ticket with tripId: {} and age: {}.", tripId, age);
            }

        } catch (Exception e) {
            LOGGER.error("CRITICAL ERROR: An exception was thrown during message processing.", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Maps the DenormalizedTicketData and enriches it with additional services
     * to create a complete TicketDocument for Elasticsearch. It also handles
     * necessary type conversions safely.
     */
    private TicketDocument mapToTicketDocument(TicketDAOImpl.DenormalizedTicketData data) {
        TicketDocument doc = new TicketDocument();

        doc.setId(data.tripId() + "_" + data.age().name());
        doc.setTripId(data.tripId());
        doc.setAge(data.age());
        doc.setPrice(data.price());
        doc.setTripVehicle(data.tripVehicle());
        doc.setDepartureTimestamp(data.departureTimestamp());
        doc.setArrivalTimestamp(data.arrivalTimestamp());
        doc.setStopCount(data.stopCount());

        // --- CORRECTED SAFE TYPE CONVERSIONS ---
        // 1. Safely convert integer to short
        doc.setTotalCapacity(safeIntToShort(data.totalCapacity()));
        doc.setReservedCapacity(safeIntToShort(data.reservedCapacity()));

        // 2. Fetch and correctly map the List of ServiceType enums to a List of Strings
        List<ServiceType> serviceNames = additionalServiceDAO.findServiceTypesByTripId(data.tripId());
        doc.setServices(serviceNames);
        // ------------------------------------

        int available = data.totalCapacity() - data.reservedCapacity();
        doc.setAvailableSeats(Math.max(0, available));

        TicketDocument.Location origin = new TicketDocument.Location();
        origin.setId(data.originId());
        origin.setCity(data.originCity());
        origin.setProvince(data.originProvince());
        origin.setCountry(data.originCountry());
        doc.setOrigin(origin);

        TicketDocument.Location destination = new TicketDocument.Location();
        destination.setId(data.destId());
        destination.setCity(data.destCity());
        destination.setProvince(data.destProvince());
        destination.setCountry(data.destCountry());
        doc.setDestination(destination);

        TicketDocument.Company company = new TicketDocument.Company();
        company.setName(data.companyName());
        company.setLogo(data.companyLogo());
        doc.setCompany(company);

        TicketDocument.VehicleDetails vehicleDetails = new TicketDocument.VehicleDetails();
        vehicleDetails.setTrainStars(data.trainStars());
        vehicleDetails.setRoomType(data.trainRoomType() != null ? data.trainRoomType().name() : null);
        vehicleDetails.setBusClass(data.busClass());
        vehicleDetails.setFlightClass(data.flightClass());
        doc.setVehicleDetails(vehicleDetails);

        return doc;
    }

    /**
     * Safely converts an integer to a short, throwing an exception if the value is out of range.
     * This prevents data corruption from unchecked narrowing casts.
     * @param value The integer to convert.
     * @return The converted short value.
     */
    private short safeIntToShort(int value) {
        if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
            throw new IllegalArgumentException("Cannot safely cast int to short, value is out of range: " + value);
        }
        return (short) value;
    }

    private String generateDocumentId(JsonNode node) {
        Long tripId = node.get("trip_id").asLong();
        String age = node.get("age").asText();
        return tripId + "_" + age;
    }

    @DltHandler
    public void handleDlt(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        LOGGER.error("Message sent to DLT! From topic {}: {}", topic, message);
    }
}