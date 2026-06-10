package com.example.url_shortener.Service;


import com.example.url_shortener.Entity.Url;
import com.example.url_shortener.Entity.UrlLog;
import com.example.url_shortener.Repository.UrlLogRepo;
import com.example.url_shortener.Repository.UrlRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlLogServiceTest {

    @Mock
    private UrlLogRepo urlLogRepo;

    @Mock
    private UrlRepo urlRepo;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private UrlLogService urlLogService;

    @Test
    void createUrlLog_shouldReturnFromCache_whenCacheHit() {
        when(redisService.getCachedLongUrl("abc123")).thenReturn("https://google.com");
        when(redisService.getCachedUrlId("abc123")).thenReturn(1L);

        String result = urlLogService.createUrlLog("abc123");

        assertEquals("https://google.com", result);
        verify(urlLogRepo, times(1)).save(any(UrlLog.class));
        // Removed urlRepo.findByCode verification — not called on cache hit
    }

    // Test 2 — cache miss
    @Test
    void createUrlLog_shouldFetchFromDb_whenCacheMiss() {
        Url mockUrl = new Url();
        mockUrl.setId(1L);
        mockUrl.setLongUrl("https://google.com");

        when(redisService.getCachedLongUrl("abc123")).thenReturn(null);
        when(urlRepo.findByCode("abc123")).thenReturn(mockUrl);

        String result = urlLogService.createUrlLog("abc123");

        assertEquals("https://google.com", result);
        verify(redisService, times(1)).cacheUrl(any(), any(), any());
        verify(urlLogRepo, times(1)).save(any(UrlLog.class));
    }

    // Test 3 — URL not found
    @Test
    void createUrlLog_shouldThrowException_whenUrlNotFound() {
        when(redisService.getCachedLongUrl("invalid")).thenReturn(null);
        when(urlRepo.findByCode("invalid")).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> urlLogService.createUrlLog("invalid"));
    }

    // Test 4 — cache populated after miss
    @Test
    void createUrlLog_shouldPopulateCache_afterCacheMiss() {
        Url mockUrl = new Url();
        mockUrl.setId(1L);
        mockUrl.setLongUrl("https://google.com");

        when(redisService.getCachedLongUrl("abc123")).thenReturn(null);
        when(urlRepo.findByCode("abc123")).thenReturn(mockUrl);

        urlLogService.createUrlLog("abc123");

        verify(redisService, times(1))
                .cacheUrl("abc123", "https://google.com", 1L);
    }
}
