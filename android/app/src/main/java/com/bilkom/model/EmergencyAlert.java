// we use this class to store the emergency alert data from the server using id, title, message and severity
package com.bilkom.model;

import java.io.Serializable;
import com.google.gson.annotations.SerializedName;
import java.util.Date;
import com.bilkom.utils.DateUtils;

/**
 * Represents a real-time emergency alert fetched from
 * <code>/emergency-alerts</code>.
 *
 * <p>Most fields come straight from the backend JSON; the helper
 * methods convert the <em>createdAt</em> ISO string into user-friendly
 * date and date-time formats.</p>
 *
 * @author  Sıla Bozkurt
 * @version 1.0
 * @since   2025-05-11
 */
public class EmergencyAlert implements Serializable {

    @SerializedName("id")        private Long   id;
    @SerializedName("title")     private String title;
    @SerializedName("description") private String description;
    @SerializedName("location")  private String location;
    @SerializedName("date")      private String date;
    @SerializedName("active")    private boolean active;
    @SerializedName("createdBy") private Long createdBy;

    public EmergencyAlert() { }

    public EmergencyAlert(Long id, String title, String description, String location, String date, boolean active, Long createdBy) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.date = date;
        this.active = active;
        this.createdBy = createdBy;
    }

    public Long   getId()          { return id; }
    public void   setId(Long id)   { this.id = id; }

    public String getTitle()       { return title; }
    public void   setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void   setDescription(String description) { this.description = description; }

    public String getLocation()    { return location; }
    public void   setLocation(String location) { this.location = location; }

    public String getDate()        { return date; }
    public void   setDate(String date) { this.date = date; }

    public boolean isActive()      { return active; }
    public void    setActive(boolean active) { this.active = active; }

    public Long getCreatedBy()     { return createdBy; }
    public void   setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    /** Raw ISO string → {@link java.util.Date} (may return {@code null} on parse error). */
    public Date getCreatedDate() {
        return DateUtils.parseApiDate(date);
    }

    public String getFormattedCreated() {
        return DateUtils.formatUserFriendlyDateTime(getCreatedDate());
    }
}
