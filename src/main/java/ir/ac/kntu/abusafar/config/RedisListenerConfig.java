package ir.ac.kntu.abusafar.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.Properties;

@Configuration
public class RedisListenerConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisListenerConfig.class);

    @Bean
    RedisMessageListenerContainer keyExpirationListenerContainer(RedisConnectionFactory connectionFactory, RedisKeyExpirationListener expirationListener) {

        try (RedisConnection connection = connectionFactory.getConnection()) {
            Properties properties = new Properties();
            properties.setProperty("notify-keyspace-events", "Ex");
            connection.serverCommands().setConfig("notify-keyspace-events", "Ex");
        } catch (Exception e) {
            LOGGER.error("CRITICAL ERROR: Could not configure Redis keyspace notifications. Expiry/reminder features will not work.", e);
        }

        RedisMessageListenerContainer listenerContainer = new RedisMessageListenerContainer();
        listenerContainer.setConnectionFactory(connectionFactory);

        listenerContainer.addMessageListener(expirationListener, new PatternTopic("__keyevent@*__:expired"));

        return listenerContainer;
    }
}