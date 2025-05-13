package com.bilkom.repository;

import com.bilkom.entity.ClubExecutive;
import com.bilkom.entity.ClubExecutiveId;
import com.bilkom.entity.Club;
import com.bilkom.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClubExecutiveRepository extends JpaRepository<ClubExecutive, ClubExecutiveId> {

    List<ClubExecutive> findByClubClubId(Long clubId);
    Optional<ClubExecutive> findByUserUserIdAndClubClubId(Long userId, Long clubId);
    Optional<ClubExecutive> findByUserAndClub(User user, Club club);

    List<ClubExecutive> findByClubAndIsActiveTrue(Club club);
    List<ClubExecutive> findByUserAndIsActiveTrue(User user);
    List<ClubExecutive> findByUserUserIdAndIsActiveTrue(Long userId);

    boolean existsByUserAndClubAndIsActiveTrue(User user, Club club);
    boolean existsByUserUserIdAndClubClubIdAndIsActiveTrue(Long userId, Long clubId);
    boolean existsByUserUserIdAndClubClubId(Long userId, Long clubId);

    Optional<ClubExecutive> findByUserUserIdAndClubClubIdAndIsActiveTrue(Long userId, Long clubId);

    List<ClubExecutive> findByClub(Club club);

    /**
     * Checks if a user is an active executive in any club other than the specified one.
     * 
     * @param userId the user ID
     * @param clubId the club ID to exclude
     * @return true if the user is an active executive in any other club
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    boolean existsByUserUserIdAndIsActiveTrueAndClubClubIdNot(Long userId, Long clubId);
}
