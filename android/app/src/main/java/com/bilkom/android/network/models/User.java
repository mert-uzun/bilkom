package com.bilkom.android.network.models;

import com.google.gson.annotations.SerializedName;
import java.sql.Timestamp;

public class User {
    @SerializedName("userId")
    private Long userId;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("firstName")
    private String firstName;
    
    @SerializedName("lastName")
    private String lastName;
    
    @SerializedName("bilkentId")
    private String bilkentId;
    
    @SerializedName("phoneNumber")
    private String phoneNumber;
    
    @SerializedName("bloodType")
    private String bloodType;
    
    @SerializedName("createdAt")
    private Timestamp createdAt;
    
    @SerializedName("isVerified")
    private boolean isVerified;
    
    @SerializedName("isActive")
    private boolean isActive;
    
    @SerializedName("lastLogin")
    private Timestamp lastLogin;
    
    // Getters and Setters
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getBilkentId() {
        return bilkentId;
    }
    
    public void setBilkentId(String bilkentId) {
        this.bilkentId = bilkentId;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getBloodType() {
        return bloodType;
    }
    
    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public boolean isVerified() {
        return isVerified;
    }
    
    public void setVerified(boolean verified) {
        isVerified = verified;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public Timestamp getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }
} 