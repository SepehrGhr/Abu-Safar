package ir.ac.kntu.abusafar.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.ac.kntu.abusafar.document.TicketDocument;
import ir.ac.kntu.abusafar.repository.TicketDAO;
import ir.ac.kntu.abusafar.repository.impl.TicketDAOImpl;
import ir.ac.kntu.abusafar.repository.elasticsearch.TicketSearchRepository;
import ir.ac.kntu.abusafar.util.constants.enums.AgeRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DebeziumEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DebeziumEventConsumer.class);

    private final ObjectMapper objectMapper;
    private final TicketSearchRepository elasticsearchRepository;
    private final TicketDAO ticketDAO;

    public DebeziumEventConsumer(ObjectMapper objectMapper,
                                 TicketSearchRepository elasticsearchRepository,
                                 TicketDAO ticketDAO) {
        this.objectMapper = objectMapper;
        this.elasticsearchRepository = elasticsearchRepository;
        this.ticketDAO = ticketDAO;
    }

    @KafkaListener(topics = "abusafar-db-changes.public.tickets")
    public void handleTicketEvent(String payload) {
        try {
            if (payload == null) {
                return; // Silently skip null messages
            }

            JsonNode message = objectMapper.readTree(payload);

            // --- THIS IS THE FIX ---
            // The pgoutput plugin has a flat structure. The 'message' is the payload.
            JsonNode payloadNode = message;
            // --- END FIX ---

            String operation = payloadNode.has("op") ? payloadNode.get("op").asText() : null;
            if (operation == null) {
                LOGGER.warn("Received a message without an 'op' field, skipping.");
                return;
            }

            if ("d".equals(operation)) {
                JsonNode beforeNode = payloadNode.get("before");
                String idToDelete = generateDocumentId(beforeNode);
                elasticsearchRepository.deleteById(idToDelete);
                LOGGER.info("Deleted document from Elasticsearch with ID: {}", idToDelete);
                return;
            }

            JsonNode afterNode = payloadNode.get("after");
            if (afterNode == null || afterNode.isNull()){
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

    // You can add more listeners for other tables like `trips` to handle
    // updates to `reserved_capacity` more efficiently.

    /**
     * Maps the DenormalizedTicketData record from Postgres to the TicketDocument for Elasticsearch.
     * This uses the correct record accessor methods (e.g., data.tripId() instead of data.getTripId()).
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
        int available = data.totalCapacity() - data.reservedCapacity();
        doc.setAvailableSeats(Math.max(0, available));

        TicketDocument.Location origin = new TicketDocument.Location();
        origin.setCity(data.originCity());
        origin.setProvince(data.originProvince());
        origin.setCountry(data.originCountry());
        doc.setOrigin(origin);
        TicketDocument.Location destination = new TicketDocument.Location();
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
     * Generates the unique Elasticsearch document ID from a Debezium payload node.
     */
    private String generateDocumentId(JsonNode node) {
        Long tripId = node.get("trip_id").asLong();
        String age = node.get("age").asText();
        return tripId + "_" + age;
    }
    // @KafkaListener(topics = "abusafar-db-changes.public.trips")
    // public void handleTripEvent(String message) { ... }

    @DltHandler
    public void handleDlt(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        LOGGER.error("Message sent to DLT! From topic {}: {}", topic, message);
    }
}
