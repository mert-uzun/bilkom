package com.bilkom.android.network.models;

import com.google.gson.annotations.SerializedName;
import java.sql.Date;

public class Event {
    @SerializedName("eventId")
    private Long eventId;
    
    @SerializedName("eventName")
    private String eventName;
    
    @SerializedName("eventDescription")
    private String eventDescription;
    
    @SerializedName("creator")
    private User creator;
    
    @SerializedName("club")
    private Club club;
    
    @SerializedName("isClubEvent")
    private boolean isClubEvent;
    
    @SerializedName("maxParticipants")
    private int maxParticipants;
    
    @SerializedName("currentParticipantsNumber")
    private int currentParticipantsNumber;
    
    @SerializedName("eventLocation")
    private String eventLocation;
    
    @SerializedName("eventDate")
    private Date eventDate;
    
    @SerializedName("isActive")
    private boolean isActive;
    
    // Getters and Setters
    public Long getEventId() {
        return eventId;
    }
    
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
    
    public String getEventName() {
        return eventName;
    }
    
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    
    public String getEventDescription() {
        return eventDescription;
    }
    
    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }
    
    public User getCreator() {
        return creator;
    }
    
    public void setCreator(User creator) {
        this.creator = creator;
    }
    
    public Club getClub() {
        return club;
    }
    
    public void setClub(Club club) {
        this.club = club;
    }
    
    public boolean isClubEvent() {
        return isClubEvent;
    }
    
    public void setClubEvent(boolean clubEvent) {
        isClubEvent = clubEvent;
    }
    
    public int getMaxParticipants() {
        return maxParticipants;
    }
    
    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }
    
    public int getCurrentParticipantsNumber() {
        return currentParticipantsNumber;
    }
    
    public void setCurrentParticipantsNumber(int currentParticipantsNumber) {
        this.currentParticipantsNumber = currentParticipantsNumber;
    }
    
    public String getEventLocation() {
        return eventLocation;
    }
    
    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }
    
    public Date getEventDate() {
        return eventDate;
    }
    
    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
} 