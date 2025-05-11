package com.bilkom.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Payload used when the client creates or updates an event
 * (<code>POST&nbsp;/events</code> or <code>PUT&nbsp;/events/{id}</code>).
 *
 * Fields mirror those required by the Spring controller; Times are ISO-8601 strings.
 *
 * @author Sıla Bozkurt
 */
public class EventRequest {

    @SerializedName("eventName")
    private String eventName;

    @SerializedName("eventDescription")
    private String eventDescription;

    @SerializedName("maxParticipants")
    private int maxParticipants;

    @SerializedName("eventLocation")
    private String eventLocation;

    @SerializedName("eventDate")
    private String eventDate;

    @SerializedName("tags")
    private List<String> tags;

    @SerializedName("isClubEvent")
    private boolean isClubEvent;

    @SerializedName("clubId")
    private Long clubId;     

    public EventRequest() { }

    public EventRequest(String eventName, String eventDescription,
                        int maxParticipants, String eventLocation,
                        String eventDate, List<String> tags,
                        boolean isClubEvent, Long clubId) {
        this.eventName        = eventName;
        this.eventDescription = eventDescription;
        this.maxParticipants  = maxParticipants;
        this.eventLocation    = eventLocation;
        this.eventDate        = eventDate;
        this.tags             = tags;
        this.isClubEvent      = isClubEvent;
        this.clubId           = clubId;
    }


    public String      getEventName()        { return eventName; }
    public void        setEventName(String eventName) { this.eventName = eventName; }

    public String      getEventDescription() { return eventDescription; }
    public void        setEventDescription(String eventDescription) { this.eventDescription = eventDescription; }

    public int         getMaxParticipants()  { return maxParticipants; }
    public void        setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }

    public String      getEventLocation()    { return eventLocation; }
    public void        setEventLocation(String eventLocation) { this.eventLocation = eventLocation; }

    public String      getEventDate()        { return eventDate; }
    public void        setEventDate(String eventDate) { this.eventDate = eventDate; }

    public List<String> getTags()            { return tags; }
    public void         setTags(List<String> tags) { this.tags = tags; }

    public boolean     isClubEvent()         { return isClubEvent; }
    public void        setClubEvent(boolean clubEvent) { isClubEvent = clubEvent; }

    public Long        getClubId()           { return clubId; }
    public void        setClubId(Long clubId) { this.clubId = clubId; }
}
