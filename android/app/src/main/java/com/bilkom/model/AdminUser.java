package com.bilkom.model;

import com.google.gson.annotations.SerializedName;

/**
 * Represents an administrative user with properties such as ID, email, 
 * active status, verification status, and role.
 * This class is used to manage and retrieve information about admin users.
 * 
 * @author Sıla Bozkurt
 */
public class AdminUser {
    @SerializedName("id")
    private long id;

    @SerializedName("email")
    private String email;

    @SerializedName("is_active")
    private boolean active;

    @SerializedName("is_verified")
    private boolean verified;

    @SerializedName("role")
    private String role;

    public long getId() { return id; }
    public String getEmail() { return email; }
    public boolean isActive() { return active; }
    public boolean isVerified() { return verified; }
    public String getRole() { return role; }
}