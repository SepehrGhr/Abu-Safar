package ir.ac.kntu.abusafar.service;

public interface RedisReserveService {
    void setKeyWithTTL(String key, String value, long timeToLiveInSeconds);
    void deleteKey(String key);
}
