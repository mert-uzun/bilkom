package com.bilkom.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import java.util.Objects;
import jakarta.persistence.Column;

@Entity
@Table(name = "club_members")
@IdClass(ClubMemberPK.class)
public class ClubMember {  
    @Id
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false, columnDefinition = "BIGINT")
    private User member;

    @Id
    @ManyToOne
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;  

    @Column(name = "join_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp joinDate;

    @Column(name = "leave_date", nullable = true)
    private Timestamp leaveDate;

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean isActive;

    public ClubMember() {}
    
    public ClubMember(Club club, User member) {
        this.club = club;
        this.member = member;
        this.joinDate = new Timestamp(System.currentTimeMillis());
        this.isActive = true;
        this.leaveDate = null;
    }

    @Override
    public String toString() {
        return "ClubMember{" +
                "club=" + club +
                ", member=" + member +
                ", joinDate=" + joinDate +
                ", leaveDate=" + leaveDate +
                ", isActive=" + isActive +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClubMember that = (ClubMember) o;
        return club.equals(that.club) && member.equals(that.member);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(club, member);
    }
    
    // Getters and Setters
    public Club getClub() {
        return club;
    }
    
    public void setClub(Club club) {
        this.club = club;
    }
    
    public User getMember() {
        return member;
    }
    
    public void setMember(User member) {
        this.member = member;
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

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
}