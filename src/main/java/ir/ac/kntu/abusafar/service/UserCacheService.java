package ir.ac.kntu.abusafar.service;

public interface UserCacheService {
    void evictUserCaches(Long userId, String email, String phoneNumber);
}