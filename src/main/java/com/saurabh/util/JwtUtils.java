package com.saurabh.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.saurabh.Entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secretKeyString;  // inject as String first

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private SecretKey secretKey;
    
    private final org.springframework.data.redis.core.StringRedisTemplate redisTemplate;
    
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    private static final String BLACKLIST_PREFIX = "jwt_blacklist:";

    public JwtUtils(org.springframework.data.redis.core.StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void init() {
        //byte[] keyBytes = Decoders.BASE64.decode(secretKeyString);
        this.secretKey =  Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }

    private static final String ISSUER = "SAURABH KAWATRA";

    // FIX 1: validateToken should NEVER throw — return false on any bad token.
    // Before: catch(JwtException e) { throw new RuntimeException(...) }
    // That caused a 500 on every expired/invalid token instead of 401.
    public boolean validateToken(String jwtToken) {
        return parseToken(jwtToken) != null;
    }
 
    // FIX 1 (continued): Each exception type is logged separately so you can
    // distinguish expired tokens from tampered ones in your logs.
    private Claims parseToken(String jwtToken) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(jwtToken)
                    .getPayload();
 
        } catch (ExpiredJwtException e) {
            logger.warn("JWT token expired: {}", e.getMessage());
        } catch (SignatureException e) {
            logger.warn("JWT signature invalid: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.warn("JWT malformed: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.warn("JWT unsupported: {}", e.getMessage());
        } catch (JwtException e) {
            logger.warn("JWT error: {}", e.getMessage());
        }
        return null;
    }

    public String getUsernameFromToken(String jwtToken) {
        Claims claims = parseToken(jwtToken);
        return claims != null ? claims.getSubject() : null;
    }

    public void blacklistToken(String jwtToken) {
        if (jwtToken == null || jwtToken.isEmpty()) return;
        try {
            Claims claims = parseToken(jwtToken);
            long remainingMs = jwtExpiration;
            if (claims != null && claims.getExpiration() != null) {
                long diff = claims.getExpiration().getTime() - System.currentTimeMillis();
                if (diff > 0) remainingMs = diff;
            }
            if (redisTemplate != null) {
                redisTemplate.opsForValue().set(BLACKLIST_PREFIX + jwtToken, "true", remainingMs, java.util.concurrent.TimeUnit.MILLISECONDS);
                logger.info("Blacklisted JWT token in Redis for remaining {} ms", remainingMs);
            }
        } catch (Exception e) {
            logger.warn("Could not blacklist token in Redis: {}", e.getMessage());
        }
    }

    public boolean isTokenBlacklisted(String jwtToken) {
        if (jwtToken == null || jwtToken.isEmpty()) return false;
        try {
            if (redisTemplate != null) {
                Boolean isBlacklisted = redisTemplate.hasKey(BLACKLIST_PREFIX + jwtToken);
                return Boolean.TRUE.equals(isBlacklisted);
            }
        } catch (Exception e) {
            logger.warn("Redis error checking blacklisted token: {}", e.getMessage());
        }
        return false;
    }

    public String generateToken(User user) {  // no longer static
        Instant now = Instant.now();
        return Jwts.builder()   
                .id(UUID.randomUUID().toString())
                .claim("role", "ROLE_" + user.getRole().name())
                .issuer(ISSUER)
                .subject(user.getEmail())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(jwtExpiration, ChronoUnit.MILLIS)))
                .signWith(secretKey)
                .compact();
    }
}