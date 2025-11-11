package com.projects.tenantmanager.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisSessionService {

    private static final String SESSION_PREFIX = "session:";
    private static final String TOKEN_PREFIX = "token:";
    private static final long SESSION_TTL_HOURS = 10;

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisSessionService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void storeUserSession(String username, String token) {
        String sessionKey = SESSION_PREFIX + username;
        String tokenKey = TOKEN_PREFIX + token;

        // Store username -> token mapping
        redisTemplate.opsForValue().set(sessionKey, token, SESSION_TTL_HOURS, TimeUnit.HOURS);

        // Store token -> username mapping for quick validation
        redisTemplate.opsForValue().set(tokenKey, username, SESSION_TTL_HOURS, TimeUnit.HOURS);
    }

    public boolean isTokenValid(String token) {
        String tokenKey = TOKEN_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(tokenKey));
    }

    public String getUsernameFromToken(String token) {
        String tokenKey = TOKEN_PREFIX + token;
        Object username = redisTemplate.opsForValue().get(tokenKey);
        return username != null ? username.toString() : null;
    }

    public void invalidateSession(String username) {
        String sessionKey = SESSION_PREFIX + username;

        // Get the token associated with this username
        Object token = redisTemplate.opsForValue().get(sessionKey);

        if (token != null) {
            String tokenKey = TOKEN_PREFIX + token.toString();
            // Delete both session and token keys
            redisTemplate.delete(sessionKey);
            redisTemplate.delete(tokenKey);
        }
    }

    public void extendSession(String token) {
        String tokenKey = TOKEN_PREFIX + token;
        String username = getUsernameFromToken(token);

        if (username != null) {
            String sessionKey = SESSION_PREFIX + username;

            // Extend TTL for both keys
            redisTemplate.expire(sessionKey, SESSION_TTL_HOURS, TimeUnit.HOURS);
            redisTemplate.expire(tokenKey, SESSION_TTL_HOURS, TimeUnit.HOURS);
        }
    }
}
