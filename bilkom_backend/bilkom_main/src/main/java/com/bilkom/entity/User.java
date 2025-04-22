package com.bilkom.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.sql.Timestamp;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "bilkent_id", nullable = false, unique = true)
    private String bilkentId;

    @Column(name = "user_role", nullable = false)
    private String userRole;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "blood_type", nullable = false)
    private String bloodType;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "last_login", nullable = false, columnDefinition = "TIMESTAMP DEFAULT created_at")
    private Timestamp lastLogin;
}
