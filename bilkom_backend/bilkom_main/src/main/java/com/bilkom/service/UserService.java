package com.bilkom.service;

import com.bilkom.entity.User;
import com.bilkom.dto.ClubDTO;
import com.bilkom.exception.BadRequestException;
import com.bilkom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Map;
import java.util.HashMap;
import com.bilkom.enums.AvatarRelativePaths;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailService emailService;
    
    // Valid blood types
    private static final Set<String> VALID_BLOOD_TYPES = new HashSet<>(
        Arrays.asList("A+", "A-", "B+", "B-", "AB+", "AB-", "0+", "0-")
    );

    @Autowired
    private ClubService clubService;

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> 
            new BadRequestException("User not found with id: " + id));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> 
        new BadRequestException("User not found with email: " + email));
    }

    public User getUserByBilkentId(String bilkentId) {
        return userRepository.findByBilkentId(bilkentId).orElseThrow(() -> 
        new BadRequestException("User not found with bilkentId: " + bilkentId));
    }

    public User getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> 
        new BadRequestException("User not found with phoneNumber: " + phoneNumber));
    }

    public List<User> getUsersByBloodType(String bloodType) {
        return userRepository.findByBloodType(bloodType);
    }

    public User getUserByFullName(String firstName, String lastName) {
        return userRepository.findByFirstNameAndLastName(firstName, lastName).orElseThrow(() -> 
        new BadRequestException("User not found with full name: " + firstName + " " + lastName));
    }

    /**
     * Gets all users.
     * 
     * @return List of all users
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Deletes a user by their ID.
     * 
     * @param id The ID of the user to delete
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public void deleteUser(Long id) {
        if (!isUserExists(id)) {
            throw new BadRequestException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Checks if a user exists by their ID.
     * 
     * @param id The ID of the user to check
     * @return true if the user exists, false otherwise
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public boolean isUserExists(Long id) {
        return userRepository.existsById(id);
    }

    /**
     * Checks if a user exists by their email.
     * 
     * @param email The email of the user to check
     * @return true if the user exists, false otherwise
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Checks if a user exists by their Bilkent ID.
     * 
     * @param bilkentId The Bilkent ID of the user to check
     * @return true if the user exists, false otherwise
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public boolean isBilkentIdExists(String bilkentId) {
        return userRepository.existsByBilkentId(bilkentId);
    }

    /**
     * Checks if a user exists by their phone number.
     * 
     * @param phoneNumber The phone number of the user to check
     * @return true if the user exists, false otherwise
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public boolean isPhoneNumberExists(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    /**
     * Checks if a user exists by their full name.
     * 
     * @param firstName The first name of the user to check
     * @param lastName The last name of the user to check
     * @return true if the user exists, false otherwise
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public boolean isFullNameExists(String firstName, String lastName) {
        return userRepository.existsByFirstNameAndLastName(firstName, lastName);
    }

    /**
     * Checks if a user is verified.
     * 
     * @param id The ID of the user to check
     * @return true if the user is verified, false otherwise
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public boolean isUserVerified(Long id) {
        User user = getUserById(id);
        return user != null && user.isVerified();
    } 

    /**
     * Creates a new user with all required fields and security measures
     * 
     * @param email User's email address
     * @param rawPassword Unencrypted password
     * @param firstName User's first name
     * @param lastName User's last name
     * @param bilkentId User's Bilkent ID
     * @param phoneNumber User's phone number
     * @param bloodType User's blood type
     * @param sendVerificationEmail Whether to send verification email
     * @return The created user
     */
    public User createUser(String email, String rawPassword, String firstName, 
                          String lastName, String bilkentId, String phoneNumber, 
                          String bloodType, boolean sendVerificationEmail) {
        
        // Validate unique fields
        if (isEmailExists(email)) {
            throw new BadRequestException("Email already in use");
        }
        if (isBilkentIdExists(bilkentId)) {
            throw new BadRequestException("Bilkent ID already in use");
        }
        if (isPhoneNumberExists(phoneNumber)) {
            throw new BadRequestException("Phone number already in use");
        }
        
        // Create new user instance
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setBilkentId(bilkentId);
        user.setPhoneNumber(phoneNumber);
        user.setBloodType(bloodType);
        
        // Set default values
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        user.setVerified(false);
        user.setActive(true);
        user.updateLastLogin();
        
        // Generate verification token
        if (sendVerificationEmail) {
            String verificationToken = UUID.randomUUID().toString();
            user.setVerificationToken(verificationToken);
            
            // Save user first to get the ID
            User savedUser = userRepository.save(user);
            
            // Send verification email
            String verificationUrl = "http://192.168.231.145:8080/api/auth/verify?token=" + verificationToken;
            emailService.sendVerificationEmail(user.getEmail(), verificationUrl);
            
            return savedUser;
        }
        
        return userRepository.save(user);
    }
    
    /**
     * Simple version of createUser that takes a User object
     * Useful when full object is already constructed
     */
    public User createUser(User user) {
        return createUser(
            user.getEmail(),
            user.getPasswordHash(), // Takes raw password
            user.getFirstName(),
            user.getLastName(),
            user.getBilkentId(),
            user.getPhoneNumber(),
            user.getBloodType(),
            true // Send verification email by default
        );
    }

    /**
     * Updates a user with comprehensive field handling and security
     * 
     * @param id User ID to update
     * @param userDetails User object with fields to update
     * @param updatePassword Whether to update password
     * @param rawPassword Raw password if updating
     * @return Updated user
     */
    @Transactional
    public User updateUser(Long id, User userDetails, boolean updatePassword, String rawPassword) {
        User existingUser = getUserById(id);
        boolean modified = false;
        
        // Update email if changed
        if (userDetails.getEmail() != null && !userDetails.getEmail().equals(existingUser.getEmail())) {
            if (isEmailExists(userDetails.getEmail())) {
                throw new BadRequestException("Email already in use");
            }
            existingUser.setEmail(userDetails.getEmail());
            modified = true;
        }
        
        // Update names if provided
        if (userDetails.getFirstName() != null) {
            existingUser.setFirstName(userDetails.getFirstName());
            modified = true;
        }
        
        if (userDetails.getLastName() != null) {
            existingUser.setLastName(userDetails.getLastName());
            modified = true;
        }
        
        // Update Bilkent ID if changed
        if (userDetails.getBilkentId() != null && !userDetails.getBilkentId().equals(existingUser.getBilkentId())) {
            if (isBilkentIdExists(userDetails.getBilkentId())) {
                throw new BadRequestException("Bilkent ID already in use");
            }
            existingUser.setBilkentId(userDetails.getBilkentId());
            modified = true;
        }
        
        // Update phone number if changed
        if (userDetails.getPhoneNumber() != null && !userDetails.getPhoneNumber().equals(existingUser.getPhoneNumber())) {
            if (isPhoneNumberExists(userDetails.getPhoneNumber())) {
                throw new BadRequestException("Phone number already in use");
            }
            existingUser.setPhoneNumber(userDetails.getPhoneNumber());
            modified = true;
        }
        
        // Update blood type if provided
        if (userDetails.getBloodType() != null) {
            existingUser.setBloodType(userDetails.getBloodType());
            modified = true;
        }
        
        // Update verification status if changed
        if (userDetails.isVerified() != existingUser.isVerified()) {
            existingUser.setVerified(userDetails.isVerified());
            modified = true;
        }
        
        // Update active status if changed
        if (userDetails.isActive() != existingUser.isActive()) {
            existingUser.setActive(userDetails.isActive());
            modified = true;
        }
        
        // Update password if requested
        if (updatePassword && rawPassword != null && !rawPassword.isEmpty()) {
            existingUser.setPasswordHash(passwordEncoder.encode(rawPassword));
            modified = true;
        }
        
        // Save only if something changed
        if (modified) {
            return userRepository.save(existingUser);
        }
        
        return existingUser;
    }
    
    /**
     * Simplified update method for backward compatibility
     * 
     * @param id The ID of the user to update
     * @param userDetails The user object with fields to update
     * @return The updated user
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public User updateUser(Long id, User userDetails) {
        return updateUser(id, userDetails, false, null);
    }

    /**
     * Updates a user's email with validation and re-verification
     * @param userId User ID
     * @param email New email
     * @return Updated user
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public User updateEmail(Long userId, String email) {
        User user = getUserById(userId);
        
        if (email == null || email.isEmpty()) {
            throw new BadRequestException("Email cannot be empty");
        }
        
        if (email.equals(user.getEmail())) {
            return user; // No change needed
        }
        
        // Validate Bilkent email format
        if (!email.matches(".*@bilkent\\.edu\\.tr$|.*@ug\\.bilkent\\.edu\\.tr$")) {
            throw new BadRequestException("Only Bilkent University emails are allowed");
        }
        
        if (isEmailExists(email)) {
            throw new BadRequestException("Email already in use");
        }
        
        // Set email and reset verification status
        user.setEmail(email);
        user.setVerified(false);
        
        // Generate new verification token
        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        
        // Save the user first to ensure it's in the database
        User savedUser = userRepository.save(user);
        
        // Send verification email
        String verificationUrl = "http://192.168.231.145:8080/api/auth/verify?token=" + verificationToken;
        emailService.sendVerificationEmail(email, verificationUrl);
        
        return savedUser;
    }
    
    /**
     * Updates a user's first name
     * @param userId User ID
     * @param firstName New first name
     * @return Updated user
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public User updateFirstName(Long userId, String firstName) {
        User user = getUserById(userId);
        
        if (firstName == null || firstName.isEmpty()) {
            throw new BadRequestException("First name cannot be empty");
        }
        
        if (firstName.equals(user.getFirstName())) {
            return user; // No change needed
        }
        
        user.setFirstName(firstName);
        return userRepository.save(user);
    }
    
    /**
     * Updates a user's last name
     * @param userId User ID
     * @param lastName New last name
     * @return Updated user
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public User updateLastName(Long userId, String lastName) {
        User user = getUserById(userId);
        
        if (lastName == null || lastName.isEmpty()) {
            throw new BadRequestException("Last name cannot be empty");
        }
        
        if (lastName.equals(user.getLastName())) {
            return user; // No change needed
        }
        
        user.setLastName(lastName);
        return userRepository.save(user);
    }
    
    /**
     * Updates a user's Bilkent ID with validation
     * @param userId User ID
     * @param bilkentId New Bilkent ID
     * @return Updated user
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public User updateBilkentId(Long userId, String bilkentId) {
        User user = getUserById(userId);
        
        if (bilkentId == null || bilkentId.isEmpty()) {
            throw new BadRequestException("Bilkent ID cannot be empty");
        }
        
        if (bilkentId.equals(user.getBilkentId())) {
            return user; // No change needed
        }
        
        // Validate numbers-only format
        if (!bilkentId.matches("^\\d+$")) {
            throw new BadRequestException("Bilkent ID must contain only numbers");
        }
        
        if (isBilkentIdExists(bilkentId)) {
            throw new BadRequestException("Bilkent ID already in use");
        }
        
        user.setBilkentId(bilkentId);
        return userRepository.save(user);
    }
    
    /**
     * Updates a user's phone number with validation
     * @param userId User ID
     * @param phoneNumber New phone number
     * @return Updated user
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public User updatePhoneNumber(Long userId, String phoneNumber) {
        User user = getUserById(userId);
        
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new BadRequestException("Phone number cannot be empty");
        }
        
        if (phoneNumber.equals(user.getPhoneNumber())) {
            return user; // No change needed
        }
        
        // Validate phone number format (basic validation)
        if (!phoneNumber.matches("^[+]?\\d{10,15}$")) {
            throw new BadRequestException("Invalid phone number format. It should contain 10-15 digits with optional + prefix");
        }
        
        if (isPhoneNumberExists(phoneNumber)) {
            throw new BadRequestException("Phone number already in use");
        }
        
        user.setPhoneNumber(phoneNumber);
        return userRepository.save(user);
    }
    
    /**
     * Updates a user's blood type with validation
     * @param userId User ID
     * @param bloodType New blood type
     * @return Updated user
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public User updateBloodType(Long userId, String bloodType) {
        User user = getUserById(userId);
        
        if (bloodType == null || bloodType.isEmpty()) {
            throw new BadRequestException("Blood type cannot be empty");
        }
        
        if (bloodType.equals(user.getBloodType())) {
            return user; // No change needed
        }
        
        // Validate blood type
        if (!VALID_BLOOD_TYPES.contains(bloodType)) {
            throw new BadRequestException("Invalid blood type. Valid types are: A+, A-, B+, B-, AB+, AB-, 0+, 0-");
        }
        
        user.setBloodType(bloodType);
        return userRepository.save(user);
    }
    
    /**
     * Updates a user's password with proper security
     * @param userId User ID
     * @param currentPassword Current password for verification
     * @param newPassword New password
     * @return Updated user
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public User updatePassword(Long userId, String currentPassword, String newPassword) {
        User user = getUserById(userId);
        
        if (newPassword == null || newPassword.isEmpty()) {
            throw new BadRequestException("New password cannot be empty");
        }
        
        if (newPassword.length() < 8) {
            throw new BadRequestException("Password must be at least 8 characters");
        }
        
        // Verify current password
        if (currentPassword != null && !passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new BadRequestException("Current password is incorrect");
        }
        
        // Update password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }
    
    /**
     * Updates a user's verification status
     * This is typically an admin function, not a user-facing one
     * 
     * @param userId The ID of the user to update
     * @param verified The new verification status
     * @return The updated user
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public User updateVerificationStatus(Long userId, boolean verified) {
        User user = getUserById(userId);
        
        if (user.isVerified() == verified) {
            return user; // No change needed
        }
        
        user.setVerified(verified);
        return userRepository.save(user);
    }
    
    /**
     * Updates a user's active status
     * This is typically an admin function, not a user-facing one
     * 
     * @param userId The ID of the user to update
     * @param active The new active status
     * @return The updated user
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public User updateActiveStatus(Long userId, boolean active) {
        User user = getUserById(userId);
        
        if (user.isActive() == active) {
            return user; // No change needed
        }
        
        user.setActive(active);
        return userRepository.save(user);
    }

    /**
     * Gets the currently authenticated user.
     * 
     * @return The authenticated user
     * @throws BadRequestException if no user is authenticated or user not found
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
                authentication.getPrincipal().equals("anonymousUser")) {
            throw new BadRequestException("No authenticated user found");
        }
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new BadRequestException("User not found"));
    }

    /**
     * Gets all club associations for a user with their role in each club.
     * 
     * @param userId The user ID
     * @return Map of clubs with role information
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public Map<String, Object> getAllClubAssociations(Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        // Get clubs where user is a member and add to the result
        List<ClubDTO> memberClubs = clubService.getClubsByMemberId(userId);
        result.put("memberClubs", memberClubs);
        
        // Get clubs where user is an executive and add to the result
        List<ClubDTO> executiveClubs = clubService.getClubsByExecutiveId(userId);
        result.put("executiveClubs", executiveClubs);
        
        // Get clubs where user is the head and add to the result
        List<ClubDTO> headClubs = clubService.getClubsByHeadId(userId);
        result.put("headClubs", headClubs);
        
        return result;
    }

    /**
     * Updates a user's avatar.
     * 
     * @param userId The ID of the user to update
     * @param avatarPathName The name of the avatar path enum value
     * @return The updated user
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Transactional
    public User updateAvatar(Long userId, String avatarPathName) {
        User user = getUserById(userId);
        
        try {
            AvatarRelativePaths avatarPath = AvatarRelativePaths.valueOf(avatarPathName);
            user.setAvatarPath(avatarPath);
            return userRepository.save(user);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid avatar path: " + avatarPathName + ". Valid values are: " + Arrays.toString(AvatarRelativePaths.values()));
        }
    }
}
