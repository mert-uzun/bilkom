package com.bilkom.repository;

import com.bilkom.entity.ClubMember;
import com.bilkom.entity.Club;
import com.bilkom.entity.User;
import com.bilkom.entity.ClubMemberPK;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ClubMember entity.
 * Provides methods for managing ClubMember records in the database.
 * 
 * @author Mert Uzun
 * @version 1.0
 * @since 2025-04-26
 */
@Repository
public interface ClubMemberRepository extends JpaRepository<ClubMember, ClubMemberPK> {

    /**
     * Finds a club member by user and club.
     * 
     * @param user The user to find the member for
     * @param club The club to find the member for
     * @return the club member
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    ClubMember findByMemberAndClub(User user, Club club);

    /**
     * Finds all club members by club.
     * 
     * @param club The club to find members for
     * @return List of club members
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    List<ClubMember> findByClub(Club club);

    /**
     * Finds all club members by club ID.
     * 
     * @param clubId The ID of the club to find members for
     * @return List of club members
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    List<ClubMember> findByClubClubId(Long clubId);

    /**
     * Finds all active club members by club.
     * 
     * @param club The club to find active members for
     * @return List of active club members
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    List<ClubMember> findByClubAndIsActiveTrue(Club club);

    /**
     * Finds a club member by user and club.
     * 
     * @param user The user to find the member for
     * @param club The club to find the member for
     * @return the club member
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    Optional<ClubMember> findByMemberUserIdAndClubClubId(Long userId, Long clubId);

    /**
     * Finds all club members by user.
     * 
     * @param user The user to find members for
     * @return List of club members
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    List<ClubMember> findByMember(User user);

    /**
     * Finds all club members by user ID.
     * 
     * @param userId The ID of the user to find members for
     * @return List of club members
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    List<ClubMember> findByMemberUserId(Long userId);

    /**
     * Finds all active club members by user.
     * 
     * @param user The user to find active members for
     * @return List of active club members
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    List<ClubMember> findByMemberAndIsActiveTrue(User user);

    /**
     * Finds all active club members by user ID.
     * 
     * @param userId The ID of the user to find active members for
     * @return List of active club members
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    List<ClubMember> findByMemberUserIdAndIsActiveTrue(Long userId);


    /**
     * Checks if a user is an active club member for a club.
     * 
     * @param user The user to check
     * @param club The club to check
     * @return true if the user is an active club member for the club, false otherwise
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    boolean existsByMemberAndClubAndIsActiveTrue(User user, Club club);


    /**
     * Checks if a user is an active club member for a club by user ID and club ID.
     * 
     * @param userId The ID of the user to check
     * @param clubId The ID of the club to check
     * @return true if the user is an active club member for the club, false otherwise
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    boolean existsByMemberUserIdAndClubClubIdAndIsActiveTrue(Long userId, Long clubId);

    /**
     * Counts all active club members by club.
     * 
     * @param club The club to count active members for
     * @return the number of active club members
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    long countByClubAndIsActiveTrue(Club club);

    /**
     * Counts all active club members by club ID.
     * 
     * @param clubId The ID of the club to count active members for
     * @return the number of active club members
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    long countByClubClubIdAndIsActiveTrue(Long clubId);

    /**
     * Finds all club members by club and member first name or last name.
     * 
     * @param club The club to find members for
     * @param firstName The first name of the member to find
     * @param lastName The last name of the member to find
     * @return List of club members
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    List<ClubMember> findByClubAndMemberFirstNameContainingOrMemberLastNameContaining(Club club, String firstName, String lastName);

    /**
     * Finds all active club members by user ID and club ID.
     * 
     * @param userId The ID of the user to find active members for
     * @param clubId The ID of the club to find active members for
     * @return List of active club members
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    List<ClubMember> findByMemberUserIdAndClubClubIdAndIsActiveTrue(Long userId, Long clubId);

}