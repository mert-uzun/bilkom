package com.bilkom.android.network.models;

import com.google.gson.annotations.SerializedName;
import java.sql.Timestamp;

public class Club {
    @SerializedName("clubId")
    private Long clubId;
    
    @SerializedName("clubName")
    private String clubName;
    
    @SerializedName("clubDescription")
    private String clubDescription;
    
    @SerializedName("clubHead")
    private User clubHead;
    
    @SerializedName("createdAt")
    private Timestamp createdAt;
    
    @SerializedName("isActive")
    private boolean isActive;
    
    // Getters and Setters
    public Long getClubId() {
        return clubId;
    }
    
    public void setClubId(Long clubId) {
        this.clubId = clubId;
    }
    
    public String getClubName() {
        return clubName;
    }
    
    public void setClubName(String clubName) {
        this.clubName = clubName;
    }
    
    public String getClubDescription() {
        return clubDescription;
    }
    
    public void setClubDescription(String clubDescription) {
        this.clubDescription = clubDescription;
    }
    
    public User getClubHead() {
        return clubHead;
    }
    
    public void setClubHead(User clubHead) {
        this.clubHead = clubHead;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
} 