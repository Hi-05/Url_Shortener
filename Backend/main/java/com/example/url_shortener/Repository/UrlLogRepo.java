package com.example.url_shortener.Repository;

import com.example.url_shortener.Entity.UrlLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlLogRepo extends JpaRepository<UrlLog , Long> {
    void deleteByUrlId(long id);

    @Query("SELECT COUNT(l) FROM UrlLog l WHERE l.url.id = :urlId")
    long countByUrlId(@Param("urlId") Long urlId);
}
