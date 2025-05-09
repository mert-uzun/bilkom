package com.bilkom.model;

import com.google.gson.annotations.SerializedName;

/**
 * Data model for club creation requests
 * 
 * @author Mert Uzun
 * @version 1.0
 * @since 2025-05-09
 */
public class ClubRequest {
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("presidentId")
    private Long presidentId;
    
    @SerializedName("advisorId")
    private Long advisorId;
    
    @SerializedName("advisorName")
    private String advisorName;
    
    @SerializedName("advisorEmail")
    private String advisorEmail;
    
    // CONSTRUCTORS
    public ClubRequest() {
    }
    
    public ClubRequest(String name, String description, Long presidentId) {
        this.name = name;
        this.description = description;
        this.presidentId = presidentId;
    }
    
    public ClubRequest(String name, String description, Long presidentId, Long advisorId, String advisorName, String advisorEmail) {
        this.name = name;
        this.description = description;
        this.presidentId = presidentId;
        this.advisorId = advisorId;
        this.advisorName = advisorName;
        this.advisorEmail = advisorEmail;
    }

    // GETTERS AND SETTERS
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

    public Long getPresidentId() {
        return presidentId;
    }

    public void setPresidentId(Long presidentId) {
        this.presidentId = presidentId;
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
} 