package com.bilkom.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "club_executives")
public class ClubExecutive {

    @EmbeddedId
    private ClubExecutiveId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "executive_id", nullable = false)
    private User user;

    @ManyToOne
    @MapsId("clubId")
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @Column(name = "position", nullable = false)
    private String position;

    @Column(name = "join_date", nullable = false)
    private Timestamp joinDate;

    @Column(name = "leave_date")
    private Timestamp leaveDate;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    public ClubExecutive() {
    }

    public ClubExecutive(User user, Club club, String position) {
        this.user = user;
        this.club = club;
        this.id = new ClubExecutiveId(user.getUserId(), club.getClubId());
        this.position = position;
        this.joinDate = new Timestamp(System.currentTimeMillis());
        this.leaveDate = null;
        this.isActive = true;
    }

    // === GETTERS AND SETTERS ===

    public ClubExecutiveId getId() {
        return id;
    }

    public void setId(ClubExecutiveId id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (this.id == null)
            this.id = new ClubExecutiveId();
        this.id.setUserId(user.getUserId());
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
        if (this.id == null)
            this.id = new ClubExecutiveId();
        this.id.setClubId(club.getClubId());
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Timestamp getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Timestamp joinDate) {
        this.joinDate = joinDate;
    }

    public Timestamp getLeaveDate() {
        return leaveDate;
    }

    public void setLeaveDate(Timestamp leaveDate) {
        this.leaveDate = leaveDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    @Override
    public String toString() {
        return "ClubExecutive{" +
                "id=" + id +
                ", user=" + user +
                ", club=" + club +
                ", position='" + position + '\'' +
                ", joinDate=" + joinDate +
                ", leaveDate=" + leaveDate +
                ", isActive=" + isActive +
                '}';
    }
}