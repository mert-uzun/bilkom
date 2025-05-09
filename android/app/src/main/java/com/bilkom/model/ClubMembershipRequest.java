package com.bilkom.model;

import com.google.gson.annotations.SerializedName;

/**
 * Data model for club membership requests
 * Could be removed in the future
 * 
 * @author Mert Uzun
 * @version 1.0
 * @since 2025-05-09
 */
public class ClubMembershipRequest {
    @SerializedName("id")
    private Long id;
    
    @SerializedName("clubId")
    private Long clubId;
    
    @SerializedName("userId")
    private Long userId;
    
    @SerializedName("requestDate")
    private String requestDate;
    
    @SerializedName("status")
    private String status; // PENDING, APPROVED, REJECTED
    
    @SerializedName("user")
    private User user;
    
    @SerializedName("club")
    private Club club;

    public ClubMembershipRequest() {
    }

    public ClubMembershipRequest(Long clubId, Long userId) {
        this.clubId = clubId;
        this.userId = userId;
        this.status = "PENDING";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClubId() {
        return clubId;
    }

    public void setClubId(Long clubId) {
        this.clubId = clubId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }
} 