package com.bilkom.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures caching for the application
 * 
 * @author Mert Uzun
 * @version 1.0
 * @since 2025-05-09
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configures a simple cache manager for the application
     * Creates separate caches for different types of data
     * 
     * @return CacheManager instance
     * 
     * @author Mert Uzun
     * @version 1.0
     * @since 2025-05-09
     */
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager(
            "weatherCache",
            "eventsCache",
            "clubCache",
            "userCache",
            "newsCache"
        );
        return cacheManager;
    }
} 