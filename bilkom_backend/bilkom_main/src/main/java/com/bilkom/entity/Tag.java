package com.bilkom.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.IdClass;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "tags")
@IdClass(TagPK.class)
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id", nullable = false, columnDefinition = "BIGINT")
    private Long tagId;
    
    @Column(name = "tag_name", nullable = false, columnDefinition = "VARCHAR(255)")
    private String tagName;
    
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false, columnDefinition = "BIGINT")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "BIGINT")
    private User user;

    @Override
    public String toString() {
        return "Tag{" + "tagId=" + tagId + ", tagName=" + tagName + ", event=" + event + ", user=" + user + '}';
    }
    
}

// PRIMARY KEY CLASS
class TagPK implements Serializable {
    private Long event;
    private String tagName;

    public TagPK() {}

    public TagPK(Long event, String tagName) {
        this.event = event;
        this.tagName = tagName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        if (o instanceof TagPK){
            TagPK tagPK = (TagPK) o;
            return tagName.equals(tagPK.tagName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(event, tagName);
    }

    @Override
    public String toString() {
        return "TagPK{" + "event=" + event + ", tagName='" + tagName + '\'' + '}';
    }

    // GETTERS AND SETTERS
    public Long getEvent() {
        return event;
    }

    public void setEvent(Long event) {
        this.event = event;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}


