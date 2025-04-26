package com.bilkom.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;

/**
 * JPA configuration for the application.
 * Configures JPA auditing, naming strategy, and performance options.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaConfig {
    
    /**
     * Creates an AuditorAware implementation that provides the current user
     * for JPA auditing (created/modified by fields).
     * 
     * @return AuditorAware implementation that returns the current user's username
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.of("system");
            }
            
            Object principal = authentication.getPrincipal();
            
            if (principal instanceof UserDetails) {
                return Optional.of(((UserDetails) principal).getUsername());
            } else if (principal instanceof String) {
                return Optional.of((String) principal);
            }
            
            return Optional.of("anonymous");
        };
    }
    
    /**
     * Customizes Hibernate properties for optimal performance.
     * 
     * @return HibernatePropertiesCustomizer that sets performance-related properties
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer() {
        return hibernateProperties -> {
            // Enable second-level cache
            hibernateProperties.put("hibernate.cache.use_second_level_cache", "true");
            hibernateProperties.put("hibernate.cache.region.factory_class", "org.hibernate.cache.jcache.JCacheRegionFactory");
            
            // Batch size for more efficient queries
            hibernateProperties.put("hibernate.jdbc.batch_size", "50");
            hibernateProperties.put("hibernate.order_inserts", "true");
            hibernateProperties.put("hibernate.order_updates", "true");
            
            // Connection pool settings
            hibernateProperties.put("hibernate.connection.provider_class", "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");
            hibernateProperties.put("hibernate.hikari.minimumIdle", "5");
            hibernateProperties.put("hibernate.hikari.maximumPoolSize", "20");
            hibernateProperties.put("hibernate.hikari.idleTimeout", "30000");
            
            // Statement logging - currently enabled only for development environment
            hibernateProperties.put("hibernate.show_sql", "true");
            hibernateProperties.put("hibernate.format_sql", "true");
        };
    }
} 