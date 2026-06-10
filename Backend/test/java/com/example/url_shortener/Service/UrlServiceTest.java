package com.example.url_shortener.Service;

import com.example.url_shortener.DTO.UrlDTO;
import com.example.url_shortener.Entity.Url;
import com.example.url_shortener.Entity.Users;
import com.example.url_shortener.Mapper.UrlMapper;
import com.example.url_shortener.Repository.UrlLogRepo;
import com.example.url_shortener.Repository.UrlRepo;
import com.example.url_shortener.Repository.UsersRepo;
import com.example.url_shortener.Util.Base62Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepo urlRepo;

    @Mock
    private UrlLogRepo urlLogRepo;

    @Mock
    private UsersRepo usersRepo;

    @Mock
    private Base62Util base62Util;

    @Mock
    private UrlMapper urlMapper;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private UrlService urlService;

    private Users mockUser;
    private UrlDTO mockUrlDTO;
    private Url mockUrl;

    @BeforeEach
    void setUp() {
        // Mock SecurityContext so getAuthenticatedUser() works
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);

        // Setup common mock objects
        mockUser = new Users();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");

        mockUrlDTO = new UrlDTO();
        mockUrlDTO.setLongUrl("https://google.com");
        mockUrlDTO.setDescription("Google");

        mockUrl = new Url();
        mockUrl.setId(1L);
        mockUrl.setCode("abc123");
        mockUrl.setLongUrl("https://google.com");
        mockUrl.setUser(mockUser);
    }

    // Test 1 — createUrl success
    @Test
    void createUrl_shouldCreateSuccessfully_whenUrlIsNew() {
        when(usersRepo.findByUsername("testuser")).thenReturn(mockUser);
        when(urlRepo.existsByUserIdAndLongUrl(1L, "https://google.com")).thenReturn(false);
        when(base62Util.generateShortCode()).thenReturn("abc123");
        when(urlRepo.existsByCode("abc123")).thenReturn(false);
        when(urlMapper.toEntity(mockUrlDTO)).thenReturn(mockUrl);
        when(urlRepo.save(any())).thenReturn(mockUrl);
        when(urlMapper.toDto(mockUrl)).thenReturn(mockUrlDTO);

        UrlDTO result = urlService.createUrl(mockUrlDTO);

        assertNotNull(result);
        verify(urlRepo, times(1)).save(any());
        verify(redisService, times(1)).cacheUrl(any(), any(), any());
    }

    // Test 2 — createUrl duplicate
    @Test
    void createUrl_shouldThrowException_whenUrlAlreadyExists() {
        when(usersRepo.findByUsername("testuser")).thenReturn(mockUser);
        when(urlRepo.existsByUserIdAndLongUrl(1L, "https://google.com")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> urlService.createUrl(mockUrlDTO));
        verify(urlRepo, never()).save(any());
    }

    // Test 3 — createUrl collision handling
    @Test
    void createUrl_shouldRetry_whenShortCodeCollisionOccurs() {
        when(usersRepo.findByUsername("testuser")).thenReturn(mockUser);
        when(urlRepo.existsByUserIdAndLongUrl(1L, "https://google.com")).thenReturn(false);
        when(base62Util.generateShortCode())
                .thenReturn("collision1")
                .thenReturn("abc123");
        when(urlRepo.existsByCode("collision1")).thenReturn(true);
        when(urlRepo.existsByCode("abc123")).thenReturn(false);
        when(urlMapper.toEntity(mockUrlDTO)).thenReturn(mockUrl);
        when(urlRepo.save(any())).thenReturn(mockUrl);
        when(urlMapper.toDto(mockUrl)).thenReturn(mockUrlDTO);

        UrlDTO result = urlService.createUrl(mockUrlDTO);

        assertNotNull(result);
        verify(base62Util, times(2)).generateShortCode();
    }

    // Test 4 — createUrl exceeds retry limit
    @Test
    void createUrl_shouldThrowException_whenRetryLimitExceeded() {
        when(usersRepo.findByUsername("testuser")).thenReturn(mockUser);
        when(urlRepo.existsByUserIdAndLongUrl(1L, "https://google.com")).thenReturn(false);
        when(base62Util.generateShortCode()).thenReturn("collision");
        when(urlRepo.existsByCode("collision")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> urlService.createUrl(mockUrlDTO));
    }

    // Test 5 — getAllUrls
    @Test
    void getAllUrls_shouldReturnUserUrls() {
        when(usersRepo.findByUsername("testuser")).thenReturn(mockUser);
        when(urlRepo.findAllByUser_id(1L)).thenReturn(List.of(mockUrl));
        when(urlMapper.toDto(mockUrl)).thenReturn(mockUrlDTO);
        when(urlLogRepo.countByUrlId(1L)).thenReturn(2L);

        List<UrlDTO> result = urlService.getAllUrls();

        assertEquals(1, result.size());
    }

    // Test 6 — updateUrl not found
    @Test
    void updateUrl_shouldThrowException_whenUrlNotFound() {
        when(usersRepo.findByUsername("testuser")).thenReturn(mockUser);
        when(urlRepo.findByCode("abc123")).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> urlService.updateUrl("abc123", mockUrlDTO));
    }

    // Test 7 — updateUrl unauthorized
    @Test
    void updateUrl_shouldThrowException_whenUserNotOwner() {
        Users otherUser = new Users();
        otherUser.setId(2L);
        mockUrl.setUser(otherUser);

        when(usersRepo.findByUsername("testuser")).thenReturn(mockUser);
        when(urlRepo.findByCode("abc123")).thenReturn(mockUrl);

        assertThrows(RuntimeException.class,
                () -> urlService.updateUrl("abc123", mockUrlDTO));
    }

    // Test 8 — deleteUrl unauthorized
    @Test
    void deleteUrl_shouldThrowException_whenUserNotOwner() {
        Users otherUser = new Users();
        otherUser.setId(2L);
        mockUrl.setUser(otherUser);

        when(usersRepo.findByUsername("testuser")).thenReturn(mockUser);
        when(urlRepo.findByCode("abc123")).thenReturn(mockUrl);

        assertThrows(RuntimeException.class,
                () -> urlService.deleteUrl("abc123"));
    }
}
