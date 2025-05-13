package com.bilkom.entity;

import com.bilkom.enums.UserRole;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;
import com.bilkom.enums.ClubRegistrationStatus;
import java.util.Objects;

@Entity
@Table(name = "clubs")
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "club_id")
    private Long clubId;      

    @Column(name = "club_name", nullable = false, columnDefinition = "VARCHAR(255)")
    private String clubName;

    @Column(name = "club_description", nullable = false, columnDefinition = "TEXT")
    private String clubDescription;

    @ManyToOne
    @JoinColumn(name = "club_head", nullable = false, columnDefinition = "BIGINT")
    private User clubHead;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean isActive = true;

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClubMember> clubMembers = new ArrayList<>();

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Event> events = new ArrayList<>();

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClubExecutive> clubExecutives = new ArrayList<>();  

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING'")
    private ClubRegistrationStatus status;

    public Club() {}

    public Club(String clubName, String clubDescription, User clubHead) {
        this.clubName = clubName;
        this.clubDescription = clubDescription;
        this.clubHead = clubHead;
        this.status = ClubRegistrationStatus.PENDING;
        this.createdAt = new Timestamp(System.currentTimeMillis());
        
        // Update user role to CLUB_HEAD if not already an ADMIN
        if (clubHead != null && clubHead.getRole() != UserRole.ADMIN) {
            clubHead.setRole(UserRole.CLUB_HEAD);
        }
    }

    @Override
    public String toString() {
        return "Club{" +
                "clubId=" + clubId +
                ", clubName='" + clubName + '\'' +
                ", clubDescription='" + clubDescription + '\'' +
                ", clubHead=" + clubHead +
                ", createdAt=" + createdAt +
                ", isActive=" + isActive +
                ", clubMembers=" + clubMembers +
                ", events=" + events +
                ", clubExecutives=" + clubExecutives +
                ", status=" + status +
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

        Club club = (Club) o;
        return clubId.equals(club.clubId);  
    }

    @Override
    public int hashCode() {
        return Objects.hash(clubId);
    }

    //GETTERS AND SETTERS
    public Long getClubId() {
        return clubId;
    }

    public void setClubId(Long clubId) {
        this.clubId = clubId;
    }

    public String getClubName() {
        return clubName;
    }
    
    public void setClubName(String clubName) {
        this.clubName = clubName;
    }
    
    public String getClubDescription() {
        return clubDescription;
    }
    
    public void setClubDescription(String clubDescription) {
        this.clubDescription = clubDescription;
    }
    
    public User getClubHead() {
        return clubHead;
    }
    
    public void setClubHead(User clubHead) {
        this.clubHead = clubHead;
        
        // Update user role to CLUB_HEAD if not already an ADMIN
        if (clubHead != null && clubHead.getRole() != UserRole.ADMIN) {
            clubHead.setRole(UserRole.CLUB_HEAD);
        }
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
    
    public List<ClubMember> getClubMembers() {
        return clubMembers;
    }
    
    public void setClubMembers(List<ClubMember> clubMembers) {
        this.clubMembers = clubMembers;
    }
    
    public List<Event> getEvents() {
        return events;
    }
    
    public void setEvents(List<Event> events) {
        this.events = events;
    }
    
    public List<ClubExecutive> getClubExecutives() {
        return clubExecutives;
    }
    
    public void setClubExecutives(List<ClubExecutive> clubExecutives) {
        this.clubExecutives = clubExecutives;
    }
    
    public ClubRegistrationStatus getStatus() {
        return status;
    }

    public void setStatus(ClubRegistrationStatus status) {
        this.status = status;
    }
}
