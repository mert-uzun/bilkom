package com.bilkom.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "event_name", nullable = false)
    private String eventName;

    @Column(name = "event_description", nullable = false)
    private String eventDescription;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @Column(name = "is_club_event", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isClubEvent;

    @Column(name = "club_id", nullable = false)
    private Long clubId;
}
