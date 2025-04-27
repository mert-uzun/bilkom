package com.bilkom.service;

import com.bilkom.dto.ClubMembershipRequestDTO;
import com.bilkom.entity.Club;
import com.bilkom.entity.ClubMembershipRequest;
import com.bilkom.entity.User;
import com.bilkom.enums.UserRole;
import com.bilkom.exception.BadRequestException;
import com.bilkom.repository.ClubMembershipRequestRepository;
import com.bilkom.repository.ClubRepository;
import com.bilkom.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for handling club membership requests.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
@Service
public class ClubMembershipRequestService {

    @Autowired
    private ClubMembershipRequestRepository requestRepository;
    
    @Autowired
    private ClubRepository clubRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ClubService clubService;
    
    @Autowired
    private EmailService emailService;
    
    /**
     * Creates a new membership request for a club.
     * 
     * @param userId The user ID
     * @param clubId The club ID
     * @param message The request message
     * @return DTO of the created request
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public ClubMembershipRequestDTO createRequest(Long userId, Long clubId, String message) {
        // Check if user and club exist
        User user = findUserById(userId);
        Club club = findClubById(clubId);
        
        // Check if club is active and approved
        if (!club.isActive() || club.getStatus() != com.bilkom.enums.ClubRegistrationStatus.APPROVED) {
            throw new BadRequestException("Club is not active or approved");
        }
        
        // Check if user is already a member
        if (clubService.isUserMember(clubId, userId)) {
            throw new BadRequestException("User is already a member of this club");
        }
        
        // Check if there's already a pending request
        if (requestRepository.existsByUserUserIdAndClubClubIdAndStatus(userId, clubId, ClubMembershipRequest.RequestStatus.PENDING)) {
            throw new BadRequestException("A pending request already exists for this user and club");
        }
        
        // Create the request
        ClubMembershipRequest request = new ClubMembershipRequest(user, club, message);
        request = requestRepository.save(request);
        
        // Notify club executives about the new request
        notifyClubExecutives(club, user);
        
        return new ClubMembershipRequestDTO(request);
    }
    
    /**
     * Gets all pending requests for a club.
     * 
     * @param clubId The club ID
     * @return List of pending requests
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public List<ClubMembershipRequestDTO> getPendingRequestsForClub(Long clubId) {
        // Check if club exists
        findClubById(clubId);
        
        return requestRepository.findByClubClubIdAndStatus(clubId, ClubMembershipRequest.RequestStatus.PENDING).stream().map(ClubMembershipRequestDTO::new).collect(Collectors.toList());
    }
    
    /**
     * Gets all requests for a club.
     * 
     * @param clubId The club ID
     * @return List of all requests
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public List<ClubMembershipRequestDTO> getAllRequestsForClub(Long clubId) {
        // Check if club exists
        findClubById(clubId);
        
        return requestRepository.findByClubClubId(clubId).stream().map(ClubMembershipRequestDTO::new).collect(Collectors.toList());
    }
    
    /**
     * Gets all requests made by a user.
     * 
     * @param userId The user ID
     * @return List of all requests made by the user
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public List<ClubMembershipRequestDTO> getUserRequests(Long userId) {
        // Check if user exists
        findUserById(userId);
        
        return requestRepository.findByUserUserId(userId).stream().map(ClubMembershipRequestDTO::new).collect(Collectors.toList());
    }
    
    /**
     * Gets pending requests made by a user.
     * 
     * @param userId The user ID
     * @return List of pending requests made by the user
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public List<ClubMembershipRequestDTO> getPendingUserRequests(Long userId) {
        // Check if user exists
        findUserById(userId);
        
        return requestRepository.findByUserUserIdAndStatus(userId, ClubMembershipRequest.RequestStatus.PENDING).stream().map(ClubMembershipRequestDTO::new).collect(Collectors.toList());
    }
    
    /**
     * Gets a specific membership request.
     * 
     * @param requestId The request ID
     * @return DTO of the request
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public ClubMembershipRequestDTO getRequest(Long requestId) {
        ClubMembershipRequest request = findRequestById(requestId);
        return new ClubMembershipRequestDTO(request);
    }
    
    /**
     * Approves a membership request.
     * 
     * @param requestId The request ID
     * @param processedByUserId The ID of the user processing the request
     * @param responseMessage The response message
     * @return DTO of the approved request
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public ClubMembershipRequestDTO approveRequest(Long requestId, Long processedByUserId, String responseMessage) {
        // Get the request and necessary entities
        ClubMembershipRequest request = findRequestById(requestId);
        User processor = findUserById(processedByUserId);
        
        // Check if the request is already processed
        if (request.getStatus() != ClubMembershipRequest.RequestStatus.PENDING) {
            throw new BadRequestException("Request is already processed");
        }
        
        // Check if processor has authority (club executive, club head, or admin)
        validateRequestProcessor(processor, request.getClub());
        
        // Update request status
        request.setStatus(ClubMembershipRequest.RequestStatus.APPROVED);
        request.setProcessedBy(processor);
        request.setProcessedAt(new Timestamp(System.currentTimeMillis()));
        request.setResponseMessage(responseMessage);
        
        // Add user to the club
        clubService.addClubMember(request.getClub().getClubId(), request.getUser().getUserId());
        
        // Save the updated request
        request = requestRepository.save(request);
        
        // Notify the user that their request was approved
        notifyUserOfApproval(request);
        
        return new ClubMembershipRequestDTO(request);
    }
    
    /**
     * Rejects a membership request.
     * 
     * @param requestId The request ID
     * @param processedByUserId The ID of the user processing the request
     * @param responseMessage The response message
     * @return DTO of the rejected request
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public ClubMembershipRequestDTO rejectRequest(Long requestId, Long processedByUserId, String responseMessage) {
        // Get the request and necessary entities
        ClubMembershipRequest request = findRequestById(requestId);
        User processor = findUserById(processedByUserId);
        
        // Check if the request is already processed
        if (request.getStatus() != ClubMembershipRequest.RequestStatus.PENDING) {
            throw new BadRequestException("Request is already processed");
        }
        
        // Check if processor has authority (club executive, club head, or admin)
        validateRequestProcessor(processor, request.getClub());
        
        // Update request status
        request.setStatus(ClubMembershipRequest.RequestStatus.REJECTED);
        request.setProcessedBy(processor);
        request.setProcessedAt(new Timestamp(System.currentTimeMillis()));
        request.setResponseMessage(responseMessage);
        
        // Save the updated request
        request = requestRepository.save(request);
        
        // Notify the user that their request was rejected
        notifyUserOfRejection(request);
        
        return new ClubMembershipRequestDTO(request);
    }
    
    /**
     * Cancels a pending membership request (can only be done by the requester).
     * 
     * @param requestId The request ID
     * @param userId The user ID (must match the requester)
     * @return DTO of the canceled request
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public ClubMembershipRequestDTO cancelRequest(Long requestId, Long userId) {
        // Get the request
        ClubMembershipRequest request = findRequestById(requestId);
        
        // Check if the request is pending
        if (request.getStatus() != ClubMembershipRequest.RequestStatus.PENDING) {
            throw new BadRequestException("Request is already processed");
        }
        
        // Check if the user is the requester
        if (!request.getUser().getUserId().equals(userId)) {
            throw new BadRequestException("Only the requester can cancel the request");
        }
        
        // Delete the request
        requestRepository.delete(request);
        
        return new ClubMembershipRequestDTO(request);
    }
    
    /**
     * Helper method that validates that a user can process membership requests for a club.
     * User must be a club executive, club head, or admin.
     * 
     * @param user The user
     * @param club The club
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    private void validateRequestProcessor(User user, Club club) {
        boolean isAdmin = user.getRole() == UserRole.ADMIN;
        boolean isClubHead = club.getClubHead().getUserId().equals(user.getUserId());
        boolean isClubExecutive = user.getRole() == UserRole.CLUB_EXECUTIVE && 
                clubService.isUserExecutive(club.getClubId(), user.getUserId());
        
        if (!isAdmin && !isClubHead && !isClubExecutive) {
            throw new BadRequestException("User is not authorized to process membership requests for this club");
        }
    }
    
    /**
     * Helper method that notifies club executives about a new membership request.
     * 
     * @param club The club
     * @param requester The requesting user
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    private void notifyClubExecutives(Club club, User requester) {
        // In a real implementation, this would email all club executives
        // For now, we'll just email the club head
        String subject = "New Club Membership Request for " + club.getClubName();
        String message = "User " + requester.getFirstName() + " " + requester.getLastName()
                            + " has requested to join " + club.getClubName() + ". Please log in to process this request.";
        
        emailService.sendSimpleEmail(club.getClubHead().getEmail(), subject, message);
    }
    
    /**
     * Helper method that notifies a user that their membership request was approved.
     * 
     * @param request The approved request
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    private void notifyUserOfApproval(ClubMembershipRequest request) {
        String subject = "Club Membership Request Approved for " + request.getClub().getClubName();
        String message = "Congratulations! Your request to join " + request.getClub().getClubName() + 
                " has been approved. You are now a member of the club.";
        
        emailService.sendSimpleEmail(request.getUser().getEmail(), subject, message);
    }
    
    /**
     * Helper method that notifies a user that their membership request was rejected.
     * 
     * @param request The rejected request
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    private void notifyUserOfRejection(ClubMembershipRequest request) {
        String subject = "Club Membership Request Rejected for " + request.getClub().getClubName();
        String message = "We're sorry, but your request to join " + request.getClub().getClubName()
                            + " has been rejected.\n\nReason: " + (request.getResponseMessage() != null ? request.getResponseMessage() : "No reason provided.");
        
        emailService.sendSimpleEmail(request.getUser().getEmail(), subject, message);
    }
    
    /**
     * Helper method that finds a user by their ID.
     * 
     * @param userId The user ID
     * @return The user
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new BadRequestException("User not found with ID: " + userId));
    }
    
    /**
     * Helper method that finds a club by their ID.
     * 
     * @param clubId The club ID
     * @return The club
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    private Club findClubById(Long clubId) {
        return clubRepository.findById(clubId).orElseThrow(() -> new BadRequestException("Club not found with ID: " + clubId));
    }
    
    /**
     * Helper method that finds a membership request by their ID.
     * 
     * @param requestId The request ID
     * @return The request
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    private ClubMembershipRequest findRequestById(Long requestId) {
        return requestRepository.findById(requestId).orElseThrow(() -> new BadRequestException("Membership request not found with ID: " + requestId));
    }
} 