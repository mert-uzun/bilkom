package com.bilkom.dto;

import com.bilkom.entity.ClubMember;
import java.sql.Timestamp;

/**
 * DTO for ClubMember entity.
 * @author Mert Uzun
 * @version 1.0.0
 */
public class ClubMemberDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private Long clubId;
    private String clubName;
    private Timestamp joinDate;
    private Timestamp leaveDate;
    private boolean isActive;

    /**
     * Default constructor for ClubMemberDTO.
     * @author Mert Uzun
     * @version 1.0.0
     */
    public ClubMemberDTO() {}

    /**
     * Constructor for ClubMemberDTO.
     * @param clubMember ClubMember entity.
     * @author Mert Uzun
     * @version 1.0.0
     */
    public ClubMemberDTO(ClubMember clubMember) {
        this.userId = clubMember.getMember().getUserId();
        this.firstName = clubMember.getMember().getFirstName();
        this.lastName = clubMember.getMember().getLastName();
        this.email = clubMember.getMember().getEmail();
        this.clubId = clubMember.getClub().getClubId();
        this.clubName = clubMember.getClub().getClubName();
        this.joinDate = clubMember.getJoinDate();
        this.leaveDate = clubMember.getLeaveDate();
        this.isActive = clubMember.isActive();
    }

    //GETTERS AND SETTERS
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

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
        isActive = active;
    }
}
