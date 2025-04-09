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
    private Long userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String first_name;

    @Column(nullable = false)
    private String last_name;

    @Column(nullable = false, unique = true)
    private String bilkent_id;

    @Column(nullable = false)
    private String user_role;

    @Column(nullable = false, unique = true)
    private String phone_number;

    @Column(nullable = false)
    private String blood_type;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp created_at;

    @Column(nullable = false)
    private boolean is_verified;

    @Column(nullable = false)
    private boolean is_active;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT created_at")
    private Timestamp last_login;
}
