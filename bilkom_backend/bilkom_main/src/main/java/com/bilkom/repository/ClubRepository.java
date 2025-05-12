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
    List<Club> findByClubHead(User clubHead);
    boolean existsByClubName(String clubName);
    List<Club> findByStatus(ClubRegistrationStatus status);
    List<Club> findByIsActiveTrue();
    List<Club> findByIsActiveFalse();
    List<Club> findByIsActiveTrueAndStatus(ClubRegistrationStatus status);
    List<Club> findByIsActiveFalseAndStatus(ClubRegistrationStatus status);
    List<Club> findByStatusAndIsActive(ClubRegistrationStatus status, boolean isActive);
}
