package com.example.url_shortener.Controller;

import com.example.url_shortener.DTO.UrlDTO;
import com.example.url_shortener.Service.UrlService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/urls")
public class UrlController {

    private UrlService urlService ;

    @PostMapping()
    public UrlDTO createUrl(@RequestBody @Valid UrlDTO urlDTO){

        return urlService.createUrl(urlDTO) ;
    }

    @GetMapping()
    public List<UrlDTO> getAllUrls(){

        return urlService.getAllUrls() ;
    }

    @PutMapping("/{shortCode}")
    public UrlDTO updateUrl(@PathVariable String shortCode ,@RequestBody @Valid UrlDTO urlDTO){

        return urlService.updateUrl(shortCode , urlDTO) ;
    }

    @DeleteMapping("/{shortCode}")
    public void deleteUrl(@PathVariable String shortCode){

        urlService.deleteUrl(shortCode) ;
    }

}
