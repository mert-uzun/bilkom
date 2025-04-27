package com.bilkom.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.bilkom.service.TokenBlacklistService;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.security.Key;

/**
 * Utility methods for JWT token generation and validation.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;
    
    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    /**
     * Generates a JWT token for the given user details.
     *
     * @param userDetails User details for whom to generate token
     * @return JWT token string
     */
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts username from the given JWT token.
     *
     * @param token Input JWT token string
     * @return Username extracted from token
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Validates token authenticity and checks expiration.
     * Also checks if the token has been blacklisted.
     *
     * @param token Input JWT token string
     * @return true if the token is valid and not blacklisted
     */
    public boolean validateToken(String token) {
        try {
            // First check if token is blacklisted
            if (tokenBlacklistService.isBlacklisted(token)) {
                logger.info("Token is blacklisted");
                return false;
            }
            
            // Then proceed with normal validation
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Blacklists a token.
     *
     * @param token The token to blacklist
     */
    public void blacklistToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
            
            long expiryTime = claims.getExpiration().getTime();
            tokenBlacklistService.blacklistToken(token, expiryTime);
            logger.info("Token blacklisted until {}", new Date(expiryTime));
        } catch (Exception e) {
            logger.error("Error blacklisting token: {}", e.getMessage());
        }
    }
    
    /**
     * Gets the expiration date from a JWT token.
     *
     * @param token Input JWT token string
     * @return The expiration date
     */
    public Date getExpirationDateFromToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getExpiration();
    }
    
    /**
     * Gets the signing key for JWT tokens.
     *
     * @return The signing key
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}