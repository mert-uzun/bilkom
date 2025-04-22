package com.bilkom.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.IdClass;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "event_participants")
@IdClass(EventParticipantPK.class)
public class EventParticipant {
    @Id
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

// PRIMARY KEY CLASS
class EventParticipantPK implements Serializable {
    private Long event;
    private Long user;
    
    // NO ARGS CONSTRUCTOR
    public EventParticipantPK() {}
    
    public EventParticipantPK(Long event, Long user) {
        this.event = event;
        this.user = user;
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
    public Long getEvent() {
        return event;
    }

    public void setEvent(Long event) {
        this.event = event;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }
}