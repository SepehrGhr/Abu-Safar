//package ir.ac.kntu.abusafar.config;
//
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
//import org.springframework.kafka.listener.DefaultErrorHandler;
//import org.springframework.util.backoff.FixedBackOff;
//@ConditionalOnProperty(name = "kafka.feature.enabled", havingValue = "true")
//@Configuration
//public class KafkaConfig {
//
//    @Bean
//    public DefaultErrorHandler errorHandler(KafkaTemplate<String, String> template) {
//        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template);
//
//        // Retry a failed message 2 times with a 1-second delay between attempts.
//        // After the final attempt fails, the message will be sent to the DLQ.
//        FixedBackOff backOff = new FixedBackOff(1000L, 2);
//
//        return new DefaultErrorHandler(recoverer, backOff);
//    }
//}