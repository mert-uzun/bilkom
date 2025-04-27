package com.bilkom.dto;

import com.bilkom.entity.Club;
import java.util.List;
import com.bilkom.enums.ClubRegistrationStatus;
import java.sql.Timestamp;
import java.util.stream.Collectors;
import com.bilkom.entity.User;

/**
 * DTO for Club entity.
 * @author Mert Uzun
 * @version 1.0.0
 */
public class ClubDTO {
    private Long clubId;
    private String clubName;
    private String clubDescription;
    private User clubHead;
    private List<ClubExecutiveDTO> clubExecutives;
    private List<ClubMemberDTO> clubMembers;
    private Timestamp createdAt;
    private boolean isActive;
    private ClubRegistrationStatus status;

    /**
     * Default constructor for ClubDTO.
     * @author Mert Uzun
     * @version 1.0.0
     */
    public ClubDTO() {}

    /**
     * Constructor for ClubDTO.
     * @param club Club entity.
     * @author Mert Uzun
     * @version 1.0.0
     */
    public ClubDTO(Club club) {
        this.clubId = club.getClubId();
        this.clubName = club.getClubName();
        this.clubDescription = club.getClubDescription();
        this.clubHead = club.getClubHead();
        this.clubExecutives = club.getClubExecutives().stream().map(ClubExecutiveDTO::new).collect(Collectors.toList());
        this.clubMembers = club.getClubMembers().stream().map(ClubMemberDTO::new).collect(Collectors.toList());
        this.createdAt = club.getCreatedAt();
        this.isActive = club.isActive();
        this.status = club.getStatus();
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
    }

    public List<ClubExecutiveDTO> getClubExecutives() {
        return clubExecutives;
    }
    
    public void setClubExecutives(List<ClubExecutiveDTO> clubExecutives) {
        this.clubExecutives = clubExecutives;
    }

    public List<ClubMemberDTO> getClubMembers() {
        return clubMembers;
    }
    
    public void setClubMembers(List<ClubMemberDTO> clubMembers) {
        this.clubMembers = clubMembers;
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
    
    public ClubRegistrationStatus getStatus() {
        return status;
    }
    
    public void setStatus(ClubRegistrationStatus status) {
        this.status = status;
    }
    
}
