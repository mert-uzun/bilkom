package com.bilkom.model;

import com.google.gson.annotations.SerializedName;

public class PendingClub {
    @SerializedName("id")
    private long id;

    @SerializedName("club_name")
    private String name;

    @SerializedName("description")
    private String description;

    public long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
}