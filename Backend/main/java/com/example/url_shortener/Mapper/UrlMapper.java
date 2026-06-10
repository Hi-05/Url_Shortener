package com.example.url_shortener.Mapper;

import org.mapstruct.*;
import com.example.url_shortener.Entity.Url ;
import com.example.url_shortener.DTO.UrlDTO;

@Mapper(componentModel = "spring")
public interface UrlMapper {


    UrlDTO toDto(Url url) ;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "clickCount", ignore = true)
    Url toEntity(UrlDTO url) ;

}
