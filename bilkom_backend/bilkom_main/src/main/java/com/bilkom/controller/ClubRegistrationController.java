package com.bilkom.controller;

import com.bilkom.dto.ClubDTO;
import com.bilkom.dto.ClubRegistrationRequestDTO;
import com.bilkom.service.ClubRegistrationService;
import com.bilkom.exception.BadRequestException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

/**
 * Controller for club registration operations.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
@RestController
@RequestMapping("/clubs/registration")
public class ClubRegistrationController {

    @Autowired
    private ClubRegistrationService clubRegistrationService;
    
    /**
     * Registers a new club (pending approval).
     * 
     * @param registrationRequest The club registration request
     * @return The registered club (pending approval)
     * @throws MessagingException If there's an error sending the verification email
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ClubDTO> registerClub(@Valid @RequestBody ClubRegistrationRequestDTO registrationRequest) throws MessagingException {
        ClubDTO registeredClub = clubRegistrationService.registerClub(registrationRequest);
        return ResponseEntity.ok(registeredClub);
    }
    
    /**
     * Checks if a club name is available.
     * 
     * @param queryName Club name from query parameter
     * @param payload JSON payload containing the club name to check (alternative)
     * @return true if the name is available, false otherwise
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @PostMapping("/check-name")
    public ResponseEntity<Boolean> isClubNameAvailable(
            @RequestParam(value = "name", required = false) String queryName,
            @RequestBody(required = false) java.util.Map<String, String> payload) {
        
        String clubName = queryName;
        
        // If not provided in query param, try to get from JSON body
        if (clubName == null && payload != null) {
            clubName = payload.get("name");
        }
        
        if (clubName == null) {
            throw new BadRequestException("Club name is required");
        }
        
        boolean isAvailable = clubRegistrationService.isClubNameAvailable(clubName);
        return ResponseEntity.ok(isAvailable);
    }
    
    /**
     * Gets pending club registrations (admin only).
     * 
     * @return List of pending clubs
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<java.util.List<ClubDTO>> getPendingRegistrations() {
        return ResponseEntity.ok(clubRegistrationService.getPendingRegistrations());
    }
    
    /**
     * Gets a specific pending club registration by ID (admin only).
     * 
     * @param id The club ID
     * @return The pending club registration
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/pending/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClubDTO> getPendingRegistrationById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(clubRegistrationService.getPendingRegistrationById(id));
    }
    
    /**
     * Gets rejected club registrations (admin only).
     * 
     * @return List of rejected clubs
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/rejected")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<java.util.List<ClubDTO>> getRejectedRegistrations() {
        return ResponseEntity.ok(clubRegistrationService.getRejectedRegistrations());
    }
}
