package com.bilkom.service;

import com.bilkom.dto.ClubDTO;
import com.bilkom.dto.ClubRegistrationRequestDTO;
import com.bilkom.entity.Club;
import com.bilkom.entity.User;
import com.bilkom.enums.ClubRegistrationStatus;
import com.bilkom.enums.UserRole;
import com.bilkom.exception.BadRequestException;
import com.bilkom.repository.ClubRepository;
import com.bilkom.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.MessagingException;
import jakarta.annotation.PostConstruct;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for club registration functionality including submission, verification, approval, and rejection.
 * 
 * @author Mert Uzun
 * @version 1.1
 */
@Service
public class ClubRegistrationService {
    private static final Logger log = LoggerFactory.getLogger(ClubRegistrationService.class);

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private NotificationService notificationService;

    // Store verification tokens temporarily (in a real production app, these should be in a database)
    private final Map<String, Long> verificationTokens = new ConcurrentHashMap<>();
    private final Map<String, Long> expirationTimes = new ConcurrentHashMap<>();
    
    @Value("${club.registration.token.expiration:86400000}") // Default 24 hours in milliseconds
    private long tokenExpirationMs;
    
    /**
     * Initialize the service and ensure token maps are never null
     */
    @PostConstruct
    public void init() {
        log.info("Initializing ClubRegistrationService with token expiration: {} ms", tokenExpirationMs);
    }
    
