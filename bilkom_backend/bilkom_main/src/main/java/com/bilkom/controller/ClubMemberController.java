package com.bilkom.controller;

import com.bilkom.dto.ClubDTO;
import com.bilkom.dto.ClubMemberDTO;
import com.bilkom.exception.BadRequestException;
import com.bilkom.service.ClubMemberService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for club member management operations.
 * Provides endpoints for adding, removing, and retrieving club members.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
@RestController
@RequestMapping("/api/clubs/members")
public class ClubMemberController {

    @Autowired
    private ClubMemberService clubMemberService;
    
    /**
     * Gets all active members for a specific club.
     * 
     * @param clubId The club ID
     * @return List of club members
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/club/{clubId}")
    public ResponseEntity<List<ClubMemberDTO>> getActiveClubMembers(@PathVariable("clubId") Long clubId) {
        return ResponseEntity.ok(clubMemberService.getActiveClubMembers(clubId));
    }
    
    /**
     * Gets all members for a specific club including inactive ones.
     * Only club executives or admins can access this.
     * 
     * @param clubId The club ID
     * @return List of all club members including inactive
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/club/{clubId}/all")
    @PreAuthorize("hasRole('CLUB_HEAD') or hasRole('ADMIN')")
    public ResponseEntity<List<ClubMemberDTO>> getAllClubMembers(@PathVariable("clubId") Long clubId) {
        return ResponseEntity.ok(clubMemberService.getAllClubMembers(clubId));
    }
    
    /**
     * Gets all clubs where a user is a member.
     * 
     * @param userId The user ID
     * @return List of clubs where the user is a member
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ClubDTO>> getClubsByMember(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(clubMemberService.getClubsByMember(userId));
    }
    
    /**
     * Gets a specific member by user ID and club ID.
     * 
     * @param userId The user ID
     * @param clubId The club ID
     * @return The member details
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/{userId}/club/{clubId}")
    public ResponseEntity<ClubMemberDTO> getMember(@PathVariable("userId") Long userId, @PathVariable("clubId") Long clubId) {
        return ResponseEntity.ok(clubMemberService.getMember(userId, clubId));
    }
    
    /**
     * Adds a new member to a club.
     * 
     * @param clubId The club ID
     * @param details Map containing the user ID
     * @return The created member details
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @PostMapping("/club/{clubId}")
    public ResponseEntity<ClubMemberDTO> addMember(@PathVariable("clubId") Long clubId, @RequestBody Map<String, Object> details) {
        if (!details.containsKey("userId")) {
            throw new BadRequestException("User ID is required");
        }
        
        Long userId = Long.valueOf(details.get("userId").toString());
        
        return ResponseEntity.ok(clubMemberService.addMember(clubId, userId));
    }
    
    /**
     * Adds multiple members to a club at once.
     * Only club executives or admins can add multiple members at once.
     * 
     * @param clubId The club ID
     * @param details Map containing a list of user IDs
     * @return List of successfully added members
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @PostMapping("/club/{clubId}/batch")
    @PreAuthorize("hasRole('CLUB_HEAD') or hasRole('ADMIN')")
    public ResponseEntity<List<ClubMemberDTO>> addMembers(@PathVariable("clubId") Long clubId, @RequestBody Map<String, Object> details) {
        if (!details.containsKey("userIds")) {
            throw new BadRequestException("List of user IDs is required");
        }
        
        @SuppressWarnings("unchecked")
        List<Long> userIds = (List<Long>) details.get("userIds");
        
        return ResponseEntity.ok(clubMemberService.addMembers(clubId, userIds));
    }
    
    /**
     * Removes a member from a club.
     * Can be used by the member themselves to leave the club, or by club executives or admins.
     * 
     * @param userId The user ID
     * @param clubId The club ID
     * @return Success message
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @DeleteMapping("/{userId}/club/{clubId}")
    public ResponseEntity<String> removeMember(@PathVariable("userId") Long userId, @PathVariable("clubId") Long clubId) {
        clubMemberService.removeMember(clubId, userId);
        return ResponseEntity.ok("Member successfully removed");
    }
    
    /**
     * Reactivates a previously deactivated member.
     * Only club executives or admins can reactivate members.
     * 
     * @param userId The user ID
     * @param clubId The club ID
     * @return The reactivated member details
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @PutMapping("/{userId}/club/{clubId}/reactivate")
    @PreAuthorize("hasRole('CLUB_HEAD') or hasRole('ADMIN')")
    public ResponseEntity<ClubMemberDTO> reactivateMember(@PathVariable("userId") Long userId, @PathVariable("clubId") Long clubId) {
        return ResponseEntity.ok(clubMemberService.reactivateMember(userId, clubId));
    }
    
    /**
     * Gets member history for a club.
     * Only club executives or admins can view full member history.
     * 
     * @param clubId The club ID
     * @return List of all member records with history
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/club/{clubId}/history")
    @PreAuthorize("hasRole('CLUB_HEAD') or hasRole('ADMIN')")
    public ResponseEntity<List<ClubMemberDTO>> getMemberHistory(@PathVariable("clubId") Long clubId) {
        return ResponseEntity.ok(clubMemberService.getMemberHistory(clubId));
    }
    
    /**
     * Gets active member count for a club.
     * 
     * @param clubId The club ID
     * @return Number of active members
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/club/{clubId}/count")
    public ResponseEntity<Long> getActiveMemberCount(@PathVariable("clubId") Long clubId) {
        return ResponseEntity.ok(clubMemberService.getActiveMemberCount(clubId));
    }
    
    /**
     * Searches for members by name pattern in a specific club.
     * 
     * @param clubId The club ID
     * @param namePattern The name pattern to search for
     * @return List of matching members
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/club/{clubId}/search")
    public ResponseEntity<List<ClubMemberDTO>> searchMembersByName(@PathVariable("clubId") Long clubId, @RequestParam("name") String namePattern) {
        return ResponseEntity.ok(clubMemberService.searchMembersByName(clubId, namePattern));
    }
}
