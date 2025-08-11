package ir.ac.kntu.abusafar.service.impl;

import ir.ac.kntu.abusafar.service.UserCacheService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Service
public class UserCacheServiceImpl implements UserCacheService {

    @Override
    @Caching(evict = {
            @CacheEvict(value = "users", key = "#userId"),
            @CacheEvict(value = "usersByEmail", key = "#email", condition = "#email != null"),
            @CacheEvict(value = "usersByPhoneNumber", key = "#phoneNumber", condition = "#phoneNumber != null")
    })
    public void evictUserCaches(Long userId, String email, String phoneNumber) {

    }
}