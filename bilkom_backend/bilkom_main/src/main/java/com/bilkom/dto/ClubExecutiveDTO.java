package com.bilkom.dto;

import java.sql.Timestamp;
import com.bilkom.entity.ClubExecutive;

/**
 * DTO for ClubExecutive entity.
 * @author Mert Uzun
 * @version 1.0.0
 */
public class ClubExecutiveDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private Long clubId;
    private String clubName;
    private String position;
    private Timestamp joinDate;
    private Timestamp leaveDate;
    private boolean isActive;

    /**
     * Default constructor for ClubExecutiveDTO.
     * @author Mert Uzun
     * @version 1.0.0
     */
    public ClubExecutiveDTO() {}

    /**
     * Constructor for ClubExecutiveDTO.
     * @param clubExecutive ClubExecutive entity.
     * @author Mert Uzun
     * @version 1.0.0
     */
    public ClubExecutiveDTO(ClubExecutive clubExecutive) {
        this.userId = clubExecutive.getUser().getUserId();
        this.firstName = clubExecutive.getUser().getFirstName();
        this.lastName = clubExecutive.getUser().getLastName();
        this.email = clubExecutive.getUser().getEmail();
        this.clubId = clubExecutive.getClub().getClubId();
        this.clubName = clubExecutive.getClub().getClubName();
        this.position = clubExecutive.getPosition();
        this.joinDate = clubExecutive.getJoinDate();
        this.leaveDate = clubExecutive.getLeaveDate();
        this.isActive = clubExecutive.isActive();
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
        isActive = active;
    }
}
