package com.bilkom.controller;

import com.bilkom.dto.ClubDTO;
import com.bilkom.exception.BadRequestException;
import com.bilkom.service.AdminVerificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for admin-specific operations including club registration verification processes.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminVerificationService adminVerificationService;
    
    /**
     * Approves a club registration.
     * 
     * @param queryId Club ID from query parameter 
     * @param queryToken Token from query parameter
     * @param payload JSON payload containing club ID and token (alternative)
     * @return The approved club
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @PostMapping("/clubs/approve")
    public ResponseEntity<ClubDTO> approveClub(
            @RequestParam(value = "id", required = false) Long queryId,
            @RequestParam(value = "token", required = false) String queryToken,
            @RequestBody(required = false) Map<String, Object> payload) {
            
        Long id = queryId;
        String token = queryToken;
        
        // If not provided in query params, try to get from JSON body
        if ((id == null || token == null) && payload != null) {
            id = payload.containsKey("id") ? Long.valueOf(payload.get("id").toString()) : null;
            token = payload.containsKey("token") ? payload.get("token").toString() : null;
        }
        
        if (id == null || token == null) {
            throw new BadRequestException("Club ID and token are required");
        }
        
        ClubDTO approvedClub = adminVerificationService.approveClub(id, token);
        return ResponseEntity.ok(approvedClub);
    }
    
    /**
     * Rejects a club registration.
     * 
     * @param queryId Club ID from query parameter
     * @param queryToken Token from query parameter
     * @param queryReason Reason from query parameter
     * @param payload JSON payload containing club ID, token and reason (alternative)
     * @return The rejected club
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @PostMapping("/clubs/reject")
    public ResponseEntity<ClubDTO> rejectClub(
            @RequestParam(value = "id", required = false) Long queryId,
            @RequestParam(value = "token", required = false) String queryToken,
            @RequestParam(value = "reason", required = false) String queryReason,
            @RequestBody(required = false) Map<String, Object> payload) {
            
        Long id = queryId;
        String token = queryToken;
        String reason = queryReason;
        
        // If not provided in query params, try to get from JSON body
        if ((id == null || token == null) && payload != null) {
            id = payload.containsKey("id") ? Long.valueOf(payload.get("id").toString()) : null;
            token = payload.containsKey("token") ? payload.get("token").toString() : null;
            reason = payload.containsKey("reason") ? payload.get("reason").toString() : null;
        }
        
        if (id == null || token == null) {
            throw new BadRequestException("Club ID and token are required");
        }
        
        ClubDTO rejectedClub = adminVerificationService.rejectClub(id, token, reason);
        return ResponseEntity.ok(rejectedClub);
    }
} 