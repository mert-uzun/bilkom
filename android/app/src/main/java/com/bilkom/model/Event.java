package com.bilkom.model;

import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import com.bilkom.utils.DateUtils;

/**
 * Data model for events
 * 
 * @author Mert Uzun
 * @version 1.0
 * @since 2025-05-09
 */
public class Event implements Serializable {
    @SerializedName("eventId")
    private Long eventId;
    
    @SerializedName("eventName")
    private String eventName;
    
    @SerializedName("eventDescription")
    private String eventDescription;
    
    @SerializedName("maxParticipants")
    private int maxParticipants;
    
    @SerializedName("currentParticipantsNumber")
    private int currentParticipantsNumber;
    
    @SerializedName("eventLocation")
    private String eventLocation;
    
    @SerializedName("eventDate")
    private String eventDate; 
    
    @SerializedName("isActive")
    private boolean isActive;
    
    @SerializedName("tags")
    private List<Object> rawTags;
    
    @SerializedName("clubId")
    private Long clubId;
    
    @SerializedName("creatorId")
    private Long creatorId;
    
    @SerializedName("isClubEvent")
    private boolean isClubEvent;

    @SerializedName("isJoined")
    private boolean isJoined;

    /**
     * Custom type adapter for tags that can handle both String values and Tag objects
     */
    public static class TagsTypeAdapter extends TypeAdapter<List<String>> {
        @Override
        public void write(JsonWriter out, List<String> value) throws IOException {
            out.beginArray();
            for (String tag : value) {
                out.value(tag);
            }
            out.endArray();
        }

        @Override
        public List<String> read(JsonReader in) throws IOException {
            List<String> tags = new ArrayList<>();
            
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return tags;
            }
            
            in.beginArray();
            while (in.hasNext()) {
                if (in.peek() == JsonToken.STRING) {
                    // If the next token is a string, read it directly
                    tags.add(in.nextString());
                } else if (in.peek() == JsonToken.BEGIN_OBJECT) {
                    // If the next token is an object, extract the tagName field
                    in.beginObject();
                    String tagName = null;
                    while (in.hasNext()) {
                        String name = in.nextName();
                        if (name.equals("tagName")) {
                            tagName = in.nextString();
                        } else {
                            in.skipValue();
                        }
                    }
                    in.endObject();
                    if (tagName != null) {
                        tags.add(tagName);
                    }
                } else {
                    in.skipValue();
                }
            }
            in.endArray();
            return tags;
        }
    }

    public Event() {}

    public Event(Long eventId, String eventName, String eventDescription, int maxParticipants, 
                int currentParticipantsNumber, String eventLocation, String eventDate, 
                boolean isActive, List<String> tags) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.maxParticipants = maxParticipants;
        this.currentParticipantsNumber = currentParticipantsNumber;
        this.eventLocation = eventLocation;
        this.eventDate = eventDate;
        this.isActive = isActive;
        this.rawTags = new ArrayList<>(tags);
    }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public String getEventDescription() { return eventDescription; }
    public void setEventDescription(String eventDescription) { this.eventDescription = eventDescription; }

    public int getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }

    public int getCurrentParticipantsNumber() { return currentParticipantsNumber; }
    public void setCurrentParticipantsNumber(int currentParticipantsNumber) { this.currentParticipantsNumber = currentParticipantsNumber; }

    public String getEventLocation() { return eventLocation; }
    public void setEventLocation(String eventLocation) { this.eventLocation = eventLocation; }

    /**
     * Get the raw event date string as stored from the API
     * @return The raw event date string
     */
    public String getEventDate() { return eventDate; }
    public void setEventDate(String eventDate) { this.eventDate = eventDate; }

    /**
     * Get the event date as a Date object
     * @return The event date as a Date object, or null if parsing fails
     */
    public Date getEventDateAsDate() {
        return DateUtils.parseApiDate(eventDate);
    }
    
    /**
     * Get the event date in a user-friendly format (e.g., "May 9, 2025")
     * @return The formatted date string
     */
    public String getFormattedEventDate() {
        Date date = getEventDateAsDate();
        return DateUtils.formatUserFriendlyDate(date);
    }
    
    /**
     * Get the event date and time in a user-friendly format (e.g., "May 9, 2025 14:30")
     * @return The formatted date and time string
     */
    public String getFormattedEventDateTime() {
        Date date = getEventDateAsDate();
        return DateUtils.formatUserFriendlyDateTime(date);
    }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public List<String> getTags() {
        List<String> tagStrings = new ArrayList<>();
        if (rawTags != null) {
            for (Object tag : rawTags) {
                if (tag instanceof String) {
                    tagStrings.add((String) tag);
                } else if (tag instanceof Map) {
                    // Handle object tags like {"name": "tag1"}
                    Map<String, Object> tagMap = (Map<String, Object>) tag;
                    if (tagMap.containsKey("name")) {
                        tagStrings.add(tagMap.get("name").toString());
                    }
                }
            }
        }
        return tagStrings;
    }

    public void setTags(List<String> tags) {
        this.rawTags = new ArrayList<>(tags);
    }
    
    public Long getClubId() { return clubId; }
    public void setClubId(Long clubId) { this.clubId = clubId; }
    
    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }
    
    public boolean isClubEvent() { return isClubEvent; }
    public void setClubEvent(boolean clubEvent) { isClubEvent = clubEvent; }

    public boolean isJoined() { return isJoined; }
    public void setJoined(boolean joined) { isJoined = joined; }

    public int getCurrentParticipants() { return currentParticipantsNumber; }
    public void setCurrentParticipants(int currentParticipants) { this.currentParticipantsNumber = currentParticipants; }
} 