package com.saurabh.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.saurabh.Entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secretKeyString;  // inject as String first

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        //byte[] keyBytes = Decoders.BASE64.decode(secretKeyString);
        this.secretKey =  Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }

    private static final String ISSUER = "SAURABH KAWATRA";

    public boolean validateToken(String jwtToken) {
        return parseToken(jwtToken) != null;
    }

    private Claims parseToken(String jwtToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)  // secretKey should be of type SecretKey
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();

        } catch (JwtException e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    public String getUsernameFromToken(String jwtToken) {
        Claims claims = parseToken(jwtToken);
        return claims != null ? claims.getSubject() : null;
    }

    public String generateToken(User user) {  // no longer static
        Instant now = Instant.now();
        return Jwts.builder()
        		
                .setId(UUID.randomUUID().toString())
                .claim("role", "ROLE_" + user.getRole().name())
                .setIssuer(ISSUER)
                .setSubject(user.getEmail())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(jwtExpiration, ChronoUnit.MINUTES)))
                .signWith(secretKey)
                .compact();
    }
}