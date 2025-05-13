package com.bilkom.service;

import com.bilkom.dto.ClubDTO;
import com.bilkom.dto.ClubRegistrationRequestDTO;
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

import jakarta.mail.MessagingException;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for club-related operations.
 * Handles CRUD operations, club registration, and verification.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
@Service
public class ClubService {

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClubExecutiveRepository clubExecutiveRepository;

    @Autowired
    private ClubMemberRepository clubMemberRepository;

    @Autowired
    private ClubRegistrationService clubRegistrationService;

    @Autowired
    private AdminVerificationService adminVerificationService;

    /**
     * Retrieves all clubs.
     * 
     * @return List of all clubs
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public List<ClubDTO> getAllClubs() {
        return clubRepository.findAll().stream().map(ClubDTO::new).collect(Collectors.toList());
    }

    /**
     * Retrieves all active clubs.
     * 
     * @return List of active clubs
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public List<ClubDTO> getActiveClubs() {
        return clubRepository.findByIsActiveTrue().stream().map(ClubDTO::new).collect(Collectors.toList());
    }

    /**
     * Retrieves approved and active clubs.
     * 
     * @return List of approved and active clubs
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public List<ClubDTO> getApprovedClubs() {
        return clubRepository.findByStatusAndIsActive(ClubRegistrationStatus.APPROVED, true).stream().map(ClubDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves clubs pending approval.
     * 
     * @return List of pending clubs
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public List<ClubDTO> getPendingClubs() {
        return clubRegistrationService.getPendingRegistrations();
    }

    /**
     * Retrieves a club by ID.
     * 
     * @param clubId The club ID
     * @return ClubDTO for the requested club
     * @throws BadRequestException if club is not found
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public ClubDTO getClubById(Long clubId) {
        Club club = findClubById(clubId);
        return new ClubDTO(club);
    }

    /**
     * Retrieves clubs where a user is the club head.
     * 
     * @param userId The user ID
     * @return List of clubs headed by the user
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public List<ClubDTO> getClubsByHeadId(Long userId) {
        User user = findUserById(userId);
        return clubRepository.findByClubHead(user).stream().map(ClubDTO::new).collect(Collectors.toList());
    }

    /**
     * Retrieves clubs where a user is an executive.
     * 
     * @param userId The user ID
     * @return List of clubs where the user is an executive
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public List<ClubDTO> getClubsByExecutiveId(Long userId) {
        return clubExecutiveRepository.findByUserUserIdAndIsActiveTrue(userId).stream()
                .map(executive -> new ClubDTO(executive.getClub())).collect(Collectors.toList());
    }

    /**
     * Retrieves clubs where a user is a member.
     * 
     * @param userId The user ID
     * @return List of clubs where the user is a member
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public List<ClubDTO> getClubsByMemberId(Long userId) {
        return clubMemberRepository.findByMemberUserIdAndIsActiveTrue(userId).stream()
                .map(member -> new ClubDTO(member.getClub())).collect(Collectors.toList());
    }

    /**
     * Creates a new club with direct approval.
     * Don't use this method for club registration, use registerClub instead.
     * 
     * @param clubName        The club name
     * @param clubDescription The club description
     * @param clubHeadId      The user ID of the club head
     * @return ClubDTO for the created club
     * @throws BadRequestException if club name is already taken
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public ClubDTO createClub(String clubName, String clubDescription, Long clubHeadId) {
        // Check if club name is already taken
        if (!clubRegistrationService.isClubNameAvailable(clubName)) {
            throw new BadRequestException("Club name already taken: " + clubName);
        }

        User clubHead = findUserById(clubHeadId);
        
        // Set the user's role to CLUB_HEAD if they're not already an admin
        if (clubHead.getRole() != UserRole.ADMIN) {
            clubHead.setRole(UserRole.CLUB_HEAD);
            userRepository.save(clubHead);
        }

        // Create the club
        Club club = new Club(clubName, clubDescription, clubHead);
        club.setStatus(ClubRegistrationStatus.APPROVED); // Directly approved when created by service
        club = clubRepository.save(club);

        // Add club head as executive and member
        addClubExecutive(club.getClubId(), clubHeadId, "Club Head");

        return new ClubDTO(club);
    }

    /**
     * Updates an existing club.
     * 
     * @param clubId          The club ID
     * @param clubName        The new club name
     * @param clubDescription The new club description
     * @return ClubDTO for the updated club
     * @throws BadRequestException if club is not found or name is taken
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public ClubDTO updateClub(Long clubId, String clubName, String clubDescription) {
        Club club = findClubById(clubId);

        // Check if name is changing and if it's already taken
        if (clubName != null && !clubName.equals(club.getClubName())) {
            if (!clubRegistrationService.isClubNameAvailable(clubName)) {
                throw new BadRequestException("Club name already taken: " + clubName);
            }
            club.setClubName(clubName);
        }

        // Update description if provided
        if (clubDescription != null) {
            club.setClubDescription(clubDescription);
        }

        // Save and return updated club
        club = clubRepository.save(club);

        return new ClubDTO(club);
    }

    /**
     * Changes the club head.
     * 
     * @param clubId    The club ID
     * @param newHeadId The user ID of the new club head
     * @return ClubDTO for the updated club
     * @throws BadRequestException if club or user is not found
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public ClubDTO changeClubHead(Long clubId, Long newHeadId) {
        Club club = findClubById(clubId);
        User newHead = findUserById(newHeadId);

        // Store the previous club head
        User previousHead = club.getClubHead();

        // Check if user is already a member
        boolean isMember = clubMemberRepository.existsByMemberUserIdAndClubClubIdAndIsActiveTrue(newHeadId, clubId);

        if (!isMember) {
            // Add as member if not already
            addClubMember(clubId, newHeadId);
        }

        // Check if new head is already an executive
        boolean isExecutive = clubExecutiveRepository.existsByUserUserIdAndClubClubIdAndIsActiveTrue(newHeadId, clubId);

        if (!isExecutive) {
            // Add as executive if not already
            addClubExecutive(clubId, newHeadId, "Club Head");
        } else {
            // Update the position of existing executive to Club Head
            ClubExecutive executive = clubExecutiveRepository
                    .findByUserUserIdAndClubClubIdAndIsActiveTrue(newHeadId, clubId)
                    .orElseThrow(() -> new BadRequestException("Executive not found"));
            executive.setPosition("Club Head");
            clubExecutiveRepository.save(executive);
        }

        // Update new head's role to CLUB_HEAD if not already
        if (newHead.getRole() != UserRole.CLUB_HEAD && newHead.getRole() != UserRole.ADMIN) {
            newHead.setRole(UserRole.CLUB_HEAD);
            userRepository.save(newHead);
        }

        // Update club head
        club.setClubHead(newHead);
        club = clubRepository.save(club);

        // Make the previous club head an executive with a different position if not
        // already
        if (previousHead != null && !previousHead.getUserId().equals(newHeadId)) {
            boolean isPreviousHeadExecutive = clubExecutiveRepository
                    .existsByUserUserIdAndClubClubIdAndIsActiveTrue(previousHead.getUserId(), clubId);

            if (isPreviousHeadExecutive) {
                // Update the position of the previous head
                ClubExecutive previousHeadExecutive = clubExecutiveRepository
                        .findByUserUserIdAndClubClubIdAndIsActiveTrue(previousHead.getUserId(), clubId)
                        .orElseThrow(() -> new BadRequestException("Executive not found"));
                previousHeadExecutive.setPosition("Former Club Head");
                clubExecutiveRepository.save(previousHeadExecutive);
            } else {
                // Add previous head as an executive
                ClubExecutive previousHeadExecutive = new ClubExecutive(previousHead, club, "Former Club Head");
                clubExecutiveRepository.save(previousHeadExecutive);
            }

            // Change role of previous club head to CLUB_EXECUTIVE if they're not an admin
            if (previousHead.getRole() == UserRole.CLUB_HEAD) {
                previousHead.setRole(UserRole.CLUB_EXECUTIVE);
                userRepository.save(previousHead);
            }
        }

        return new ClubDTO(club);
    }

    /**
     * Deactivates a club.
     * 
     * @param clubId The club ID
     * @return ClubDTO for the deactivated club
     * @throws BadRequestException if club is not found
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public ClubDTO deactivateClub(Long clubId) {
        Club club = findClubById(clubId);
        club.setActive(false);
        club = clubRepository.save(club);
        return new ClubDTO(club);
    }

    /**
     * Reactivates a club.
     * 
     * @param clubId The club ID
     * @return ClubDTO for the reactivated club
     * @throws BadRequestException if club is not found
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public ClubDTO reactivateClub(Long clubId) {
        Club club = findClubById(clubId);
        club.setActive(true);
        club = clubRepository.save(club);
        return new ClubDTO(club);
    }

    /**
     * Adds a club executive.
     * 
     * @param clubId   The club ID
     * @param userId   The user ID
     * @param position The executive position
     * @return The created ClubExecutive
     * @throws BadRequestException if club or user is not found or user is already
     *                             an executive
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public ClubExecutive addClubExecutive(Long clubId, Long userId, String position) {
        Club club = findClubById(clubId);
        User user = findUserById(userId);

        // Check if user is already an executive in this club
        if (clubExecutiveRepository.existsByUserUserIdAndClubClubIdAndIsActiveTrue(userId, clubId)) {
            throw new BadRequestException("User is already an executive in this club");
        }

        // Add user as a member if not already
        if (!clubMemberRepository.existsByMemberUserIdAndClubClubIdAndIsActiveTrue(userId, clubId)) {
            addClubMember(clubId, userId);
        }

        // Create and save the executive record
        ClubExecutive executive = new ClubExecutive(user, club, position);
        return clubExecutiveRepository.save(executive);
    }

    /**
     * Removes a club executive.
     * 
     * @param clubId The club ID
     * @param userId The user ID
     * @throws BadRequestException if club or user is not found or user is the club
     *                             head
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public void removeClubExecutive(Long clubId, Long userId) {
        Club club = findClubById(clubId);

        // Prevent removing the club head as executive
        if (club.getClubHead().getUserId().equals(userId)) {
            throw new BadRequestException("Cannot remove the club head as an executive");
        }

        // Find the executive record
        ClubExecutive executive = clubExecutiveRepository.findByUserUserIdAndClubClubId(userId, clubId)
                .orElseThrow(() -> new BadRequestException("User is not an executive in this club"));

        // Set as inactive and set leave date
        executive.setActive(false);
        executive.setLeaveDate(new Timestamp(System.currentTimeMillis()));
        clubExecutiveRepository.save(executive);
        
        // Update user role if they're not a club head or executive in any other club
        User user = executive.getUser();
        if (user.getRole() == UserRole.CLUB_EXECUTIVE) {
            // Check if user is still an executive in any other club
            boolean isStillExecutiveInOtherClubs = clubExecutiveRepository.existsByUserUserIdAndIsActiveTrueAndClubClubIdNot(userId, clubId);
            boolean isClubHeadInOtherClubs = clubRepository.existsByClubHeadUserIdAndClubIdNot(userId, clubId);
            
            if (!isStillExecutiveInOtherClubs && !isClubHeadInOtherClubs) {
                // Demote to regular user
                user.setRole(UserRole.USER);
                userRepository.save(user);
            }
        }
    }

    /**
     * Adds a club member.
     * 
     * @param clubId The club ID
     * @param userId The user ID
     * @return The created ClubMember
     * @throws BadRequestException if club or user is not found or user is already a
     *                             member
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public ClubMember addClubMember(Long clubId, Long userId) {
        Club club = findClubById(clubId);
        User user = findUserById(userId);

        // Check if user is already a member
        if (clubMemberRepository.existsByMemberUserIdAndClubClubIdAndIsActiveTrue(userId, clubId)) {
            throw new BadRequestException("User is already a member of this club");
        }

        // Create and save the member record
        ClubMember member = new ClubMember(club, user);
        return clubMemberRepository.save(member);
    }

    /**
     * Removes a club member.
     * 
     * @param clubId The club ID
     * @param userId The user ID
     * @throws BadRequestException if club or user is not found, user is not a
     *                             member, or is an executive
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public void removeClubMember(Long clubId, Long userId) {
        Club club = findClubById(clubId);

        // Prevent removing the club head as member
        if (club.getClubHead().getUserId().equals(userId)) {
            throw new BadRequestException("Cannot remove the club head as a member");
        }

        // Check if user is an executive
        if (clubExecutiveRepository.existsByUserUserIdAndClubClubIdAndIsActiveTrue(userId, clubId)) {
            throw new BadRequestException("Cannot remove an executive, demote them first");
        }

        // Find the member record
        ClubMember member = clubMemberRepository.findByMemberUserIdAndClubClubId(userId, clubId)
                .orElseThrow(() -> new BadRequestException("User is not a member of this club"));

        // Set as inactive and set leave date
        member.setActive(false);
        member.setLeaveDate(new Timestamp(System.currentTimeMillis()));
        clubMemberRepository.save(member);
    }

    /**
     * Registers a new club (pending approval).
     * Delegates to ClubRegistrationService.
     * 
     * @param request The club registration request
     * @return ClubDTO for the registered club (pending approval)
     * @throws BadRequestException if club name is taken or user is not found
     * @throws MessagingException  if there's an error sending the verification
     *                             email
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public ClubDTO registerClub(ClubRegistrationRequestDTO request) throws MessagingException {
        return clubRegistrationService.registerClub(request);
    }

    /**
     * Approves a club registration.
     * Delegates to AdminVerificationService.
     * 
     * @param clubId The club ID
     * @param token  The verification token
     * @return ClubDTO for the approved club
     * @throws BadRequestException if club is not found, token is invalid, or club
     *                             is not pending
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public ClubDTO approveClub(Long clubId, String token) {
        return adminVerificationService.approveClub(clubId, token);
    }

    /**
     * Rejects a club registration.
     * Delegates to AdminVerificationService.
     * 
     * @param clubId The club ID
     * @param token  The verification token
     * @param reason The reason for rejection
     * @return ClubDTO for the rejected club
     * @throws BadRequestException if club is not found, token is invalid, or club
     *                             is not pending
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public ClubDTO rejectClub(Long clubId, String token, String reason) {
        return adminVerificationService.rejectClub(clubId, token, reason);
    }

    /**
     * Checks if a user is a member of a club.
     * 
     * @param clubId The club ID
     * @param userId The user ID
     * @return true if the user is an active member of the club
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public boolean isUserMember(Long clubId, Long userId) {
        return clubMemberRepository.existsByMemberUserIdAndClubClubIdAndIsActiveTrue(userId, clubId);
    }

    /**
     * Checks if a user is an executive of a club.
     * 
     * @param clubId The club ID
     * @param userId The user ID
     * @return true if the user is an active executive of the club
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public boolean isUserExecutive(Long clubId, Long userId) {
        return clubExecutiveRepository.existsByUserUserIdAndClubClubIdAndIsActiveTrue(userId, clubId);
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
        return clubRepository.findById(clubId)
                .orElseThrow(() -> new BadRequestException("Club not found with ID: " + clubId));
    }

    /**
     * Helper method to find a user by ID.
     * 
     * @param userId The user ID
     * @return The user entity
     * @throws BadRequestException if user is not found
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found with ID: " + userId));
    }
}
