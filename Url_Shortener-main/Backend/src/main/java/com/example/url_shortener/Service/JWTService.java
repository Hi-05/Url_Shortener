package com.example.url_shortener.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

@Service
public class JWTService {

    @Value("${jwt.secret}")
    private String secretKey;

    // The old constructor with the KeyGenerator has been completely removed.
    // Spring will automatically use the default no-args constructor.

    public String generateToken(String username) {
        Map<String , Object> claims = new HashMap<>() ;

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                // 2. Fixed expiration math: 1000ms * 60s * 60m * 10h = 10 hours
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .and()
                .signWith(getKey())
                .compact() ;

    }

    private SecretKey getKey() {
        byte[] keyBites = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBites) ;
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