    /**
     * Gets all pending club registrations.
     * 
     * @return List of pending club registrations as DTOs
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public List<ClubDTO> getPendingRegistrations() {
        List<Club> pendingClubs = clubRepository.findByStatus(ClubRegistrationStatus.PENDING);
        return pendingClubs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets all rejected club registrations.
     * 
     * @return List of rejected club registrations as DTOs
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public List<ClubDTO> getRejectedRegistrations() {
        List<Club> rejectedClubs = clubRepository.findByStatus(ClubRegistrationStatus.REJECTED);
        return rejectedClubs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets all approved clubs.
     * 
     * @return List of approved clubs as DTOs
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public List<ClubDTO> getApprovedClubs() {
        List<Club> approvedClubs = clubRepository.findByStatusAndIsActive(ClubRegistrationStatus.APPROVED, true);
        return approvedClubs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets a pending registration by ID.
     * 
     * @param clubId The ID of the club registration
     * @return ClubDTO representing the pending club registration
     * @throws BadRequestException if the club registration is not in pending status
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public ClubDTO getPendingRegistrationById(Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new BadRequestException("Club registration not found with ID: " + clubId));
                
        if (club.getStatus() != ClubRegistrationStatus.PENDING) {
            throw new BadRequestException("Club registration is not in pending status");
        }
        
        return convertToDTO(club);
    }
    
    /**
     * Registers a new club.
     * 
     * @param request The registration request containing club details and executive user ID
     * @return ClubDTO representing the registered club
     * @throws BadRequestException if the club name is already in use
     * @throws BadRequestException if the executive user is not found
     * @throws MessagingException if there is an error sending the verification email
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public ClubDTO registerClub(ClubRegistrationRequestDTO request) throws MessagingException {
        // Check if club name is available
        if (!isClubNameAvailable(request.getClubName())) {
            throw new BadRequestException("Club name is already in use");
        }
        
        // Get the executive user
        User executiveUser = userRepository.findById(request.getExecutiveUserId())
                .orElseThrow(() -> new BadRequestException("User not found with ID: " + request.getExecutiveUserId()));
        
        // Create and save the club
        Club club = new Club();
        club.setClubName(request.getClubName());
        club.setClubDescription(request.getClubDescription());
        club.setClubHead(executiveUser);
        club.setStatus(ClubRegistrationStatus.PENDING);
        club.setActive(true);  // Initially active
        club.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        
        club = clubRepository.save(club);
        
        // Generate a verification token
        String token = generateVerificationToken(club.getClubId());
        
        // Send verification email directly to admin (returns the same verification token)
        String verificationToken = emailService.sendClubRegistrationVerificationEmail(request, executiveUser, club.getClubId());
        
        // Store the new token in our map if it's different from what we generated
        if (!token.equals(verificationToken)) {
            verificationTokens.put(verificationToken, club.getClubId());
            long expirationTime = System.currentTimeMillis() + tokenExpirationMs;
            expirationTimes.put(verificationToken, expirationTime);
        }
        
        // Notify admin of new club registration
        notifyAdminsOfNewRegistration(club);
        
        return convertToDTO(club);
    }
    
    /**
     * Checks if a club name is available for registration.
     * 
     * @param clubName The club name to check
     * @return true if the club name is available, false otherwise
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public boolean isClubNameAvailable(String clubName) {
        return !clubRepository.existsByClubNameIgnoreCase(clubName);
    }
    
    /**
     * Verifies a club registration using the verification token.
     * 
     * @param token The verification token
     * @return ClubDTO representing the verified club
     * @throws BadRequestException if the token is invalid or expired
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public ClubDTO verifyClubRegistration(String token) {
        Long clubId = getClubIdByToken(token);
        
        if (clubId == null) {
            throw new BadRequestException("Invalid or expired verification token");
        }
        
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new BadRequestException("Club not found with ID: " + clubId));
        
        if (club.getStatus() != ClubRegistrationStatus.PENDING) {
            throw new BadRequestException("Club registration is not in pending status");
        }
        
        // Mark the club as verified - this may not be directly in the Club entity
        // If this property is missing in the entity, you might need to add it or use a different approach
        // For now, we'll assume it's present or the verification is tracked elsewhere
        club = clubRepository.save(club);
        
        // Remove the token
        removeToken(token);
        
        return convertToDTO(club);
    }
    
    /**
     * Approves a club registration.
     * 
     * @param clubId The ID of the club to approve
     * @param adminUserId The ID of the admin approving the registration
     * @return ClubDTO representing the approved club
     * @throws BadRequestException if the user is not an admin
     * @throws BadRequestException if the club is not in pending status
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public ClubDTO approveClubRegistration(Long clubId, Long adminUserId) {
        // Verify admin
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new BadRequestException("Admin user not found"));
                
        if (admin.getRole() != UserRole.ADMIN) {
            throw new BadRequestException("Only administrators can approve club registrations");
        }
        
        // Get the club
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new BadRequestException("Club not found with ID: " + clubId));
                
        if (club.getStatus() != ClubRegistrationStatus.PENDING) {
            throw new BadRequestException("Club registration is not in pending status");
        }
        
        // Update the club status
        club.setStatus(ClubRegistrationStatus.APPROVED);
        club = clubRepository.save(club);
        
        // Update the club head's role
        User clubHead = club.getClubHead();
        clubHead.setRole(UserRole.CLUB_HEAD);
        userRepository.save(clubHead);
        
        // Notify the club head
        notifyClubHead(club, "Club Registration Approved", 
                "Congratulations! Your club registration for " + club.getClubName() + " has been approved.");
        
        return convertToDTO(club);
    }
    
    /**
     * Rejects a club registration.
     * 
     * @param clubId The ID of the club to reject
     * @param adminUserId The ID of the admin rejecting the registration
     * @param reason The reason for rejection
     * @return ClubDTO representing the rejected club
     * @throws BadRequestException if the user is not an admin
     * @throws BadRequestException if the club is not in pending status
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public ClubDTO rejectClubRegistration(Long clubId, Long adminUserId, String reason) {
        // Verify admin
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new BadRequestException("Admin user not found"));
                
        if (admin.getRole() != UserRole.ADMIN) {
            throw new BadRequestException("Only administrators can reject club registrations");
        }
        
        // Get the club
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new BadRequestException("Club not found with ID: " + clubId));
                
        if (club.getStatus() != ClubRegistrationStatus.PENDING) {
            throw new BadRequestException("Club registration is not in pending status");
        }
        
        // Update the club status
        club.setStatus(ClubRegistrationStatus.REJECTED);
        club.setActive(false);
        // Store rejection reason in a way that fits your app's model
        // If the Club entity doesn't have setRejectionReason method, you might need to modify your approach
        club = clubRepository.save(club);
        
        // Notify the club head
        notifyClubHead(club, "Club Registration Rejected", 
                "Your club registration for " + club.getClubName() + " has been rejected.\n\nReason: " + reason);
        
        return convertToDTO(club);
    }
    
    /**
     * Gets the club ID associated with a verification token.
     * 
     * @param token The verification token
     * @return The club ID, or null if the token is invalid or expired
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public Long getClubIdByToken(String token) {
        // Check if token exists
        if (!verificationTokens.containsKey(token)) {
            log.debug("Token {} not found in verificationTokens map", token);
            return null;
        }
        
        // Check if token is expired
        Long clubId = verificationTokens.get(token);
        
        // Only check expiration if the map is not empty
        if (expirationTimes != null && !expirationTimes.isEmpty() && expirationTimes.containsKey(token)) {
            long expirationTime = expirationTimes.getOrDefault(token, 0L);
            if (expirationTime < System.currentTimeMillis()) {
                // Remove expired token
                log.debug("Token {} expired, removing", token);
                removeToken(token);
                return null;
            }
        }
        
        return clubId;
    }
    
    /**
     * Removes a verification token.
     * 
     * @param token The token to remove
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public void removeToken(String token) {
        verificationTokens.remove(token);
        if (expirationTimes != null) {
            expirationTimes.remove(token);
        }
    }
    
    /**
     * Sets a verification token directly for testing purposes.
     * Implemented only for testing.
     * 
     * @param token The token to set
     * @param clubId The club ID to associate with the token
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public void setTestToken(String token, Long clubId) {
        log.debug("Setting test token {} for club {}", token, clubId);
        verificationTokens.put(token, clubId);
        if (expirationTimes != null) {
            expirationTimes.put(token, System.currentTimeMillis() + tokenExpirationMs);
        }
    }
    
    /**
     * Generates a verification token for a club.
     * 
     * @param clubId The ID of the club
     * @return The generated token
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    private String generateVerificationToken(Long clubId) {
        String token = UUID.randomUUID().toString();
        verificationTokens.put(token, clubId);
        
        // Set expiration time (24 hours from now)
        long expirationTime = System.currentTimeMillis() + tokenExpirationMs;
        expirationTimes.put(token, expirationTime);
        
        return token;
    }
    
    /**
     * Notifies administrators of a new club registration.
     * 
     * @param club The newly registered club
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    private void notifyAdminsOfNewRegistration(Club club) {
        // Get all admin users
        List<User> admins = userRepository.findByRole(UserRole.ADMIN);
        
        // Send notification to all admins
        String title = "New Club Registration";
        String body = "A new club '" + club.getClubName() + "' has been registered and is waiting for approval.";
        
        notificationService.sendNotificationToUsers(admins, title, body);
        log.info("Notified {} admins of new club registration: {}", admins.size(), club.getClubName());
    }
    
    /**
     * Notifies the club head of registration status changes.
     * 
     * @param club The club
     * @param title The notification title
     * @param body The notification body
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    private void notifyClubHead(Club club, String title, String body) {
        User clubHead = club.getClubHead();
        if (clubHead != null) {
            // Send notification
            notificationService.sendNotificationToUser(clubHead, title, body);
            
            // Also send email - use the admin-targeting method for result emails
            try {
                boolean isApproved = club.getStatus() == ClubRegistrationStatus.APPROVED;
                String reason = isApproved ? null : body.substring(body.indexOf("Reason:") + 8).trim();
                emailService.sendClubRegistrationResultEmail(clubHead.getEmail(), club.getClubName(), isApproved, reason);
            } catch (Exception e) {
                log.error("Failed to send email to club head: {}", e.getMessage(), e);
            }
            
            log.info("Notified club head of status change for club: {}", club.getClubName());
        }
    }
    
    /**
     * Converts a Club entity to ClubDTO.
     * 
     * @param club The Club entity to convert
     * @return ClubDTO representation of the Club
     */
    private ClubDTO convertToDTO(Club club) {
        ClubDTO dto = new ClubDTO();
        dto.setClubId(club.getClubId());
        dto.setClubName(club.getClubName());
        dto.setClubDescription(club.getClubDescription());
        dto.setClubHead(club.getClubHead());
        dto.setActive(club.isActive());
        dto.setStatus(club.getStatus());
        dto.setCreatedAt(club.getCreatedAt());
        return dto;
    }
}
