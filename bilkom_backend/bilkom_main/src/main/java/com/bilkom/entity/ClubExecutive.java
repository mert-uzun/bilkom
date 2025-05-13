package com.bilkom.entity;

import com.bilkom.enums.UserRole;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

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
        
        // Update user role to CLUB_EXECUTIVE if it's USER and not already a CLUB_HEAD or ADMIN
        if (user != null && user.getRole() == UserRole.USER) {
            user.setRole(UserRole.CLUB_EXECUTIVE);
        }
    }

    //GETTERS AND SETTERS

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
        if (leaveDate == null) {
            return Timestamp.valueOf("2099-12-31 23:59:59");
        }
        return leaveDate;
    }

    public void setLeaveDate(Timestamp leaveDate) {
        this.leaveDate = leaveDate;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        // If deactivating and previously was active
        if (!active && this.isActive) {
            // Set leave date if setting to inactive
            this.leaveDate = new Timestamp(System.currentTimeMillis());
        
            // Handle role reversion (only if user is not a club head)
            if (user != null && club != null && user.getRole() == UserRole.CLUB_EXECUTIVE) {
                if (!Objects.equals(club.getClubHead() == null ? null : club.getClubHead().getUserId(), user.getUserId())) {
                    // Only change role if no other active executive or club head positions
                    user.setRole(UserRole.USER);
                }
            }
        }
        
        this.isActive = active;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
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

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        
        ClubExecutive that = (ClubExecutive) o;
        return user.equals(that.user) && club.equals(that.club);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, club);
    }

    public boolean isUserClubHead() {
        return user != null && user.getRole() == UserRole.CLUB_HEAD;
    }

    public boolean isUserClubExecutive() {
        return user != null && user.getRole() == UserRole.CLUB_EXECUTIVE;
    }

    public boolean isUserAdmin() {
        return user != null && user.getRole() == UserRole.ADMIN;
    }

    public boolean isUserMember() {
        return user != null && user.getRole() == UserRole.USER;
    }
}
