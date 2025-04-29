package com.bilkom.repository;

import com.bilkom.entity.User;
import com.bilkom.enums.UserRole;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity.
 * Provides methods for managing User records in the database.
 * 
 * @author Mert Uzun
 * @version 1.0
 * @since 2025-04-26
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     * 
     * @param email the email address
     * @return optional containing the user if found
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Checks if a user with the given email exists.
     * 
     * @param email the email address
     * @return true if the email exists, false otherwise
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    boolean existsByEmail(String email);
    
    /**
     * Finds a user by verification token.
     * 
     * @param token the verification token
     * @return optional containing the user if found
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    Optional<User> findByVerificationToken(String token);
    
    /**
     * Finds users by blood type.
     * 
     * @param bloodType the blood type
     * @return list of users with the specified blood type
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    List<User> findByBloodType(String bloodType);
    
    /**
     * Finds a user by first and last name.
     * 
     * @param firstName the first name
     * @param lastName the last name
     * @return optional containing the user if found
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    Optional<User> findByFirstNameAndLastName(String firstName, String lastName);
    
    /**
     * Checks if a user with the given first and last name exists.
     * 
     * @param firstName the first name
     * @param lastName the last name
     * @return true if a user with the given name exists, false otherwise
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    boolean existsByFirstNameAndLastName(String firstName, String lastName);
    
    /**
     * Finds a user by Bilkent ID.
     * 
     * @param bilkentId the Bilkent ID
     * @return optional containing the user if found
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    Optional<User> findByBilkentId(String bilkentId);
    
    /**
     * Checks if a user with the given Bilkent ID exists.
     * 
     * @param bilkentId the Bilkent ID
     * @return true if a user with the given Bilkent ID exists, false otherwise
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    boolean existsByBilkentId(String bilkentId);
    
    /**
     * Finds a user by phone number.
     * 
     * @param phoneNumber the phone number
     * @return optional containing the user if found
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    Optional<User> findByPhoneNumber(String phoneNumber);
    
    /**
     * Checks if a user with the given phone number exists.
     * 
     * @param phoneNumber the phone number
     * @return true if a user with the given phone number exists, false otherwise
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    boolean existsByPhoneNumber(String phoneNumber);
    
    /**
     * Checks if a user with the given ID exists and is verified.
     * 
     * @param userId the user ID
     * @param isVerified verification status to check
     * @return true if a user exists with the given ID and verification status
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    boolean existsByUserIdAndIsVerified(Long userId, boolean isVerified);
    
    /**
     * Finds users by role.
     * 
     * @param role the user role
     * @return list of users with the specified role
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    List<User> findByRole(UserRole role);
}