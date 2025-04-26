package com.bilkom.service;

import com.bilkom.dto.ClubDTO;
import com.bilkom.dto.ClubRegistrationRequestDTO;
import com.bilkom.entity.Club;
import com.bilkom.entity.User;
import com.bilkom.enums.ClubRegistrationStatus;
import com.bilkom.exception.BadRequestException;
import com.bilkom.repository.ClubRepository;
import com.bilkom.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.MessagingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for handling club registration processes.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
@Service
public class ClubRegistrationService {

    @Autowired
    private ClubRepository clubRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService;
    
    // Store verification tokens temporarily until approval/rejection
    private final Map<String, Long> verificationTokens = new HashMap<>();
    
    /**
     * Retrieves pending club registrations.
     * 
     * @return List of clubs with pending status
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public List<ClubDTO> getPendingRegistrations() {
        return clubRepository.findByStatus(ClubRegistrationStatus.PENDING).stream().map(ClubDTO::new).collect(Collectors.toList());
    }
    
    /**
     * Retrieves rejected club registrations.
     * 
     * @return List of clubs with rejected status
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public List<ClubDTO> getRejectedRegistrations() {
        return clubRepository.findByStatus(ClubRegistrationStatus.REJECTED).stream().map(ClubDTO::new).collect(Collectors.toList());
    }
    
    /**
     * Retrieves a specific pending club registration by ID.
     * 
     * @param clubId The club ID
     * @return ClubDTO for the requested pending club
     * @throws BadRequestException if club is not found or not in pending status
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public ClubDTO getPendingRegistrationById(Long clubId) {
        Club club = clubRepository.findById(clubId).orElseThrow(() -> new BadRequestException("Club not found with ID: " + clubId));
        
        if (club.getStatus() != ClubRegistrationStatus.PENDING) {
            throw new BadRequestException("Club is not in pending status");
        }
        
        return new ClubDTO(club);
    }
    
    /**
     * Registers a new club (pending approval).
     * 
     * @param request The club registration request
     * @return ClubDTO for the registered club (pending approval)
     * @throws BadRequestException if club name is taken or user is not found
     * @throws MessagingException if there's an error sending the verification email
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public ClubDTO registerClub(ClubRegistrationRequestDTO request) throws MessagingException {
        // Validate club name
        if (clubRepository.existsByClubName(request.getClubName())) {
            throw new BadRequestException("Club name already taken: " + request.getClubName());
        }
        
        // Find the executive user
        User executiveUser = findUserById(request.getExecutiveUserId());
        
        // Create the club with PENDING status
        Club club = new Club(request.getClubName(), request.getClubDescription(), executiveUser);
        club.setStatus(ClubRegistrationStatus.PENDING);
        club = clubRepository.save(club);
        
        // Send verification email to admin
        String verificationToken = emailService.sendClubRegistrationVerificationEmail(
                request, executiveUser, club.getClubId());
        
        // Store token temporarily
        verificationTokens.put(verificationToken, club.getClubId());
        
        return new ClubDTO(club);
    }

    /**
     * Gets a verification token for a club.
     * 
     * @param token The verification token
     * @return The associated club ID or null if token is invalid
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public Long getClubIdByToken(String token) {
        return verificationTokens.get(token);
    }
    
    /**
     * Removes a verification token after processing.
     * 
     * @param token The verification token to remove
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public void removeToken(String token) {
        verificationTokens.remove(token);
    }
    
    /**
     * Checks if a club name is available.
     * 
     * @param clubName The club name to check
     * @return true if the name is available, false otherwise
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public boolean isClubNameAvailable(String clubName) {
        return !clubRepository.existsByClubName(clubName);
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
        return userRepository.findById(userId).orElseThrow(() -> 
                new BadRequestException("User not found with ID: " + userId));
    }
}
