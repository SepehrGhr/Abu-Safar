package ir.ac.kntu.abusafar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisListenerConfig {

    @Bean
    RedisMessageListenerContainer keyExpirationListenerContainer(RedisConnectionFactory connectionFactory, RedisKeyExpirationListener expirationListener) {
        RedisMessageListenerContainer listenerContainer = new RedisMessageListenerContainer();
        listenerContainer.setConnectionFactory(connectionFactory);

        listenerContainer.addMessageListener(expirationListener, new PatternTopic("__keyevent@*__:expired"));

        return listenerContainer;
    }
}