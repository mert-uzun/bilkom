package com.bilkom.service;

import com.bilkom.dto.ClubDTO;
import com.bilkom.dto.ClubExecutiveDTO;
import com.bilkom.entity.Club;
import com.bilkom.entity.ClubExecutive;
import com.bilkom.entity.User;
import com.bilkom.enums.UserRole;
import com.bilkom.exception.BadRequestException;
import com.bilkom.repository.ClubExecutiveRepository;
import com.bilkom.repository.ClubRepository;
import com.bilkom.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for club executive management operations.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
@Service
public class ClubExecutiveService {

    @Autowired
    private ClubExecutiveRepository clubExecutiveRepository;
    
    @Autowired
    private ClubRepository clubRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ClubService clubService;

    /**
     * Gets all active executives for a specific club.
     * 
     * @param clubId The club ID
     * @return List of ClubExecutiveDTO objects
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public List<ClubExecutiveDTO> getActiveClubExecutives(Long clubId) {
        Club club = findClubById(clubId);
        return clubExecutiveRepository.findByClubAndIsActiveTrue(club).stream().map(ClubExecutiveDTO::new).collect(Collectors.toList());
    }
    
    /**
     * Gets all executives for a specific club including inactive ones.
     * 
     * @param clubId The club ID
     * @return List of ClubExecutiveDTO objects
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public List<ClubExecutiveDTO> getAllClubExecutives(Long clubId) {
        Club club = findClubById(clubId);
        return clubExecutiveRepository.findByClub(club).stream().map(ClubExecutiveDTO::new).collect(Collectors.toList());
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
    public List<ClubDTO> getClubsByExecutive(Long userId) {
        return clubService.getClubsByExecutiveId(userId);
    }
    
    /**
     * Gets a specific executive by user ID and club ID.
     * 
     * @param userId The user ID
     * @param clubId The club ID
     * @return ClubExecutiveDTO object
     * @throws BadRequestException if executive is not found
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public ClubExecutiveDTO getExecutive(Long userId, Long clubId) {
        ClubExecutive executive = findExecutive(userId, clubId);
        return new ClubExecutiveDTO(executive);
    }
    
    /**
     * Adds a new executive to a club.
     * Delegates to ClubService for the actual operation to ensure business rules are followed.
     * 
     * @param clubId The club ID
     * @param userId The user ID
     * @param position The executive position
     * @return ClubExecutiveDTO representing the new executive
     * @throws BadRequestException if club or user is not found or user is already an executive
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public ClubExecutiveDTO addExecutive(Long clubId, Long userId, String position) {
        // Check if user already has a role higher than CLUB_EXECUTIVE
        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException("User not found"));
        
        // Only promote if user is a regular user
        if (user.getRole() == UserRole.USER) {
            // Promote to CLUB_EXECUTIVE role
            user.setRole(UserRole.CLUB_EXECUTIVE);
            userRepository.save(user);
        }
        
        // Add as executive
        ClubExecutive executive = clubService.addClubExecutive(clubId, userId, position);
        return new ClubExecutiveDTO(executive);
    }
    
    /**
     * Updates an executive's position.
     * Can't update club head position, use changeClubHead method instead.
     * 
     * @param userId The user ID
     * @param clubId The club ID
     * @param newPosition The new position
     * @return ClubExecutiveDTO with updated position
     * @throws BadRequestException if executive is not found
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public ClubExecutiveDTO updateExecutivePosition(Long userId, Long clubId, String newPosition) {
        // Find the executive
        ClubExecutive executive = findExecutive(userId, clubId);
        
        // Prevent updating club head position from outside changeClubHead method
        if (executive.getPosition().equals("Club Head")) {
            Club club = executive.getClub();
            if (club.getClubHead().getUserId().equals(userId)) {
                throw new BadRequestException("Cannot change Club Head position. Use changeClubHead method instead.");
            }
        }
        
        // Update position
        executive.setPosition(newPosition);
        executive = clubExecutiveRepository.save(executive);
        
        return new ClubExecutiveDTO(executive);
    }
    
    /**
     * Removes an executive from a club.
     * Delegates to ClubService for the actual operation to ensure business rules are followed.
     * 
     * @param clubId The club ID
     * @param userId The user ID
     * @throws BadRequestException if club or user is not found or user is the club head
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public void removeExecutive(Long clubId, Long userId) {
        clubService.removeClubExecutive(clubId, userId);
    }
    
    /**
     * Reactivates a previously deactivated executive.
     * 
     * @param userId The user ID
     * @param clubId The club ID
     * @param position The new position
     * @return ClubExecutiveDTO for the reactivated executive
     * @throws BadRequestException if executive is not found or is already active
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public ClubExecutiveDTO reactivateExecutive(Long userId, Long clubId, String position) {
        
        // Find the executive record (including inactive records)
        ClubExecutive executive = clubExecutiveRepository.findByUserUserIdAndClubClubIdAndIsActiveTrue(userId, clubId)
                .orElseThrow(() -> new BadRequestException("User was never an executive in this club"));
        
        // Check if already active
        if (executive.isActive()) {
            throw new BadRequestException("Executive is already active");
        }
        
        // Reactivate
        executive.setActive(true);
        executive.setLeaveDate(null);
        executive.setPosition(position);
        executive.setJoinDate(new Timestamp(System.currentTimeMillis())); // Reset join date to now
        
        // Save changes
        executive = clubExecutiveRepository.save(executive);
        
        return new ClubExecutiveDTO(executive);
    }
    
    /**
     * Gets executive history for a club.
     * Returns all executives (active and inactive) with their position histories.
     * 
     * @param clubId The club ID
     * @return List of ClubExecutiveDTO objects with full history
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public List<ClubExecutiveDTO> getExecutiveHistory(Long clubId) {
        Club club = findClubById(clubId);
        return clubExecutiveRepository.findByClub(club).stream().map(ClubExecutiveDTO::new).collect(Collectors.toList());
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
     * Helper method to find an executive by user ID and club ID.
     * 
     * @param userId The user ID
     * @param clubId The club ID
     * @return The executive entity
     * @throws BadRequestException if executive is not found
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    private ClubExecutive findExecutive(Long userId, Long clubId) {
        return clubExecutiveRepository.findByUserUserIdAndClubClubIdAndIsActiveTrue(userId, clubId)
                .stream().findFirst().orElseThrow(() -> new BadRequestException("Executive not found"));
    }
}
