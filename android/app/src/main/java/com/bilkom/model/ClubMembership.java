package com.bilkom.model;

import com.google.gson.annotations.SerializedName;

/**
 * Data model for club memberships (simplified version of ClubMember)
 * Aim of this class is to be backward compatible with our old implementation of ClubMember model
 * 
 * @author Mert Uzun
 * @version 1.0
 * @since 2025-05-09
 */
public class ClubMembership {
    @SerializedName("userId")
    private Long userId;
    
    @SerializedName("clubId")
    private Long clubId;
    
    @SerializedName("club")
    private Club club;
    
    @SerializedName("joinDate")
    private String joinDate;
    
    @SerializedName("isActive")
    private boolean isActive;

    public ClubMembership() {
    }

    public ClubMembership(Long userId, Long clubId) {
        this.userId = userId;
        this.clubId = clubId;
        this.isActive = true;
    }

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

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
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