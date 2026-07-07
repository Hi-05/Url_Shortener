package com.example.url_shortener.Service;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public void cacheUrl(String shortCode, String longUrl, Long urlId) {
        redisTemplate.opsForHash().put(shortCode, "longUrl", longUrl);
        redisTemplate.opsForHash().put(shortCode, "id", String.valueOf(urlId));
        redisTemplate.expire(shortCode, 7, TimeUnit.DAYS);
    }

    public String getCachedLongUrl(String shortCode) {
        return (String) redisTemplate.opsForHash().get(shortCode, "longUrl");
    }

    public Long getCachedUrlId(String shortCode) {
        String id = (String) redisTemplate.opsForHash().get(shortCode, "id");
        return id != null ? Long.parseLong(id) : null;
    }

    public void evictUrl(String shortCode) {
        redisTemplate.delete(shortCode);
    }
}