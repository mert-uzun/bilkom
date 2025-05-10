package com.bilkom.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception class for handling resource not found errors for backend
 * 
 * @author Mert Uzun
 * @version 1.0
 * @since 2025-05-09
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
    
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public NotFoundException(String entityName, Long id) {
        super(entityName + " with ID " + id + " not found");
    }
    
    public NotFoundException(String entityName, String identifier) {
        super(entityName + " with identifier " + identifier + " not found");
    }
} 