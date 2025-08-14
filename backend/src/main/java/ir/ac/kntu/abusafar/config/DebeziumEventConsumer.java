package ir.ac.kntu.abusafar.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class DebeziumEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DebeziumEventConsumer.class);

    @KafkaListener(topics = "abusafar-db-changes.public.tickets")
    public void handleTicketEvent(String message) {
        LOGGER.info("Received event for tickets topic: {}", message);
        // TODO: add logic here
        //  if (message.contains("bad-data")) throw new RuntimeException("Simulating a processing error!");
    }
    // @KafkaListener(topics = "abusafar-db-changes.public.trips")
    // public void handleTripEvent(String message) { ... }

    @DltHandler
    public void handleDlt(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        LOGGER.error("Message sent to DLT! From topic {}: {}", topic, message);
    }
}