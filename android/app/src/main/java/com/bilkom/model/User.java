// this is a pojo class for the user 
// it is used to store the user data from the server using userId, email, passwordHash, firstName, 
//lastName, bilkentId, phoneNumber, bloodType, createdAt, isVerified, isActive, lastLogin and verificationToken    
package com.bilkom.model;

import java.sql.Timestamp;
import java.util.List;

public class User {
    private Long userId;
    private String email;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private String bilkentId;
    private String phoneNumber;
    private String bloodType;
    private Timestamp createdAt;
    private boolean isVerified;
    private boolean isActive;
    private Timestamp lastLogin;
    private String verificationToken;

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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }
} 