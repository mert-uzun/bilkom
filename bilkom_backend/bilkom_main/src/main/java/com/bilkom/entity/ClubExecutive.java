package com.bilkom.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import java.sql.Timestamp;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import java.util.Objects;

@Entity
@Table(name = "club_executives")
public class ClubExecutive {
    @Id
    @OneToOne
    @JoinColumn(name = "executive_id", referencedColumnName = "user_id", nullable = false, columnDefinition = "BIGINT")
    private User user;

    @ManyToOne
    @JoinColumn(name = "club_id", nullable = false, columnDefinition = "BIGINT")
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
        isActive = active;
    }
}
