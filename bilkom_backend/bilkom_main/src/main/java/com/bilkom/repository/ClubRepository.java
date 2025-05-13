package com.bilkom.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bilkom.entity.Club;
import com.bilkom.entity.User;
import com.bilkom.enums.ClubRegistrationStatus;

/**
 * Repository for Club entity.
 * Provides methods for managing Club records in the database.
 * 
 * @author Mert Uzun
 * @version 1.0.0
 * @since 2025-04-26
 */
@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {
    Optional<Club> findByClubName(String clubName); 
    Optional<Club> findByClubId(Long clubId);
    Optional<Club> findByClubHead(User clubHead);
    boolean existsByClubName(String clubName);
    List<Club> findByStatus(ClubRegistrationStatus status);
    List<Club> findByIsActiveTrue();
    List<Club> findByIsActiveFalse();
    List<Club> findByIsActiveTrueAndStatus(ClubRegistrationStatus status);
    List<Club> findByIsActiveFalseAndStatus(ClubRegistrationStatus status);
    List<Club> findByStatusAndIsActive(ClubRegistrationStatus status, boolean isActive);
    boolean existsByClubNameIgnoreCase(String clubName);
    
    /**
     * Counts clubs by club head, status, and active status.
     *
     * @param clubHead the club head user
     * @param status the club registration status
     * @param isActive whether the club is active
     * @return the count of clubs matching the criteria
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    long countByClubHeadAndStatusAndIsActive(User clubHead, ClubRegistrationStatus status, boolean isActive);

    /**
     * Checks if a user is a club head in any club other than the specified one.
     *
     * @param userId the user ID
     * @param clubId the club ID to exclude
     * @return true if the user is a club head in any other club
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    boolean existsByClubHeadUserIdAndClubIdNot(Long userId, Long clubId);
}
