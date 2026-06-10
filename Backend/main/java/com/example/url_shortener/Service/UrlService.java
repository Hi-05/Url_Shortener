package com.example.url_shortener.Service;

import com.example.url_shortener.DTO.UrlDTO;
import com.example.url_shortener.Entity.Url;
import com.example.url_shortener.Entity.Users;
import com.example.url_shortener.Mapper.UrlMapper;
import com.example.url_shortener.Repository.UrlLogRepo;
import com.example.url_shortener.Repository.UrlRepo;
import com.example.url_shortener.Repository.UsersRepo;
import com.example.url_shortener.Util.Base62Util;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor

public class UrlService {

    private Base62Util base62Util ;
    private UrlMapper urlMapper ;
    private UsersRepo usersRepo ;
    private UrlRepo urlRepo ;
    private UrlLogRepo urlLogRepo ;
    private RedisService redisService ;

    private Users getAuthenticatedUser(){

        String username = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName() ;
        Users user = usersRepo.findByUsername(username);

        if (user == null) {
            throw new RuntimeException("Authenticated user not found in database");
        }
        return user;
    }

    @Transactional
    public UrlDTO createUrl(UrlDTO urlDTO){

        Users currentUser = getAuthenticatedUser() ;

        if(urlRepo.existsByUserIdAndLongUrl(currentUser.getId() , urlDTO.getLongUrl())){

            throw new RuntimeException("Error : Url already exists") ;
        }

        String uniqueCode = base62Util.generateShortCode() ;
        int retries = 0;

        while(urlRepo.existsByCode(uniqueCode)){

            retries++;

            if(retries > 5){
                throw new RuntimeException("Exceeded retry limit") ;
            }

            uniqueCode = base62Util.generateShortCode() ;
        }

        Url urlToCreate = urlMapper.toEntity(urlDTO) ;
        urlToCreate.setCode(uniqueCode);
        urlToCreate.setUser(currentUser);
        urlToCreate.setCreatedAt(LocalDateTime.now());

        urlRepo.save(urlToCreate) ;

        redisService.cacheUrl(uniqueCode , urlToCreate.getLongUrl() , urlToCreate.getId());
        return urlMapper.toDto(urlToCreate) ;
    }

    public List<UrlDTO> getAllUrls(){

        Users currentUser = getAuthenticatedUser() ;

        List<Url> urls = urlRepo.findAllByUser_id(currentUser.getId()) ;

        return urls.stream().map(url -> {
            UrlDTO dto = urlMapper.toDto(url);
            dto.setClickCount(urlLogRepo.countByUrlId(url.getId()));
            return dto;
        }).toList();
    }

    @Transactional
    public UrlDTO updateUrl( String shortCode ,UrlDTO urlDTO){

        Users currentUser = getAuthenticatedUser() ;

        Url existingUrl = urlRepo.findByCode(shortCode) ;
        if (existingUrl == null) {
            throw new IllegalArgumentException("URL not found");
        }

        if(!existingUrl.getUser().getId().equals(currentUser.getId())){
            throw new RuntimeException("Unauthorized : You don't have access to this URL") ;
        }

        redisService.evictUrl(shortCode);

        existingUrl.setLongUrl(urlDTO.getLongUrl());
        existingUrl.setDescription(urlDTO.getDescription());

        Url updatedUrl =  urlRepo.save(existingUrl) ;
        return urlMapper.toDto(updatedUrl) ;
    }

    @Transactional
    public void deleteUrl(String shortCode) {

        Users currentUser = getAuthenticatedUser();

        Url urlToDelete = urlRepo.findByCode(shortCode);

        if (urlToDelete == null) {
            throw new IllegalArgumentException("URL not found");
        }

        if (!urlToDelete.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized : You don't have access to this URL");
        }

        // Delete logs first before deleting URL
        urlLogRepo.deleteByUrlId(urlToDelete.getId());

        // Delete cache (evict)
        redisService.evictUrl(shortCode);

        urlRepo.delete(urlToDelete);
    }
}
