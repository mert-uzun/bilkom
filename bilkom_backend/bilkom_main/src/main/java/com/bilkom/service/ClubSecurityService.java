package com.bilkom.service;

import com.bilkom.repository.ClubRepository;
import com.bilkom.repository.ClubExecutiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for handling club-specific security checks.
 * Used in method-level security expressions to determine if a user has access to club resources.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
@Service
public class ClubSecurityService {

    @Autowired
    private ClubRepository clubRepository;
    
    @Autowired
    private ClubExecutiveRepository clubExecutiveRepository;
    
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
} 