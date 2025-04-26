package com.bilkom.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.lang.NonNull;

/**
 * WebSocket configuration class for the application.
 * Enables and configures STOMP messaging over WebSocket.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${websocket.endpoint:/ws}")
    private String websocketEndpoint;

    @Value("${websocket.allowed-origins:http://localhost:3000,http://localhost:8080}")
    private String allowedOrigins;

    /**
     * Configures the message broker for WebSocket communication.
     * 
     * @param registry the message broker registry
     */
    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry registry) {
        // Enable simple in-memory message broker for these destination prefixes
        registry.enableSimpleBroker("/topic", "/queue");
        
        // Set prefix for messages bound for @MessageMapping methods
        registry.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Registers STOMP endpoints for WebSocket communication.
     * 
     * @param registry the STOMP endpoint registry
     */
    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        registry.addEndpoint(websocketEndpoint)
                .setAllowedOrigins(allowedOrigins.split(","))
                .withSockJS();
    }
} 