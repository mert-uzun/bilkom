package com.bilkom.model;

import java.util.List;

public class EventRequest {
    private String eventName;
    private String eventDescription;
    private String eventLocation;
    private String eventDate;
    private int maxParticipants;
    private List<String> tags;

    public EventRequest(String eventName, String eventDescription, String eventLocation, String eventDate, int maxParticipants, List<String> tags) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventLocation = eventLocation;
        this.eventDate = eventDate;
        this.maxParticipants = maxParticipants;
        this.tags = tags;
    }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public String getEventDescription() { return eventDescription; }
    public void setEventDescription(String eventDescription) { this.eventDescription = eventDescription; }

    public String getEventLocation() { return eventLocation; }
    public void setEventLocation(String eventLocation) { this.eventLocation = eventLocation; }

    public String getEventDate() { return eventDate; }
    public void setEventDate(String eventDate) { this.eventDate = eventDate; }

    public int getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
} 