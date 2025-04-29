package com.bilkom.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

/**
 * Tag is an entity class representing a tag associated with an event and a user.
 * It contains information about the tag such as tag ID, tag name, event, and user.
 * The class includes methods to get and set these attributes.
 *
 * @author Elif Bozkurt
 * @version 1.0
 */
@Entity
@Table(name = "tags")
public class Tag {
    // FIELDS
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id", nullable = false, columnDefinition = "BIGINT")
    private Long tagId;
    
    @Column(name = "tag_name", nullable = false, unique = true, columnDefinition = "VARCHAR(255)")
    private String tagName;
    
    @ManyToOne
    @JoinColumn(name = "event_id", columnDefinition = "BIGINT")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id", columnDefinition = "BIGINT")
    private User user;

    @Override
    public String toString() {
        return "Tag{" + "tagId=" + tagId + ", tagName=" + tagName + ", event=" + event + ", user=" + user + '}';
    }
    
    // GETTERS AND SETTERS
    public Long getTagId() { return tagId; }
    public void setTagId(Long tagId) { this.tagId = tagId; }

    public String getTagName() { return tagName; }
    public void setTagName(String tagName) { this.tagName = tagName; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}