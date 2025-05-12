// this is a pojo class for the user 
// it is used to store the user data from the server using userId, email, passwordHash, firstName, 
// lastName, bilkentId, phoneNumber, bloodType, createdAt, isVerified, isActive, lastLogin and verificationToken    
package com.bilkom.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Data model for users, synchronized with the backend User entity.
 * Updated to properly match backend User entity.
 * 
 * @author Mert Uzun and Sıla Bozkurt
 * @version 1.0
 * @since 2025-05-11
 */
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
    
    @SerializedName("isVerified")
    private boolean isVerified;
    
    @SerializedName("isActive")
    private boolean isActive;
    
    @SerializedName("createdAt")
    private String createdAt;
    
    @SerializedName("lastLogin")
    private String lastLogin;
    
    @SerializedName("role")
    private String role;
    
    @SerializedName("emailNotificationsEnabled")
    private boolean emailNotificationsEnabled;
    
    @SerializedName("smsNotificationsEnabled")
    private boolean smsNotificationsEnabled;
    
    @SerializedName("profileVisibility")
    private String profileVisibility;
    
    @SerializedName("avatarPath")
    private String avatarPath;
    
    @SerializedName("clubMemberships")
    private List<ClubMember> clubMemberships;
    
    @SerializedName("fcmToken")
    private String fcmToken;

    public User() {}

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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public List<ClubMember> getClubMemberships() {
        return clubMemberships;
    }

    public void setClubMemberships(List<ClubMember> clubMemberships) {
        this.clubMemberships = clubMemberships;
    }
    
    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
    
    /**
     * Gets the full name of the user (first name + last name)
     * @return The full name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    /**
     * Get the avatar URL for this user
     * @return Full URL to the avatar image
     */
    public String getAvatarUrl() {
        if (avatarPath == null || avatarPath.isEmpty()) {
            return "https://bilkom-api.bilkent.edu.tr/api/assets/avatars/AVATAR_1.png";
        }
        return "https://bilkom-api.bilkent.edu.tr/api/assets/avatars/" + avatarPath + ".png";
    }
} 