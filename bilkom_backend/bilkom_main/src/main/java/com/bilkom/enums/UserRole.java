package com.bilkom.enums;

/**
 * Enum for defining user roles in the system.
 * These roles are used for authorization and access control.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
public enum UserRole {
    USER,           // Regular user
    CLUB_EXECUTIVE, // User who is an executive in a club
    CLUB_HEAD,      // User who manages a club
    ADMIN;          // Administrator with full access
    
    /**
     * Returns the Spring Security role format for this role.
     * Spring Security typically expects roles to be prefixed with "ROLE_".
     * 
     * @return The Spring Security role representation
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public String getSpringSecurityRole() {
        return "ROLE_" + this.name();
    }
} 