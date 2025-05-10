package com.bilkom.repository;

import com.bilkom.entity.ClubMembershipRequest;
import com.bilkom.entity.ClubMembershipRequest.RequestStatus;
import com.bilkom.entity.Club;
import com.bilkom.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for ClubMembershipRequest entity.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
@Repository
public interface ClubMembershipRequestRepository extends JpaRepository<ClubMembershipRequest, Long> {
    
    /**
     * Finds all membership requests for a specific club.
     * 
     * @param clubId The club ID
     * @return List of membership requests
     */
    List<ClubMembershipRequest> findByClubClubId(Long clubId);
    
    /**
     * Finds all membership requests with a specific status for a club.
     * 
     * @param clubId The club ID
     * @param status The request status
     * @return List of membership requests
     */
    List<ClubMembershipRequest> findByClubClubIdAndStatus(Long clubId, RequestStatus status);
    
    /**
     * Finds all membership requests from a specific user.
     * 
     * @param userId The user ID
     * @return List of membership requests
     */
    List<ClubMembershipRequest> findByUserUserId(Long userId);


    /**
     * Finds a membership request by member and club.
     * 
     * @param user The member
     * @param club The club
     * @return the membership request
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    ClubMembershipRequest findByUserAndClub(User user, Club club);
    
    /**
     * Finds all membership requests with a specific status from a user.
     * 
     * @param userId The user ID
     * @param status The request status
     * @return List of membership requests
     */
    List<ClubMembershipRequest> findByUserUserIdAndStatus(Long userId, RequestStatus status);
    
    /**
     * Finds a specific membership request by user and club.
     * 
     * @param userId The user ID
     * @param clubId The club ID
     * @return Optional containing the request if found
     */
    Optional<ClubMembershipRequest> findByUserUserIdAndClubClubId(Long userId, Long clubId);
    
    /**
     * Checks if a pending request exists for a user and club.
     * 
     * @param userId The user ID
     * @param clubId The club ID
     * @return true if a pending request exists
     */
    boolean existsByUserUserIdAndClubClubIdAndStatus(Long userId, Long clubId, RequestStatus status);
    

} 