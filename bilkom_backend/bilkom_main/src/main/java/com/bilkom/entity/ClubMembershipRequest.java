package com.bilkom.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

/**
 * Entity for club membership requests.
 * Represents a user's request to join a club, pending approval.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
@Entity
@Table(name = "club_membership_requests")
public class ClubMembershipRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id", nullable = false)
    private Long requestId;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;
    
    @Column(name = "request_message", columnDefinition = "TEXT")
    private String requestMessage;
    
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;
    
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'PENDING'")
    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.PENDING;
    
    @Column(name = "response_message", columnDefinition = "TEXT")
    private String responseMessage;
    
    @Column(name = "processed_at")
    private Timestamp processedAt;
    
    @ManyToOne
    @JoinColumn(name = "processed_by")
    private User processedBy;
    
    /**
     * Status of a membership request.
     */
    public enum RequestStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
    
    /**
     * Default constructor.
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public ClubMembershipRequest() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }
    
    /**
     * Constructor with required fields.
     * 
     * @param user The user requesting membership
     * @param club The club to join
     * @param requestMessage The request message
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public ClubMembershipRequest(User user, Club club, String requestMessage) {
        this.user = user;
        this.club = club;
        this.requestMessage = requestMessage;
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.status = RequestStatus.PENDING;
    }
    
    // Getters and setters
    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

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

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
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

    public User getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(User processedBy) {
        this.processedBy = processedBy;
    }
} 