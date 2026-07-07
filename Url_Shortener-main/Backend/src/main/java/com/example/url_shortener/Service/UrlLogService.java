package com.example.url_shortener.Service;

import com.example.url_shortener.Entity.Url;
import com.example.url_shortener.Entity.UrlLog;
import com.example.url_shortener.Repository.UrlLogRepo;
import com.example.url_shortener.Repository.UrlRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class UrlLogService {

    private final UrlLogRepo urlLogRepo ;
    private final UrlRepo urlRepo ;
    private final RedisService redisService ;

    @Transactional
    public String createUrlLog(String shortcode){

        String cachedLongUrl = redisService.getCachedLongUrl(shortcode);
        Long cachedUrlId = redisService.getCachedUrlId(shortcode);

        if(cachedUrlId != null && cachedLongUrl != null){

            UrlLog urlLog = new UrlLog() ;
            urlLog.setAccessedAt(LocalDateTime.now());
            Url urlRef = new Url() ;
            urlRef.setId(cachedUrlId);
            urlLog.setUrl(urlRef);
            urlLogRepo.save(urlLog) ;

            return cachedLongUrl ;
        }

        Url url = urlRepo.findByCode(shortcode) ;

        if(url == null){
            throw new IllegalArgumentException("Url does not exist.");
        }

        redisService.cacheUrl(shortcode , url.getLongUrl() , url.getId());
        UrlLog urlLog = new UrlLog() ;
        urlLog.setAccessedAt(LocalDateTime.now());
        urlLog.setUrl(url);

        urlLogRepo.save(urlLog) ;

        return url.getLongUrl();
    }
}
