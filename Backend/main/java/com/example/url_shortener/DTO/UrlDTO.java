package com.example.url_shortener.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UrlDTO {

    String code ;

    @NotBlank(message = "url cannot be empty")
    String longUrl ;

    String description ;

    LocalDateTime createdAt;

    Long clickCount;
}

