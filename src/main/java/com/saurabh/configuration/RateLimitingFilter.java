package com.saurabh.configuration;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);
    private static final String RATE_LIMIT_PREFIX = "rate_limit:auth:";

    @Value("${rate.limit.auth.per.minute:30}")
    private int maxRequestsPerMinute;

    public static Logger getLogger() {
        return logger;
    }

    private final StringRedisTemplate redisTemplate;
    private final Map<String, Bucket> fallbackCache = new ConcurrentHashMap<>();

    public RateLimitingFilter(@NonNull StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(maxRequestsPerMinute)
                .refillIntervally(maxRequestsPerMinute, Duration.ofMinutes(1))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    @Override
    protected void doFilterInternal(@SuppressWarnings("null") HttpServletRequest request, @SuppressWarnings("null") HttpServletResponse response,
            @SuppressWarnings("null") FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.startsWith("/api/v1/auth/")) {
            String clientIp = getClientIP(request);
            boolean allowed = checkRateLimit(clientIp);

            if (!allowed) {
                response.setStatus(429);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\": \"Too many requests. Please wait 1 minute before trying again.\", \"authStatus\": \"TOO_MANY_REQUESTS\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean checkRateLimit(String clientIp) {
        try {
            if (redisTemplate != null) {
                String key = RATE_LIMIT_PREFIX + clientIp;
                Long count = redisTemplate.opsForValue().increment(key);
                if (count != null && count == 1) {
                    redisTemplate.expire(key, 1, TimeUnit.MINUTES);
                } else if (count != null && count > 1) {
                    Long ttl = redisTemplate.getExpire(key);
                    if (ttl != null && ttl < 0) {
                        redisTemplate.expire(key, 1, TimeUnit.MINUTES);
                    }
                }
                return count != null && count <= maxRequestsPerMinute;
            }
        } catch (Exception e) {
            logger.warn("Redis unavailable for rate limiting, falling back to local bucket: {}", e.getMessage());
        }

        // Bound memory footprint on fallback
        if (fallbackCache.size() > 5000) {
            fallbackCache.clear();
        }

        // Fallback to local Bucket4j
        Bucket bucket = fallbackCache.computeIfAbsent(clientIp, k -> createNewBucket());
        return bucket.tryConsume(1);
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}
