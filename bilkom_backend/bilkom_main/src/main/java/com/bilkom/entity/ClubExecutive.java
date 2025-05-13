package com.bilkom.entity;

import com.bilkom.enums.UserRole;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import java.sql.Timestamp;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "club_executives")
@IdClass(ClubExecutivePK.class)
public class ClubExecutive {
    @Id
    @OneToOne
    @JoinColumn(name = "executive_id", referencedColumnName = "user_id", nullable = false, columnDefinition = "BIGINT")
    @JsonIgnore
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "club_id", nullable = false)
    @JsonIgnore
    private Club club;    

    @Column(name = "position", nullable = false, columnDefinition = "VARCHAR(255)")
    private String position;

    @Column(name = "join_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp joinDate;

    @Column(name = "leave_date", nullable = true)
    private Timestamp leaveDate;

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean isActive; 

    public ClubExecutive() {}

    public ClubExecutive(User user, Club club, String position)
    {
        this.user = user;
        this.club = club;
        this.position = position;
        this.joinDate = new Timestamp(System.currentTimeMillis());
        this.leaveDate = null;
        this.isActive = true;
        
        // Update user role to CLUB_EXECUTIVE if it's USER and not already a CLUB_HEAD or ADMIN
        if (user != null && user.getRole() == UserRole.USER) {
            user.setRole(UserRole.CLUB_EXECUTIVE);
        }
    }

    @Override
    public String toString() {
        return "ClubExecutive{" +
                "user=" + user +
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
    
    //GETTERS AND SETTERS
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    public Club getClub() {
        return club;
    }
    
    public void setClub(Club club) {
        this.club = club;
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
    
    public void setJoinDate(Timestamp createdAt) {
        this.joinDate = createdAt;
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
}
