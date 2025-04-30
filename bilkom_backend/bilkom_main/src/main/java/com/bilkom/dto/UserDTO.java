package com.bilkom.dto;

import com.bilkom.entity.User;
import com.bilkom.enums.UserRole;
import java.sql.Timestamp;

/**
 * DTO for User entity.
 * @author Mert Uzun
 * @version 1.0.0
 */
public class UserDTO {
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String bilkentId;
    private String phoneNumber;
    private String bloodType;
    private boolean isVerified;
    private boolean isActive;
    private Timestamp createdAt;
    private Timestamp lastLogin;
    private UserRole role;
    private boolean emailNotificationsEnabled;
    private boolean smsNotificationsEnabled;
    private String profileVisibility;
    private String avatarName;
    private String avatarPath;

    /**
     * Constructor for UserDTO.
     * @param user User entity.
     * @author Mert Uzun
     * @version 1.0.0
     */
    public UserDTO(User user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.bilkentId = user.getBilkentId();
        this.phoneNumber = user.getPhoneNumber();
        this.bloodType = user.getBloodType();
        this.createdAt = user.getCreatedAt();
        this.lastLogin = user.getLastLogin();
        this.isVerified = user.isVerified();
        this.isActive = user.isActive();
        this.role = user.getRole();
        this.emailNotificationsEnabled = user.isEmailNotificationsEnabled();
        this.smsNotificationsEnabled = user.isSmsNotificationsEnabled();
        this.profileVisibility = user.getProfileVisibility().name();
        this.avatarName = user.getAvatarPath().name();
        this.avatarPath = user.getAvatarPath().getPath();
    }

    // GETTERS AND SETTERS
    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getBilkentId() {
        return bilkentId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getBloodType() {
        return bloodType;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public boolean isActive() {
        return isActive;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public UserRole getRole() {
        return role;
    }

    public boolean isEmailNotificationsEnabled() {
        return emailNotificationsEnabled;
    }

    public void setEmailNotificationsEnabled(boolean emailNotificationsEnabled) {
        this.emailNotificationsEnabled = emailNotificationsEnabled;
    }

    public boolean isSmsNotificationsEnabled() {
        return smsNotificationsEnabled;
    }

    public void setSmsNotificationsEnabled(boolean smsNotificationsEnabled) {
        this.smsNotificationsEnabled = smsNotificationsEnabled;
    }

    public String getProfileVisibility() {
        return profileVisibility;
    }

    public void setProfileVisibility(String profileVisibility) {
        this.profileVisibility = profileVisibility;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setBilkentId(String bilkentId) {
        this.bilkentId = bilkentId;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public void setIsVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    public void setRole(UserRole role) {
        this.role = role;
    }
    
    public String getAvatarName() {
        return avatarName;
    }
    
    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }
    
    public String getAvatarPath() {
        return avatarPath;
    }
    
    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }
}
