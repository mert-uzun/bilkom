package com.bilkom.model;

public class ClubMembership {
    private String clubName;
    private String role; // CLUB_HEAD, CLUB_EXECUTIVE, USER

    public String getClubName() { return clubName; }
    public void setClubName(String clubName) { this.clubName = clubName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
} 