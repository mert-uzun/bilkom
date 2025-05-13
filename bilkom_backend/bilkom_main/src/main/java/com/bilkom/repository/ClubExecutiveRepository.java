package com.bilkom.repository;

import com.bilkom.entity.ClubExecutive;
import com.bilkom.entity.Club;
import com.bilkom.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ClubExecutive entity.
 * Provides methods for managing ClubExecutive records in the database.
 * 
 * @author Mert Uzun
 * @version 1.0
 * @since 2025-04-26
 */
@Repository
public interface ClubExecutiveRepository extends JpaRepository<ClubExecutive, User> {
    
    /**
     * Finds all club executives by club.
     * 
     * @param club The club to find executives for
     * @return List of club executives
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    List<ClubExecutive> findByClubClubId(Long clubId);

    /**
     * Finds a club executive by user and club.
     * 
     * @param user The user to find the executive for
     * @param club The club to find the executive for
     * @return the club executive
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    Optional<ClubExecutive> findByUserUserIdAndClubClubId(Long userId, Long clubId);

    /**
     * Finds all active club executives by club.
     * 
     * @param club The club to find active executives for
     * @return List of active club executives
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    List<ClubExecutive> findByClubAndIsActiveTrue(Club club);

    /**
     * Finds all active club executives by user.
     * 
     * @param user The user to find active executives for
     * @return List of active club executives
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    List<ClubExecutive> findByUserAndIsActiveTrue(User user);

    /**
     * Finds all active club executives by user ID.
     * 
     * @param userId The ID of the user to find active executives for
     * @return List of active club executives
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    List<ClubExecutive> findByUserUserIdAndIsActiveTrue(Long userId);

    /**
     * Checks if a user is an active club executive for a club.
     * 
     * @param user The user to check
     * @param club The club to check
     * @return true if the user is an active club executive for the club, false otherwise
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    boolean existsByUserAndClubAndIsActiveTrue(User user, Club club);

    /**
     * Checks if a user is an active club executive for a club by user ID and club ID.
     * 
     * @param userId The ID of the user to check
     * @param clubId The ID of the club to check
     * @return true if the user is an active club executive for the club, false otherwise
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    boolean existsByUserUserIdAndClubClubIdAndIsActiveTrue(Long userId, Long clubId);

    /**
     * Checks if a user is an active club executive for a club by user ID and club ID.
     * 
     * @param userId The ID of the user to check
     * @param clubId The ID of the club to check
     * @return true if the user is an active club executive for the club, false otherwise
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    boolean existsByUserUserIdAndClubClubId(Long userId, Long clubId);

    /**
     * Finds a club executive by user ID and club ID and is active true.
     * 
     * @param userId The ID of the user to check
     * @param clubId The ID of the club to check
     * @return the club executive
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    Optional<ClubExecutive> findByUserUserIdAndClubClubIdAndIsActiveTrue(Long userId, Long clubId);

    /**
     * Finds all club executives by club.
     * 
     * @param club The club to find executives for
     * @return List of club executives
     * 
     * @author Mert Uzun
     * @version 1.0
     */
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
