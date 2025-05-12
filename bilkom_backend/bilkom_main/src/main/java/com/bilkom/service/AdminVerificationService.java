package com.bilkom.service;

import com.bilkom.dto.ClubDTO;
import com.bilkom.entity.Club;
import com.bilkom.entity.ClubExecutive;
import com.bilkom.entity.ClubExecutiveId;
import com.bilkom.entity.ClubMember;
import com.bilkom.entity.User;
import com.bilkom.enums.ClubRegistrationStatus;
import com.bilkom.enums.UserRole;
import com.bilkom.exception.BadRequestException;
import com.bilkom.repository.ClubExecutiveRepository;
import com.bilkom.repository.ClubMemberRepository;
import com.bilkom.repository.ClubRepository;
import com.bilkom.repository.UserRepository;

import java.sql.Timestamp;
import java.util.Optional;

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
        System.out.println("[DEBUG] Approving club ID: " + clubId + " with token: " + token);

        // Step 1: Verify token
        verifyToken(clubId, token);
        System.out.println("[DEBUG] Token verified.");

        // Step 2: Fetch club
        Club club = findClubById(clubId);
        System.out.println("[DEBUG] Club fetched: " + club.getClubName() + " (status: " + club.getStatus() + ")");

        // Step 3: Ensure club is in pending state
        if (club.getStatus() != ClubRegistrationStatus.PENDING) {
            throw new BadRequestException("Club is not in pending status");
        }

        // Step 4: Update status to APPROVED
        club.setStatus(ClubRegistrationStatus.APPROVED);
        club = clubRepository.save(club);
        System.out.println("[DEBUG] Club approved.");

        // Step 5: Fetch club head and promote if needed
        User clubHead = club.getClubHead();
        System.out.println("[DEBUG] Club head user ID: " + clubHead.getUserId());

        if (clubHead.getRole() == UserRole.USER) {
            clubHead.setRole(UserRole.CLUB_HEAD);
            userRepository.save(clubHead);
            System.out.println("[DEBUG] Club head promoted to CLUB_HEAD.");
        }

        // Step 6: Safely add or reactivate executive
        ClubExecutiveId execId = new ClubExecutiveId(clubHead.getUserId(), club.getClubId());
        System.out.println("[DEBUG] ClubExecutive ID = " + execId.getUserId() + "-" + execId.getClubId());

        ClubExecutive exec = clubExecutiveRepository.findById(execId).orElse(null);

        if (exec == null) {
            exec = new ClubExecutive(clubHead, club, "Club Head");
            exec.setId(execId);
            clubExecutiveRepository.save(exec);
            System.out.println("[DEBUG] New ClubExecutive created.");
        } else {
            if (!exec.isActive()) {
                exec.setActive(true);
                exec.setLeaveDate(null);
                exec.setJoinDate(new Timestamp(System.currentTimeMillis()));
                exec.setPosition("Club Head");
                clubExecutiveRepository.save(exec);
                System.out.println("[DEBUG] Existing ClubExecutive reactivated.");
            } else {
                System.out.println("[DEBUG] ClubExecutive already exists and is active.");
            }
        }

        // Step 7: Safely add club member
        if (!clubMemberRepository.existsByMemberUserIdAndClubClubIdAndIsActiveTrue(clubHead.getUserId(), club.getClubId())) {
            clubMemberRepository.save(new ClubMember(club, clubHead));
            System.out.println("[DEBUG] ClubMember created.");
        } else {
            System.out.println("[DEBUG] ClubMember already exists and is active.");
        }

        // Step 8: Send confirmation email
        emailService.sendClubRegistrationResultEmail(clubHead.getEmail(), club.getClubName(), true, null);
        System.out.println("[DEBUG] Email sent to club head.");

        // Step 9: Remove verification token
        clubRegistrationService.removeToken(token);
        System.out.println("[DEBUG] Token removed.");

        // Step 10: Return DTO
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
        
        // Update status to REJECTED
        club.setStatus(ClubRegistrationStatus.REJECTED);
        club = clubRepository.save(club);
        
        // Send notification email to club head
        User clubHead = club.getClubHead();
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
}
