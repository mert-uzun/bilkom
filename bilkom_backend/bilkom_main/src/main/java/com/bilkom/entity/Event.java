package com.bilkom.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import jakarta.persistence.CascadeType;

/**
 * Event is an entity class representing an event in the system.
 * It contains information about the event such as name, description, location, date, and participants.
 * The class also includes methods to manage tags and participants associated with the event.
 *
 * @author Elif Bozkurt
 * @version 1.0
 */
@Entity
@Table(name = "events")
public class Event {
    // FIELDS
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id", nullable = false, columnDefinition = "BIGINT")
    private Long eventId;

    @Column(name = "event_name", nullable = false, columnDefinition = "VARCHAR(255)")
    private String eventName;

    @Column(name = "event_description", nullable = false, columnDefinition = "TEXT")
    private String eventDescription;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false, columnDefinition = "BIGINT")
    private User creator;

    @ManyToOne
    @JoinColumn(name = "club_id", nullable = true)
    @JsonIgnore
    private Club club;

    @Column(name = "is_club_event", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isClubEvent;

    @Column(name = "max_participants", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int maxParticipants;

    @Column(name = "current_participants_number", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int currentParticipantsNumber = 0;

    @Column(name = "event_location", nullable = false, columnDefinition = "VARCHAR(255)")
    private String eventLocation;
    
    @Column(name = "event_date", nullable = false, columnDefinition = "DATE")
    private Date eventDate;

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean isActive;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventParticipant> participants = new ArrayList<>();

    @Override
    public String toString() {
        System.out.println("Calling Event.toString() on eventId: " + eventId);
        return "Event{" +
            "eventId=" + eventId +
            ", eventName=" + eventName +
            ", maxParticipants=" + maxParticipants +
            ", tagCount=" + (tags != null ? tags.size() : 0) +
            ", participantCount=" + (participants != null ? participants.size() : 0) +
            '}';
    }

    // GETTERS AND SETTERS
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    
    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    
    public String getEventDescription() { return eventDescription; }
    public void setEventDescription(String eventDescription) { this.eventDescription = eventDescription; }
    
    public User getCreator() { return creator; }
    public void setCreator(User creator) { this.creator = creator; }
    
    public Club getClub() { return club; }
    public void setClub(Club club) { this.club = club; }
    
    public boolean isClubEvent() { return isClubEvent; }
    public void setIsClubEvent(boolean isClubEvent) { this.isClubEvent = isClubEvent; }
    
    public int getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }
    
    public int getCurrentParticipantsNumber() { return currentParticipantsNumber; }
    public void setCurrentParticipantsNumber(int currentParticipants) { this.currentParticipantsNumber = currentParticipants; }
    
    public String getEventLocation() { return eventLocation; }
    public void setEventLocation(String eventLocation) { this.eventLocation = eventLocation; }
    
    public Date getEventDate() { return eventDate; }
    public void setEventDate(Date eventDate) { this.eventDate = eventDate; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean isActive) { this.isActive = isActive; }
    
    public List<Tag> getTags() { return tags; }
    public void setTags(List<Tag> tags) { this.tags = tags; }
    
    public List<EventParticipant> getParticipants() { return participants; }    
}
