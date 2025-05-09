package com.bilkom.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Data model for clubs, matching with the backend Club entity
 * 
 * @author Mert Uzun
 * @version 1.0
 * @since 2025-05-09
 */
public class Club {
    @SerializedName("id")
    private Long id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("creationDate")
    private String creationDate;
    
    @SerializedName("isActive")
    private boolean isActive;
    
    @SerializedName("presidentId")
    private Long presidentId;
    
    @SerializedName("president")
    private User president;
    
    @SerializedName("advisorId")
    private Long advisorId;
    
    @SerializedName("advisorName")
    private String advisorName;
    
    @SerializedName("advisorEmail")
    private String advisorEmail;
    
    @SerializedName("memberCount")
    private int memberCount;
    
    @SerializedName("clubEvents")
    private List<Event> clubEvents;

    public Club() {
    }

    // GETTERS AND SETTERS

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Long getPresidentId() {
        return presidentId;
    }

    public void setPresidentId(Long presidentId) {
        this.presidentId = presidentId;
    }

    public User getPresident() {
        return president;
    }

    public void setPresident(User president) {
        this.president = president;
    }

    public Long getAdvisorId() {
        return advisorId;
    }

    public void setAdvisorId(Long advisorId) {
        this.advisorId = advisorId;
    }

    public String getAdvisorName() {
        return advisorName;
    }

    public void setAdvisorName(String advisorName) {
        this.advisorName = advisorName;
    }

    public String getAdvisorEmail() {
        return advisorEmail;
    }

    public void setAdvisorEmail(String advisorEmail) {
        this.advisorEmail = advisorEmail;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public List<Event> getClubEvents() {
        return clubEvents;
    }

    public void setClubEvents(List<Event> clubEvents) {
        this.clubEvents = clubEvents;
    }
} 