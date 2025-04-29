package com.bilkom.service;

import com.bilkom.dto.ClubDTO;
import com.bilkom.dto.ClubMemberDTO;
import com.bilkom.entity.Club;
import com.bilkom.entity.ClubMember;
import com.bilkom.exception.BadRequestException;
import com.bilkom.repository.ClubMemberRepository;
import com.bilkom.repository.ClubRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for club member management operations.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
@Service
public class ClubMemberService {

    @Autowired
    private ClubMemberRepository clubMemberRepository;
    
    @Autowired
    private ClubRepository clubRepository;
    
    @Autowired
    private ClubService clubService;

    /**
     * Gets all active members for a specific club.
     * 
     * @param clubId The club ID
     * @return List of ClubMemberDTO objects
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public List<ClubMemberDTO> getActiveClubMembers(Long clubId) {
        Club club = findClubById(clubId);
        return clubMemberRepository.findByClubAndIsActiveTrue(club).stream().map(ClubMemberDTO::new).collect(Collectors.toList());
    }
    
    /**
     * Gets all members for a specific club including inactive ones.
     * 
     * @param clubId The club ID
     * @return List of ClubMemberDTO objects
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public List<ClubMemberDTO> getAllClubMembers(Long clubId) {
        Club club = findClubById(clubId);
        return clubMemberRepository.findByClub(club).stream().map(ClubMemberDTO::new).collect(Collectors.toList());
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
    public List<ClubDTO> getClubsByMember(Long userId) {
        return clubService.getClubsByMemberId(userId);
    }
    
    /**
     * Gets a specific member by user ID and club ID.
     * 
     * @param userId The user ID
     * @param clubId The club ID
     * @return ClubMemberDTO object
     * @throws BadRequestException if member is not found
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public ClubMemberDTO getMember(Long userId, Long clubId) {
        ClubMember member = findMember(userId, clubId);
        return new ClubMemberDTO(member);
    }
    
    /**
     * Adds a new member to a club.
     * Delegates to ClubService for the actual operation to ensure business rules are followed.
     * 
     * @param clubId The club ID
     * @param userId The user ID
     * @return ClubMemberDTO representing the new member
     * @throws BadRequestException if club or user is not found or user is already a member
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public ClubMemberDTO addMember(Long clubId, Long userId) {
        ClubMember member = clubService.addClubMember(clubId, userId);
        return new ClubMemberDTO(member);
    }
    
    /**
     * Adds multiple members to a club at once.
     * 
     * @param clubId The club ID
     * @param userIds List of user IDs to add as members
     * @return List of ClubMemberDTO objects for successfully added members
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public List<ClubMemberDTO> addMembers(Long clubId, List<Long> userIds) {
        return userIds.stream().filter(userId -> !clubMemberRepository.existsByMemberUserIdAndClubClubIdAndIsActiveTrue(userId, clubId)).map(userId -> {
                    try {
                        return clubService.addClubMember(clubId, userId);
                    } catch (BadRequestException e) {
                        // Skip that user if it cannot be added
                        return null;
                    }
                })
                .filter(member -> member != null).map(ClubMemberDTO::new).collect(Collectors.toList());
    }
    
    /**
     * Removes a member from a club.
     * Delegates to ClubService for the actual operation to ensure business rules are followed.
     * 
     * @param clubId The club ID
     * @param userId The user ID
     * @throws BadRequestException if club or user is not found, user is not a member, or is an executive
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public void removeMember(Long clubId, Long userId) {
        clubService.removeClubMember(clubId, userId);
    }
    
    /**
     * Reactivates a previously deactivated member.
     * 
     * @param userId The user ID
     * @param clubId The club ID
     * @return ClubMemberDTO for the reactivated member
     * @throws BadRequestException if member is not found or is already active
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public ClubMemberDTO reactivateMember(Long userId, Long clubId) {
        
        // Find the member record (including inactive records)
        ClubMember member = clubMemberRepository.findByMemberUserIdAndClubClubId(userId, clubId)
                .orElseThrow(() -> new BadRequestException("User was never a member of this club"));
        
        // Check if already active
        if (member.isActive()) {
            throw new BadRequestException("Member is already active");
        }
        
        // Reactivate
        member.setActive(true);
        member.setLeaveDate(null);
        member.setJoinDate(new Timestamp(System.currentTimeMillis()));
        
        // Save changes
        member = clubMemberRepository.save(member);
        
        return new ClubMemberDTO(member);
    }
    
    /**
     * Gets member history for a club.
     * Returns all members (active and inactive) with their join/leave dates.
     * 
     * @param clubId The club ID
     * @return List of ClubMemberDTO objects with full history
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public List<ClubMemberDTO> getMemberHistory(Long clubId) {
        Club club = findClubById(clubId);
        return clubMemberRepository.findByClub(club).stream().map(ClubMemberDTO::new).collect(Collectors.toList());
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
    public long getActiveMemberCount(Long clubId) {
        return clubMemberRepository.countByClubClubIdAndIsActiveTrue(clubId);
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
    public List<ClubMemberDTO> searchMembersByName(Long clubId, String namePattern) {
        Club club = findClubById(clubId);
        
        return clubMemberRepository.findByClubAndMemberFirstNameContainingOrMemberLastNameContaining(club, namePattern, namePattern)
                .stream().filter(ClubMember::isActive).map(ClubMemberDTO::new).collect(Collectors.toList());
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
     * Helper method to find a member by user ID and club ID.
     * 
     * @param userId The user ID
     * @param clubId The club ID
     * @return The member entity
     * @throws BadRequestException if member is not found
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    private ClubMember findMember(Long userId, Long clubId) {
        return clubMemberRepository.findByMemberUserIdAndClubClubIdAndIsActiveTrue(userId, clubId).stream().findFirst().orElseThrow(() -> new BadRequestException("Member not found"));
    }
}
