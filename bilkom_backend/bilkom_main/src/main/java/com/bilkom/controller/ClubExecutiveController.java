package com.bilkom.controller;

import com.bilkom.dto.ClubDTO;
import com.bilkom.dto.ClubExecutiveDTO;
import com.bilkom.exception.BadRequestException;
import com.bilkom.service.ClubExecutiveService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for club executive management operations.
 * Provides endpoints for adding, updating, and removing club executives.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
@RestController
@RequestMapping("/clubs/executives")
public class ClubExecutiveController {

    @Autowired
    private ClubExecutiveService clubExecutiveService;
    
    /**
     * Gets all active executives for a specific club.
     * 
     * @param clubId The club ID
     * @return List of club executives
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/club/{clubId}")
    public ResponseEntity<List<ClubExecutiveDTO>> getActiveClubExecutives(@PathVariable("clubId") Long clubId) {
        return ResponseEntity.ok(clubExecutiveService.getActiveClubExecutives(clubId));
    }
    
    /**
     * Gets all executives for a specific club including inactive ones.
     * 
     * @param clubId The club ID
     * @return List of all club executives including inactive
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/club/{clubId}/all")
    @PreAuthorize("hasRole('CLUB_HEAD') or hasRole('ADMIN')")
    public ResponseEntity<List<ClubExecutiveDTO>> getAllClubExecutives(@PathVariable("clubId") Long clubId) {
        return ResponseEntity.ok(clubExecutiveService.getAllClubExecutives(clubId));
    }
    
    /**
     * Gets all clubs where a user is an executive.
     * 
     * @param userId The user ID
     * @return List of clubs where the user is an executive
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ClubDTO>> getClubsByExecutive(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(clubExecutiveService.getClubsByExecutive(userId));
    }
    
    /**
     * Gets a specific executive by user ID and club ID.
     * 
     * @param userId The user ID
     * @param clubId The club ID
     * @return The executive details
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/{userId}/club/{clubId}")
    public ResponseEntity<ClubExecutiveDTO> getExecutive(@PathVariable("userId") Long userId, @PathVariable("clubId") Long clubId) {
        return ResponseEntity.ok(clubExecutiveService.getExecutive(userId, clubId));
    }
    
    /**
     * Adds a new executive to a club.
     * Only club head or admin can add executives.
     * 
     * @param clubId The club ID
     * @param details Map containing userId and position
     * @return The created executive details
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @PostMapping("/club/{clubId}")
    @PreAuthorize("hasRole('CLUB_HEAD') or hasRole('ADMIN')")
    public ResponseEntity<ClubExecutiveDTO> addExecutive(@PathVariable("clubId") Long clubId, @RequestBody Map<String, Object> details) {
        if (!details.containsKey("userId") || !details.containsKey("position")) {
            throw new BadRequestException("User ID and position are required");
        }
        
        Long userId = Long.valueOf(details.get("userId").toString());
        String position = (String) details.get("position");
        
        return ResponseEntity.ok(clubExecutiveService.addExecutive(clubId, userId, position));
    }
    
    /**
     * Updates an executive's position.
     * Only club head or admin can update executive positions.
     * 
     * @param userId The user ID
     * @param clubId The club ID
     * @param details Map containing the new position
     * @return The updated executive details
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @PutMapping("/{userId}/club/{clubId}")
    @PreAuthorize("hasRole('CLUB_HEAD') or hasRole('ADMIN')")
    public ResponseEntity<ClubExecutiveDTO> updateExecutivePosition(@PathVariable("userId") Long userId, @PathVariable("clubId") Long clubId, @RequestBody Map<String, String> details) {
        if (!details.containsKey("position")) {
            throw new BadRequestException("Position is required");
        }
        
        String position = details.get("position");
        
        return ResponseEntity.ok(clubExecutiveService.updateExecutivePosition(userId, clubId, position));
    }
    
    /**
     * Removes an executive from a club.
     * Only club head or admin can remove executives.
     * 
     * @param userId The user ID
     * @param clubId The club ID
     * @return Success message
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @DeleteMapping("/{userId}/club/{clubId}")
    @PreAuthorize("hasRole('CLUB_HEAD') or hasRole('ADMIN')")
    public ResponseEntity<String> removeExecutive(@PathVariable("userId") Long userId, @PathVariable("clubId") Long clubId) {
        clubExecutiveService.removeExecutive(clubId, userId);
        return ResponseEntity.ok("Executive successfully removed");
    }
    
    /**
     * Reactivates a previously deactivated executive.
     * Only club head or admin can reactivate executives.
     * 
     * @param userId The user ID
     * @param clubId The club ID
     * @param details Map containing the new position
     * @return The reactivated executive details
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @PutMapping("/{userId}/club/{clubId}/reactivate")
    @PreAuthorize("hasRole('CLUB_HEAD') or hasRole('ADMIN')")
    public ResponseEntity<ClubExecutiveDTO> reactivateExecutive(@PathVariable("userId") Long userId, @PathVariable("clubId") Long clubId, @RequestBody Map<String, String> details) {
        if (!details.containsKey("position")) {
            throw new BadRequestException("Position is required");
        }
        
        String position = details.get("position");
        
        return ResponseEntity.ok(clubExecutiveService.reactivateExecutive(userId, clubId, position));
    }
    
    /**
     * Gets executive history for a club.
     * Only club head or admin can view full executive history.
     * 
     * @param clubId The club ID
     * @return List of all executive records with history
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/club/{clubId}/history")
    @PreAuthorize("hasRole('CLUB_HEAD') or hasRole('ADMIN')")
    public ResponseEntity<List<ClubExecutiveDTO>> getExecutiveHistory(@PathVariable("clubId") Long clubId) {
        return ResponseEntity.ok(clubExecutiveService.getExecutiveHistory(clubId));
    }
}
