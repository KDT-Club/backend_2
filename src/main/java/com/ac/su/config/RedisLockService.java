package com.ac.su.config;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisLockService {

    private final StringRedisTemplate redisTemplate;

    public RedisLockService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean acquireLock(String key, long expireTime) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, "locked", expireTime, TimeUnit.SECONDS);
        return success != null && success;
    }

    public void releaseLock(String key) {
        redisTemplate.delete(key);
    }
}
