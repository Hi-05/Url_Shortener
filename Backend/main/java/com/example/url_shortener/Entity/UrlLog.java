package com.example.url_shortener.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UrlLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id ;

    @Column(nullable = false)
    LocalDateTime accessedAt;

    @ManyToOne
    @JoinColumn(name = "url_id", nullable = false)
    Url url ;

}
