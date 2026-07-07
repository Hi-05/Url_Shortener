package com.example.url_shortener.Repository;

import com.example.url_shortener.Entity.Url;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlRepo extends JpaRepository<Url, Long> {
    List<Url> findAllByUser_id(Long id);

    Url findByCode(String code);



    boolean existsByCode(String uniqueCode);


    boolean existsByUserIdAndLongUrl(Long id, @NotBlank(message = "url cannot be empty") @URL(message = "Invalid URL format") String longUrl);

    Object findAllByUserId(long l);
}
