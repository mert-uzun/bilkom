package com.bilkom.controller;

import com.bilkom.dto.ClubMembershipRequestDTO;
import com.bilkom.exception.BadRequestException;
import com.bilkom.service.ClubMembershipRequestService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for club membership request operations.
 * Provides endpoints for creating, approving, and rejecting membership requests.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
@RestController
@RequestMapping("/api/clubs/membership-requests")
public class ClubMembershipRequestController {
    
    @Autowired
    private ClubMembershipRequestService requestService;
    
    /**
     * Creates a new membership request.
     * 
     * @param details Map containing clubId and optional requestMessage
     * @param userId User ID from the authenticated user
     * @return The created request
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ClubMembershipRequestDTO> createRequest(@RequestBody Map<String, Object> details, @RequestParam("userId") Long userId) {
        if (!details.containsKey("clubId")) {
            throw new BadRequestException("Club ID is required");
        }
        
        Long clubId = Long.valueOf(details.get("clubId").toString());
        String message = details.containsKey("requestMessage") ? (String) details.get("requestMessage") : null;
        
        ClubMembershipRequestDTO request = requestService.createRequest(userId, clubId, message);
        return ResponseEntity.ok(request);
    }
    
    /**
     * Gets all pending requests for a club.
     * Only accessible by club executives and admins.
     * 
     * @param clubId The club ID
     * @return List of pending requests
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/club/{clubId}/pending")
    @PreAuthorize("hasAnyRole('CLUB_HEAD', 'CLUB_EXECUTIVE', 'ADMIN')")
    public ResponseEntity<List<ClubMembershipRequestDTO>> getPendingRequestsForClub(@PathVariable("clubId") Long clubId) {
        return ResponseEntity.ok(requestService.getPendingRequestsForClub(clubId));
    }
    
    /**
     * Gets all requests for a club.
     * Only accessible by club executives and admins.
     * 
     * @param clubId The club ID
     * @return List of all requests
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/club/{clubId}")
    @PreAuthorize("hasAnyRole('CLUB_HEAD', 'CLUB_EXECUTIVE', 'ADMIN')")
    public ResponseEntity<List<ClubMembershipRequestDTO>> getAllRequestsForClub(@PathVariable("clubId") Long clubId) {
        return ResponseEntity.ok(requestService.getAllRequestsForClub(clubId));
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
    @GetMapping("/user/{userId}")
    @PreAuthorize("authentication.principal.username == @userService.getUserById(#userId).email or hasRole('ADMIN')")
    public ResponseEntity<List<ClubMembershipRequestDTO>> getUserRequests(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(requestService.getUserRequests(userId));
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
    @GetMapping("/user/{userId}/pending")
    @PreAuthorize("authentication.principal.username == @userService.getUserById(#userId).email or hasRole('ADMIN')")
    public ResponseEntity<List<ClubMembershipRequestDTO>> getPendingUserRequests(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(requestService.getPendingUserRequests(userId));
    }
    
    /**
     * Gets a specific membership request.
     * 
     * @param requestId The request ID
     * @return The request details
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/{requestId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ClubMembershipRequestDTO> getRequest(@PathVariable("requestId") Long requestId) {
        return ResponseEntity.ok(requestService.getRequest(requestId));
    }
    
    /**
     * Approves a membership request.
     * Only accessible by club executives and admins.
     * 
     * @param requestId The request ID
     * @param details Map containing processedByUserId and optional responseMessage
     * @return The approved request
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @PutMapping("/{requestId}/approve")
    @PreAuthorize("hasAnyRole('CLUB_HEAD', 'CLUB_EXECUTIVE', 'ADMIN')")
    public ResponseEntity<ClubMembershipRequestDTO> approveRequest(@PathVariable("requestId") Long requestId, @RequestBody Map<String, Object> details) {
        if (!details.containsKey("processedByUserId")) {
            throw new BadRequestException("Processor user ID is required");
        }
        
        Long processedByUserId = Long.valueOf(details.get("processedByUserId").toString());
        String responseMessage = details.containsKey("responseMessage") ? (String) details.get("responseMessage") : null;
        
        ClubMembershipRequestDTO request = requestService.approveRequest(requestId, processedByUserId, responseMessage);
        return ResponseEntity.ok(request);
    }
    
    /**
     * Rejects a membership request.
     * Only accessible by club executives and admins.
     * 
     * @param requestId The request ID
     * @param details Map containing processedByUserId and optional responseMessage
     * @return The rejected request
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @PutMapping("/{requestId}/reject")
    @PreAuthorize("hasAnyRole('CLUB_HEAD', 'CLUB_EXECUTIVE', 'ADMIN')")
    public ResponseEntity<ClubMembershipRequestDTO> rejectRequest(@PathVariable("requestId") Long requestId,
                                                               @RequestBody Map<String, Object> details) {
        if (!details.containsKey("processedByUserId")) {
            throw new BadRequestException("Processor user ID is required");
        }
        
        Long processedByUserId = Long.valueOf(details.get("processedByUserId").toString());
        String responseMessage = details.containsKey("responseMessage") ? (String) details.get("responseMessage") : null;
        
        ClubMembershipRequestDTO request = requestService.rejectRequest(requestId, processedByUserId, responseMessage);
        return ResponseEntity.ok(request);
    }
    
    /**
     * Cancels a pending membership request (can only be done by the requester).
     * 
     * @param requestId The request ID
     * @param userId The user ID
     * @return The canceled request
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @DeleteMapping("/{requestId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ClubMembershipRequestDTO> cancelRequest(@PathVariable("requestId") Long requestId, @RequestParam("userId") Long userId) {
        ClubMembershipRequestDTO request = requestService.cancelRequest(requestId, userId);
        return ResponseEntity.ok(request);
    }
} 