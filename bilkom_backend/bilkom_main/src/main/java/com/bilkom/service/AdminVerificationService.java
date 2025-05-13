package com.bilkom.service;

import com.bilkom.dto.ClubDTO;
import com.bilkom.entity.Club;
import com.bilkom.entity.ClubExecutive;
import com.bilkom.entity.ClubMember;
import com.bilkom.entity.User;
import com.bilkom.enums.ClubRegistrationStatus;
import com.bilkom.enums.UserRole;
import com.bilkom.exception.BadRequestException;
import com.bilkom.repository.ClubExecutiveRepository;
import com.bilkom.repository.ClubMemberRepository;
import com.bilkom.repository.ClubRepository;
import com.bilkom.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for handling admin verification processes for clubs and other entities.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
@Service
public class AdminVerificationService {

    @Autowired
    private ClubRepository clubRepository;
    
    @Autowired
    private ClubExecutiveRepository clubExecutiveRepository;
    
    @Autowired
    private ClubMemberRepository clubMemberRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private ClubRegistrationService clubRegistrationService;

    /**
     * Approves a club registration.
     * 
     * @param clubId The club ID
     * @param token The verification token
     * @return ClubDTO for the approved club
     * @throws BadRequestException if club is not found, token is invalid, or club is not pending
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public ClubDTO approveClub(Long clubId, String token) {
        // Verify token
        verifyToken(clubId, token);
        
        // Find the club
        Club club = findClubById(clubId);
        
        // Verify club is in PENDING status
        if (club.getStatus() != ClubRegistrationStatus.PENDING) {
            throw new BadRequestException("Club is not in pending status");
        }
        
        // Update status to APPROVED
        club.setStatus(ClubRegistrationStatus.APPROVED);
        club = clubRepository.save(club);
        
        // Get the club head user
        User clubHead = club.getClubHead();
        
        // Promote user to CLUB_HEAD role if they're a regular user
        if (clubHead.getRole() == UserRole.USER) {
            clubHead.setRole(UserRole.CLUB_HEAD);
            userRepository.save(clubHead);
        }
        
        // Add club head as executive and member
        ClubExecutive executive = new ClubExecutive(clubHead, club, "Club Head");
        clubExecutiveRepository.save(executive);
        
        ClubMember member = new ClubMember(club, clubHead);
        clubMemberRepository.save(member);
        
        // Send notification email to club head
        emailService.sendClubRegistrationResultEmail(clubHead.getEmail(), club.getClubName(), true, null);
        
        // Remove token
        clubRegistrationService.removeToken(token);
        
        return new ClubDTO(club);
    }
    
    /**
     * Rejects a club registration.
     * 
     * @param clubId The club ID
     * @param token The verification token
     * @param reason The reason for rejection
     * @return ClubDTO for the rejected club
     * @throws BadRequestException if club is not found, token is invalid, or club is not pending
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public ClubDTO rejectClub(Long clubId, String token, String reason) {
        // Verify token
        verifyToken(clubId, token);
        
        // Find the club
        Club club = findClubById(clubId);
        
        // Verify club is in PENDING status
        if (club.getStatus() != ClubRegistrationStatus.PENDING) {
            throw new BadRequestException("Club is not in pending status");
        }
        
        // Get the club head
        User clubHead = club.getClubHead();
        
        // Update status to REJECTED
        club.setStatus(ClubRegistrationStatus.REJECTED);
        club.setActive(false); // Set club to inactive when rejected
        club = clubRepository.save(club);
        
        // Ensure user role remains USER if they were a USER
        if (clubHead.getRole() == UserRole.USER) {
            // No change needed, leave as USER
        } else if (clubHead.getRole() == UserRole.CLUB_HEAD) {
            // Check if the user is head of other clubs
            long otherActiveClubsCount = clubRepository.countByClubHeadAndStatusAndIsActive(
                clubHead, ClubRegistrationStatus.APPROVED, true);
            
            // Only downgrade to USER if this was their only club
            if (otherActiveClubsCount == 0) {
                clubHead.setRole(UserRole.USER);
                userRepository.save(clubHead);
            }
        }
        
        // Send notification email to club head
        emailService.sendClubRegistrationResultEmail(
                clubHead.getEmail(), club.getClubName(), false, reason);
        
        // Remove token
        clubRegistrationService.removeToken(token);
        
        return new ClubDTO(club);
    }
    
    /**
     * Helper method to find a club by ID.
     * 
     * @param clubId The club ID
     * @return The club entity
     * @throws BadRequestException if club is not found
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    private Club findClubById(Long clubId) {
        return clubRepository.findById(clubId).orElseThrow(() -> new BadRequestException("Club not found with ID: " + clubId));
    }
    
    /**
     * Helper method to verify token validity.
     * 
     * @param clubId The club ID
     * @param token The verification token
     * @throws BadRequestException if token is invalid
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    private void verifyToken(Long clubId, String token) {
        Long tokenClubId = clubRegistrationService.getClubIdByToken(token);
        
        if (tokenClubId == null || !tokenClubId.equals(clubId)) {
            throw new BadRequestException("Invalid verification token");
        }
    }
    
    /**
     * Method to set a verification token directly for testing purposes only.
     * This should not be used in production code.
     * 
     * @param token The token
     * @param clubId The club ID
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public void setTestToken(String token, Long clubId) {
        clubRegistrationService.setTestToken(token, clubId);
    }
}
