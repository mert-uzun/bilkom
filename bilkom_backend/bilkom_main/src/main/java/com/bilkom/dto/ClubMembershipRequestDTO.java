package com.bilkom.dto;

import com.bilkom.entity.ClubMembershipRequest;
import java.sql.Timestamp;

/**
 * DTO for ClubMembershipRequest entity.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
public class ClubMembershipRequestDTO {
    
    private Long requestId;
    private Long userId;
    private String userName;
    private Long clubId;
    private String clubName;
    private String requestMessage;
    private Timestamp createdAt;
    private ClubMembershipRequest.RequestStatus status;
    private String responseMessage;
    private Timestamp processedAt;
    private Long processedById;
    private String processedByName;

    public ClubMembershipRequestDTO() {
    }
    
    /**
     * Constructor from entity.
     * 
     * @param request The ClubMembershipRequest entity
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public ClubMembershipRequestDTO(ClubMembershipRequest request) {
        this.requestId = request.getRequestId();
        
        if (request.getUser() != null) {
            this.userId = request.getUser().getUserId();
            this.userName = request.getUser().getFirstName() + " " + request.getUser().getLastName();
        }
        
        if (request.getClub() != null) {
            this.clubId = request.getClub().getClubId();
            this.clubName = request.getClub().getClubName();
        }
        
        this.requestMessage = request.getRequestMessage();
        this.createdAt = request.getCreatedAt();
        this.status = request.getStatus();
        this.responseMessage = request.getResponseMessage();
        this.processedAt = request.getProcessedAt();
        
        if (request.getProcessedBy() != null) {
            this.processedById = request.getProcessedBy().getUserId();
            this.processedByName = request.getProcessedBy().getFirstName() + " " + request.getProcessedBy().getLastName();
        }
    }
    
    // GETTERS AND SETTERS
    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public ClubMembershipRequest.RequestStatus getStatus() {
        return status;
    }

    public void setStatus(ClubMembershipRequest.RequestStatus status) {
        this.status = status;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public Timestamp getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Timestamp processedAt) {
        this.processedAt = processedAt;
    }

    public Long getProcessedById() {
        return processedById;
    }

    public void setProcessedById(Long processedById) {
        this.processedById = processedById;
    }

    public String getProcessedByName() {
        return processedByName;
    }

    public void setProcessedByName(String processedByName) {
        this.processedByName = processedByName;
    }
} 