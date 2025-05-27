package ir.ac.kntu.abusafar.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    // Add other properties as needed (e.g., password, timeout)
    // @Value("${spring.redis.password}")
    // private String redisPassword;

    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        // Configure pool properties if needed (e.g., maxTotal, maxIdle)
        // poolConfig.setMaxTotal(128);
        // poolConfig.setMaxIdle(128);
        // poolConfig.setMinIdle(16);
        // poolConfig.setTestOnBorrow(true);
        // poolConfig.setTestOnReturn(true);
        // poolConfig.setTestWhileIdle(true);
        // poolConfig.setNumTestsPerEvictionRun(3);
        // poolConfig.setBlockWhenExhausted(true);
        poolConfig.setJmxEnabled(false);
        return new JedisPool(poolConfig, redisHost, redisPort);
    }
}