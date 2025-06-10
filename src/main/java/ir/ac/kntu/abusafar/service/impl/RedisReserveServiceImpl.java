package ir.ac.kntu.abusafar.service.impl;

import ir.ac.kntu.abusafar.service.RedisReserveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

@Service
public class RedisReserveServiceImpl implements RedisReserveService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisReserveServiceImpl.class);

    private final JedisPool jedisPool;

    @Autowired
    public RedisReserveServiceImpl(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    public void setKeyWithTTL(String key, String value, long timeToLiveInSeconds) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(key, timeToLiveInSeconds, value);
            LOGGER.info("Set Redis key: '{}' with TTL: {} seconds.", key, timeToLiveInSeconds);
        } catch (JedisException e) {
            LOGGER.error("Failed to set key '{}' in Redis. Error: {}", key, e.getMessage(), e);
            // In a real-world scenario, you might have a fallback or alert mechanism here.
        }
    }

    @Override
    public void deleteKey(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
            LOGGER.info("Deleted Redis key: '{}'", key);
        } catch (JedisException e) {
            LOGGER.error("Failed to delete key '{}' from Redis. Error: {}", key, e.getMessage(), e);
        }
    }
}