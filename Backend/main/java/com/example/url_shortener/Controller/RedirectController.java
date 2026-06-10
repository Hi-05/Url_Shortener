package com.example.url_shortener.Controller;

import com.example.url_shortener.Service.UrlLogService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@AllArgsConstructor
public class RedirectController {

    private final UrlLogService urlLogService ;

    @GetMapping("/{shortCode}")
    public void redirect(@PathVariable String shortCode, HttpServletResponse response) throws IOException {

        String longUrl = urlLogService.createUrlLog(shortCode);
                response.sendRedirect(longUrl);
    }
}
