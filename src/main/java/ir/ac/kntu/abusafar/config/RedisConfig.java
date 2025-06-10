package ir.ac.kntu.abusafar.config;

import okhttp3.OkHttpClient;
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

    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
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

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient();
    }
}