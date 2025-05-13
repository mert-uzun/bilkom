package com.bilkom.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.IdClass;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * EventParticipant is an entity class representing a participant in an event.
 * It contains references to the event and the user participating in it.
 * The class uses a composite primary key consisting of event ID and user ID.
 *
 * @author Elif Bozkurt
 * @version 1.0
 */
@Entity
@Table(name = "event_participants")
@IdClass(EventParticipantPK.class)
public class EventParticipant {
    @Id
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    @JsonIgnore
    private Event event;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Override
    public String toString() {
        System.out.println("Calling EventParticipant.toString()");
        return "EventParticipant{" + "user=" + user.getUserId() + "}";
    }

    // GETTERS AND SETTERS
    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event;}
    
    public User getUser() { return user;}
    public void setUser(User user) { this.user = user;}

    public String getEmail(){
        return user.getEmail();
    }
}