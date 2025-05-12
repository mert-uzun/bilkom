// we use this class to store the emergency alert data from the server using id, title, message and severity
package com.bilkom.model;

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
public class EmergencyAlert {

    @SerializedName("id")        private Long   id;
    @SerializedName("title")     private String title;
    @SerializedName("description") private String description;
    @SerializedName("location")  private String location;
    @SerializedName("severity")  private String severity;
    @SerializedName("createdAt") private String createdAt;  
    @SerializedName("active")    private boolean active;

    public EmergencyAlert() { }

    public EmergencyAlert(Long id, String title, String description, String location,
                          String severity, String createdAt, boolean active) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.severity = severity;
        this.createdAt = createdAt;
        this.active = active;
    }


    public Long   getId()          { return id; }
    public void   setId(Long id)   { this.id = id; }

    public String getTitle()       { return title; }
    public void   setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void   setDescription(String description) { this.description = description; }

    public String getLocation()    { return location; }
    public void   setLocation(String location) { this.location = location; }

    public String getSeverity()    { return severity; }
    public void   setSeverity(String severity) { this.severity = severity; }

    public String getCreatedAt()   { return createdAt; }
    public void   setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public boolean isActive()      { return active; }
    public void    setActive(boolean active) { this.active = active; }


    /** Raw ISO string → {@link java.util.Date} (may return {@code null} on parse error). */
    public Date getCreatedDate() {
        return DateUtils.parseApiDate(createdAt);
    }

    /** Returns date-time in a user-friendly format, e.g. “11 May 2025 13:07”. */
    public String getFormattedCreated() {
        return DateUtils.formatUserFriendlyDateTime(getCreatedDate());
    }
}
