package com.bilkom.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Service for managing blacklisted JWT tokens.
 * We should implement a Redis-based blacklist service for production use. 
 * But we use in-memory map for now.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
@Service
public class TokenBlacklistService {
    
    // In-memory map to store blacklisted tokens and their expiry time
    private final Map<String, Long> blacklistedTokens = new ConcurrentHashMap<>();
    
    // Executor service for cleaning up expired tokens
    private final ScheduledExecutorService cleanupExecutor;
    
    /**
     * Constructor initializes the token cleanup task.
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public TokenBlacklistService() {
        cleanupExecutor = Executors.newSingleThreadScheduledExecutor();
        cleanupExecutor.scheduleAtFixedRate(this::cleanupExpiredTokens, 0, 1, TimeUnit.HOURS);
    }
    
    /**
     * Blacklists a JWT token until its expiry time.
     * 
     * @param token The token to blacklist
     * @param expiryTimeMillis The expiry time of the token in milliseconds since epoch
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public void blacklistToken(String token, long expiryTimeMillis) {
        blacklistedTokens.put(token, expiryTimeMillis);
    }
    
    /**
     * Checks if a token is blacklisted.
     * 
     * @param token The token to check
     * @return true if the token is blacklisted, false otherwise
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public boolean isBlacklisted(String token) {
        Long expiryTime = blacklistedTokens.get(token);
        if (expiryTime == null) {
            return false;
        }
        
        // Token is in the blacklist, check if it's expired
        if (System.currentTimeMillis() > expiryTime) {
            blacklistedTokens.remove(token);
            return false;
        }
        
        return true;
    }
    
    /**
     * Cleans up expired tokens from the blacklist.
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    private void cleanupExpiredTokens() {
        long currentTime = System.currentTimeMillis();
        blacklistedTokens.entrySet().removeIf(entry -> entry.getValue() < currentTime);
    }
} 