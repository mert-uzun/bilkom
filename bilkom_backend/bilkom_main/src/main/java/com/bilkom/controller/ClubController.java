package com.bilkom.controller;

import com.bilkom.dto.ClubDTO;
import com.bilkom.exception.BadRequestException;
import com.bilkom.service.ClubService;
import com.bilkom.service.UserService;
import com.bilkom.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.security.Principal;

/**
 * Controller for general club operations.
 * Provides endpoints for retrieving, creating, updating, and managing clubs.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
@RestController
@RequestMapping("/clubs")
public class ClubController {

    @Autowired
    private ClubService clubService;
    
    @Autowired
    private UserService userService;
    
    /**
     * Gets all approved and active clubs.
     * 
     * @return List of all approved and active clubs
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping
    public ResponseEntity<List<ClubDTO>> getAllClubs() {
        return ResponseEntity.ok(clubService.getApprovedClubs());
    }
    
    /**
     * Gets all clubs (for admin use).
     * 
     * @return List of all clubs including inactive or unapproved
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ClubDTO>> getAllClubsAdmin() {
        return ResponseEntity.ok(clubService.getAllClubs());
    }
    
    /**
     * Gets a specific club by ID.
     * 
     * @param id The club ID
     * @return The requested club
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClubDTO> getClubById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(clubService.getClubById(id));
    }
    
    /**
     * Gets clubs where the specified user is the club head.
     * 
     * @param userId The user ID
     * @return List of clubs headed by the user
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/headed-by/{userId}")
    public ResponseEntity<List<ClubDTO>> getClubsByHeadId(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(clubService.getClubsByHeadId(userId));
    }
    
    /**
     * Gets clubs where the specified user is an executive.
     * 
     * @param userId The user ID
     * @return List of clubs where the user is an executive
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/executive-in/{userId}")
    public ResponseEntity<List<ClubDTO>> getClubsByExecutiveId(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(clubService.getClubsByExecutiveId(userId));
    }
    
    /**
     * Gets clubs where the specified user is a member.
     * 
     * @param userId The user ID
     * @return List of clubs where the user is a member
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/member-in/{userId}")
    public ResponseEntity<List<ClubDTO>> getClubsByMemberId(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(clubService.getClubsByMemberId(userId));
    }
    
    /**
     * Creates a new club (admin only).
     * This bypasses the normal club registration process.
     * 
     * @param clubDetails Map containing club name, description, and club head ID
     * @return The created club
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClubDTO> createClub(@RequestBody Map<String, Object> clubDetails) {
        if (!clubDetails.containsKey("clubName") || !clubDetails.containsKey("clubDescription") || !clubDetails.containsKey("clubHeadId")) {
            throw new BadRequestException("Club name, description, and club head ID are required");
        }
        
        String clubName = (String) clubDetails.get("clubName");
        String clubDescription = (String) clubDetails.get("clubDescription");
        Long clubHeadId = Long.valueOf(clubDetails.get("clubHeadId").toString());
        
        return ResponseEntity.ok(clubService.createClub(clubName, clubDescription, clubHeadId));
    }
    
    /**
     * Updates a club's details.
     * Only club head or admin can update a club.
     * 
     * @param id The club ID
     * @param clubDetails Map containing club name and/or description
     * @return The updated club
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CLUB_HEAD') or hasRole('ADMIN')")
    public ResponseEntity<ClubDTO> updateClub(@PathVariable("id") Long id, @RequestBody Map<String, String> clubDetails) {
        String clubName = clubDetails.get("clubName");
        String clubDescription = clubDetails.get("clubDescription");
        
        return ResponseEntity.ok(clubService.updateClub(id, clubName, clubDescription));
    }

    /**
     * Deletes a club (admin only).
     * 
     * @param id The club ID
     * @return Success message
     * 
     * @author Elif Bozkurt
     * @version 1.0
     * -- ADDED AFTER COMPARING WITH ANDROID --
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CLUB_HEAD') or hasRole('ADMIN')")
    public ResponseEntity<String> deleteClub(@PathVariable("id") Long id) {
        clubService.deleteClub(id);
        return ResponseEntity.ok("Club deleted successfully");
    }
    
    /**
     * Changes the club head.
     * Only the current club head or admin can change the club head.
     * 
     * @param id The club ID
     * @param details Map containing the new club head ID
     * @return The updated club
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @PutMapping("/{id}/head")
    @PreAuthorize("hasRole('CLUB_HEAD') or hasRole('ADMIN')")
    public ResponseEntity<ClubDTO> changeClubHead(@PathVariable("id") Long id, @RequestBody Map<String, Object> details) {
        if (!details.containsKey("newHeadId")) {
            throw new BadRequestException("New club head ID is required");
        }
        
        Long newHeadId = Long.valueOf(details.get("newHeadId").toString());
        
        return ResponseEntity.ok(clubService.changeClubHead(id, newHeadId));
    }
    
    /**
     * Deactivates a club (admin only).
     * 
     * @param id The club ID
     * @return The deactivated club
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClubDTO> deactivateClub(@PathVariable("id") Long id) {
        return ResponseEntity.ok(clubService.deactivateClub(id));
    }
    
    /**
     * Reactivates a club (admin only).
     * 
     * @param id The club ID
     * @return The reactivated club
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @PutMapping("/{id}/reactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClubDTO> reactivateClub(@PathVariable("id") Long id) {
        return ResponseEntity.ok(clubService.reactivateClub(id));
    }
    
    /**
     * Adds a user as a member of a club.
     * Any authenticated user can join a club directly without approval.
     * 
     * @param clubId The club ID
     * @return Success message
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @PostMapping("/{clubId}/members")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> joinClub(@PathVariable("clubId") Long clubId, Principal principal) {
        // Get the authenticated user
        User user = userService.getUserByEmail(principal.getName());
        
        clubService.addClubMember(clubId, user.getUserId());
        
        return ResponseEntity.ok("You have successfully joined the club");
    }
}
