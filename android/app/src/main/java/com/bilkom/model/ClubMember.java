package com.bilkom.model;

import com.google.gson.annotations.SerializedName;

/**
 * Data model for club members, matching with the backend ClubMember entity
 * 
 * @author Mert Uzun
 * @version 1.0
 * @since 2025-05-09
 */
public class ClubMember {
    @SerializedName("userId")
    private Long userId;
    
    @SerializedName("clubId")
    private Long clubId;
    
    @SerializedName("user")
    private User user;
    
    @SerializedName("joinDate")
    private String joinDate;
    
    @SerializedName("isActive")
    private boolean isActive;

    public ClubMember() {
    }

    public ClubMember(Long userId, Long clubId) {
        this.userId = userId;
        this.clubId = clubId;
        this.isActive = true;
    }

    // GETTERS AND SETTERS
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getClubId() {
        return clubId;
    }

    public void setClubId(Long clubId) {
        this.clubId = clubId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
} 