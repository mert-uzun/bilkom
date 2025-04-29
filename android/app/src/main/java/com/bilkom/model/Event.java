package com.bilkom.model;

import java.util.List;

public class Event {
    private Long eventId;
    private String eventName;
    private String eventDescription;
    private int maxParticipants;
    private int currentParticipantsNumber;
    private String eventLocation;
    private String eventDate; // Use String for easier JSON mapping; parse as needed
    private boolean isActive;
    private List<String> tags;

    public Event() {}

    public Event(Long eventId, String eventName, String eventDescription, int maxParticipants, int currentParticipantsNumber, String eventLocation, String eventDate, boolean isActive, List<String> tags) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.maxParticipants = maxParticipants;
        this.currentParticipantsNumber = currentParticipantsNumber;
        this.eventLocation = eventLocation;
        this.eventDate = eventDate;
        this.isActive = isActive;
        this.tags = tags;
    }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public String getEventDescription() { return eventDescription; }
    public void setEventDescription(String eventDescription) { this.eventDescription = eventDescription; }

    public int getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }

    public int getCurrentParticipantsNumber() { return currentParticipantsNumber; }
    public void setCurrentParticipantsNumber(int currentParticipantsNumber) { this.currentParticipantsNumber = currentParticipantsNumber; }

    public String getEventLocation() { return eventLocation; }
    public void setEventLocation(String eventLocation) { this.eventLocation = eventLocation; }

    public String getEventDate() { return eventDate; }
    public void setEventDate(String eventDate) { this.eventDate = eventDate; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
} 