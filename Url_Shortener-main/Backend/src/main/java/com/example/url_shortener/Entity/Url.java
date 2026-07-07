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
@Table(
        name = "url",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "long_url"})
)
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id ;

    @Column(unique = true , nullable = false)
    String code ;

    @Column(nullable = false)
    String longUrl ;

    String description ;

    LocalDateTime createdAt ;

    long clickCount ;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    Users user;
}
