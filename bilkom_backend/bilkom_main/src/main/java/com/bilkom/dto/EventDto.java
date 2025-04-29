package com.bilkom.dto;

import java.sql.Date;
import java.util.List;

import com.bilkom.entity.*;

/**
 * * Data Transfer Object for Event entity.
 * This class is used to transfer data between the client and server.
 * 
 * @author Elif Bozkurt
 */
public class EventDto {
    // FIELDS
    private String name;
    private String description;
    private String location;
    private Date eventDate;
    private int maxParticipants;
    private boolean isClubEvent;

    private Long clubId; 
    private List<String> tags; 

    // CONSTRUCTORS
    public EventDto() {}

    public EventDto(Event event) {
        this.name = event.getEventName();
        this.description = event.getEventDescription();
        this.location = event.getEventLocation();
        this.eventDate = event.getEventDate();
        this.maxParticipants = event.getMaxParticipants();
        this.tags = event.getTags().stream().map(Tag::getTagName).toList();
        this.clubId = (event.getClub() != null) ? event.getClub().getClubId() : null;
        this.isClubEvent = event.isClubEvent();
    }    

    // GETTERS AND SETTERS
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public Date getEventDate() { return eventDate; }
    public void setEventDate(Date eventDate) { this.eventDate = eventDate; }
    
    public int getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }
    
    public Long getClubId() { return clubId; }
    public void setClubId(Long clubId) { this.clubId = clubId; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public boolean isClubEvent() { return isClubEvent; }
    public void setIsClubEvent(boolean b) { this.isClubEvent = b; }    
}