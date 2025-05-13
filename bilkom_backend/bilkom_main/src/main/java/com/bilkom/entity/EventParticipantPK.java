package com.bilkom.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * EventParticipantPK is a composite primary key class for the EventParticipant entity.
 * It contains two fields: event and user, which together uniquely identify an EventParticipant.
 * 
 * @author Elif Bozkurt
* @version 1.0
*/
public class EventParticipantPK implements Serializable {
    private Long event;
    private Long user;
    
    // NO ARGS CONSTRUCTOR
    public EventParticipantPK() {}
    
    public EventParticipantPK(Long event, Long user) {
        this.event = event;
        this.user = user;
    }
    
    // Create from Event and User objects
    public static EventParticipantPK fromEntities(Event event, User user) {
        return new EventParticipantPK(event.getEventId(), user.getUserId());
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventParticipantPK that = (EventParticipantPK) o;
        return Objects.equals(event, that.event) && Objects.equals(user, that.user);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(event, user);
    }

    @Override
    public String toString() {
        return "EventParticipantPK{" + "event=" + event + ", user=" + user + '}';
    }

    // GETTERS AND SETTERS
    public Long getEvent() { return event; }
    public void setEvent(Long event) { this.event = event; }

    public Long getUser() { return user; }
    public void setUser(Long user) { this.user = user; }
}