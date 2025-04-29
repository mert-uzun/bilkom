package com.bilkom.controller;

import com.bilkom.dto.ClubDTO;
import com.bilkom.exception.BadRequestException;
import com.bilkom.service.AdminVerificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for admin-specific operations including club registration verification processes.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminVerificationService adminVerificationService;
    
    /**
     * Approves a club registration.
     * 
     * @param id The club ID
     * @param token The verification token
     * @return The approved club
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/clubs/approve")
    public ResponseEntity<ClubDTO> approveClub(@RequestParam("id") Long id, @RequestParam("token") String token) {
        if (id == null || token == null) {
            throw new BadRequestException("Club ID and token are required");
        }
        
        ClubDTO approvedClub = adminVerificationService.approveClub(id, token);
        return ResponseEntity.ok(approvedClub);
    }
    
    /**
     * Rejects a club registration.
     * 
     * @param id The club ID
     * @param token The verification token
     * @param reason The reason for rejection
     * @return The rejected club
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/clubs/reject")
    public ResponseEntity<ClubDTO> rejectClub(@RequestParam("id") Long id, @RequestParam("token") String token, @RequestParam(value = "reason", required = false) String reason) {
        
        if (id == null || token == null) {
            throw new BadRequestException("Club ID and token are required");
        }
        
        ClubDTO rejectedClub = adminVerificationService.rejectClub(id, token, reason);
        return ResponseEntity.ok(rejectedClub);
    }
} 