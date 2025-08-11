package ir.ac.kntu.abusafar.service.impl;

import ir.ac.kntu.abusafar.service.RedisReserveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
public class RedisReserveServiceImpl implements RedisReserveService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisReserveServiceImpl.class);

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public RedisReserveServiceImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void setKeyWithTTL(String key, String value, long timeToLiveInSeconds) {
        try {
            ValueOperations<String, String> ops = redisTemplate.opsForValue();
            ops.set(key, value, Duration.ofSeconds(timeToLiveInSeconds));
            LOGGER.info("Set Redis key: '{}' with TTL: {} seconds.", key, timeToLiveInSeconds);
        } catch (Exception e) {
            LOGGER.error("Failed to set key '{}' in Redis. Error: {}", key, e.getMessage(), e);
        }
    }

    @Override
    public void deleteKey(String key) {
        try {
            redisTemplate.delete(key);
            LOGGER.info("Deleted Redis key: '{}'", key);
        } catch (Exception e) {
            LOGGER.error("Failed to delete key '{}' from Redis. Error: {}", key, e.getMessage(), e);
        }
    }
}