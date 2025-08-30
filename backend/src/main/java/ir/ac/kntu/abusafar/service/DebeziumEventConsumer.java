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
        if (payload == null) {
            return;
        }

        try {
            JsonNode message = objectMapper.readTree(payload);
            String operation = message.has("op") ? message.get("op").asText() : null;

            if (operation == null) {
                LOGGER.warn("Received a message on tickets topic without an 'op' field, skipping.");
                return;
            }

            if ("d".equals(operation)) {
                JsonNode beforeNode = message.get("before");
                if (beforeNode != null && !beforeNode.isNull()) {
                    String idToDelete = generateDocumentId(beforeNode);
                    elasticsearchRepository.deleteById(idToDelete);
                    LOGGER.info("Deleted document from Elasticsearch with ID: {}", idToDelete);
                }
                return;
            }

            JsonNode afterNode = message.get("after");
            if (afterNode == null || afterNode.isNull()) {
                return;
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
            LOGGER.error("CRITICAL ERROR processing ticket event: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "abusafar-db-changes.public.trips")
    public void handleTripEvent(String payload) {
        if (payload == null) {
            return;
        }

        try {
            JsonNode message = objectMapper.readTree(payload);
            String operation = message.has("op") ? message.get("op").asText() : null;

            if (operation == null) {
                LOGGER.warn("Received a message on trips topic without an 'op' field, skipping.");
                return;
            }

            if ("d".equals(operation)) {
                JsonNode beforeNode = message.get("before");
                if (beforeNode != null && !beforeNode.isNull()) {
                    Long tripId = beforeNode.get("trip_id").asLong();
                    LOGGER.info("Received delete for tripId: {}. Deleting associated tickets from Elasticsearch.", tripId);
                    for (AgeRange age : AgeRange.values()) {
                        elasticsearchRepository.deleteById(tripId + "_" + age.name());
                    }
                }
                return;
            }

            JsonNode afterNode = message.get("after");
            if (afterNode == null || afterNode.isNull()){
                return;
            }

            Long tripId = afterNode.get("trip_id").asLong();
            LOGGER.info("Received update for tripId: {}. Re-indexing associated tickets.", tripId);

            for (AgeRange age : AgeRange.values()) {
                Optional<TicketDAOImpl.DenormalizedTicketData> dataOpt = ticketDAO.findFullyJoinedTicket(tripId, age);
                if (dataOpt.isPresent()) {
                    TicketDocument doc = mapToTicketDocument(dataOpt.get());
                    elasticsearchRepository.save(doc);
                    LOGGER.info("Re-indexed document due to trip update. ID: {}", doc.getId());
                }
            }

        } catch (Exception e) {
            LOGGER.error("CRITICAL ERROR processing trip event: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

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

        doc.setTotalCapacity(safeIntToShort(data.totalCapacity()));
        doc.setReservedCapacity(safeIntToShort(data.reservedCapacity()));

        List<ServiceType> serviceNames = additionalServiceDAO.findServiceTypesByTripId(data.tripId());
        doc.setServices(serviceNames);

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