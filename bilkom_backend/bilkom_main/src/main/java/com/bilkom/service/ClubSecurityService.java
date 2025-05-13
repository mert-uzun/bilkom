package com.bilkom.service;

import com.bilkom.entity.Club;
import com.bilkom.entity.User;
import com.bilkom.enums.UserRole;
import com.bilkom.repository.ClubRepository;
import com.bilkom.repository.ClubExecutiveRepository;
import com.bilkom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for handling club-specific security checks.
 * Used in method-level security expressions to determine if a user has access to club resources.
 * 
 * @author Mert Uzun
 * @version 1.1
 */
@Service
public class ClubSecurityService {

    @Autowired
    private ClubRepository clubRepository;
    
    @Autowired
    private ClubExecutiveRepository clubExecutiveRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Checks if a user is either a club executive or the club head for a specific club.
     * 
     * @param userId The user ID to check
     * @param clubId The club ID to check against
     * @return true if the user is either a club executive or the club head for the specified club
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public boolean isUserClubExecutiveOrHead(Long userId, Long clubId) {
        // Check if user is the club head
        boolean isClubHead = clubRepository.findById(clubId).map(club -> club.getClubHead() != null && club.getClubHead().getUserId().equals(userId)).orElse(false);
            
        if (isClubHead) {
            return true;
        }
        
        // Check if user is a club executive
        return clubExecutiveRepository.existsByUserUserIdAndClubClubIdAndIsActiveTrue(userId, clubId);
    }
    
    /**
     * Checks if a user has permission to manage a club's resources.
     * This includes club heads, administrators, and active club executives.
     *
     * @param userId The user ID to check
     * @param clubId The club ID to check against
     * @return true if the user has management permissions for the club
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public boolean canUserManageClub(Long userId, Long clubId) {
        // Check if user is an admin
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent() && userOpt.get().getRole() == UserRole.ADMIN) {
            return true;
        }
        
        // Check if club is active
        Optional<Club> clubOpt = clubRepository.findById(clubId);
        if (clubOpt.isEmpty() || !clubOpt.get().isActive()) {
            return false;
        }
        
        // For non-admins, use the executive/head check
        return isUserClubExecutiveOrHead(userId, clubId);
    }
    
    /**
     * Checks if a user has permission to edit club details.
     * Only club heads and administrators can edit club details.
     *
     * @param userId The user ID to check
     * @param clubId The club ID to check against
     * @return true if the user can edit the club details
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public boolean canUserEditClub(Long userId, Long clubId) {
        // Check if user is an admin
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent() && userOpt.get().getRole() == UserRole.ADMIN) {
            return true;
        }
        
        // Check if user is the club head
        return clubRepository.findById(clubId)
                .map(club -> club.isActive() && club.getClubHead() != null && club.getClubHead().getUserId().equals(userId))
                .orElse(false);
    }
    
    /**
     * Checks if a user has permission to manage club events.
     * This includes creating, editing, and deleting events.
     *
     * @param userId The user ID to check
     * @param clubId The club ID to check against
     * @return true if the user can manage club events
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public boolean canUserManageClubEvents(Long userId, Long clubId) {
        // Club executives and heads can manage events
        return canUserManageClub(userId, clubId);
    }
} 