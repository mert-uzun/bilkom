package com.bilkom.service;

import com.bilkom.dto.LoginRequest;
import com.bilkom.dto.RegistrationRequest;
import com.bilkom.dto.AuthResponse;
import com.bilkom.entity.User;
import com.bilkom.enums.UserRole;
import com.bilkom.exception.BadRequestException;
import com.bilkom.repository.UserRepository;
import com.bilkom.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    // Valid blood types
    private static final Set<String> VALID_BLOOD_TYPES = new HashSet<>(
        Arrays.asList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
    );
    
    // Admin emails - typically would be in configuration
    private static final Set<String> ADMIN_EMAILS = new HashSet<>(
        Arrays.asList("mert.uzun@ug.bilkent.edu.tr", "silab@ug.bilkent.edu.tr, elif.bozkurt@ug.bilkent.edu.tr")
    );

    /**
     * Registers a new user with the provided registration request.
     * 
     * @param request The registration request containing user details
     * @return AuthResponse containing registration success status and message
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public AuthResponse register(RegistrationRequest request) {
        // Check if email already exists
        Optional<User> existingUserOpt = userRepository.findByEmail(request.getEmail());

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();

            if (!existingUser.isActive()) {
                throw new BadRequestException("This email belongs to a deactivated account. Contact support to reactivate.");
            } else {
                throw new BadRequestException("Email already registered");
            }
        }

        // Check if Bilkent ID exists
        if (userRepository.existsByBilkentId(request.getBilkentId())) {
            throw new BadRequestException("Bilkent ID already registered");
        }

        // Check if phone number exists
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new BadRequestException("Phone number already registered");
        }
        
        // Validate phone number format
        if (!request.getPhoneNumber().matches("^[+]?\\d{10,15}$")) {
            throw new BadRequestException("Invalid phone number format. It should contain 10-15 digits with optional + prefix");
        }
        
        // Validate blood type
        if (!VALID_BLOOD_TYPES.contains(request.getBloodType())) {
            throw new BadRequestException("Invalid blood type. Valid types are: A+, A-, B+, B-, AB+, AB-, O+, O-");
        }
        
        // Create new user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setBilkentId(request.getBilkentId());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setBloodType(request.getBloodType());
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        user.setVerified(false);
        user.setActive(true);
        user.updateLastLogin();
        
        // Set user role - ADMIN for predefined admin emails, USER for everyone else
        if (ADMIN_EMAILS.contains(request.getEmail())) {
            user.setRole(UserRole.ADMIN);
        } else {
            user.setRole(UserRole.USER);
        }
        
        // Generate verification token
        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        
        userRepository.save(user);
        
        // Send verification email
        String verificationUrl = "http://localhost:8080/api/auth/verify?token=" + verificationToken;
        emailService.sendVerificationEmail(user.getEmail(), verificationUrl);
        
        return new AuthResponse(true, "Registration successful. Please check your email to verify your account.");
    }

    /**
     * Logs in a user with the provided email and password.
     * 
     * @param request The login request containing email and password
     * @return AuthResponse containing login success status, message, JWT token, and user ID
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    /*public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new BadRequestException("User not found"));
            
            if (!user.isVerified()) {
                throw new BadRequestException("Please verify your email before logging in");
            }
            
            // Update last login time
            user.updateLastLogin();
            userRepository.save(user);
            
            String jwt = jwtUtils.generateToken(userDetails);
            
            return new AuthResponse(true, "Login successful", jwt, user.getUserId());
            
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Invalid email or password");
        }
    }*/
    //DEBUG
    public AuthResponse login(LoginRequest request) {
        System.out.println("Login requested for: " + request.getEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            System.out.println("Authentication succeeded");

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new BadRequestException("User not found"));

            if (!user.isVerified()) {
                System.out.println("User not verified");
                throw new BadRequestException("Please verify your email before logging in");
            }

            if (!user.isActive()) {
                System.out.println("User is deactivated");
                throw new BadRequestException("Your account has been deactivated. Contact an administrator.");
            }

            user.updateLastLogin();
            userRepository.save(user);

            String jwt = jwtUtils.generateToken(userDetails);

            return new AuthResponse(true, "Login successful", jwt, user.getUserId());

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            System.out.println("Authentication failed: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            throw new BadRequestException("Invalid email or password");
        }
    }
    
    /**
     * Logs out a user by blacklisting their token.
     * With JWT, the actual logout happens on the client side by removing the token.
     * This method blacklists the token on the server side for additional security.
     * 
     * @param userId The user ID
     * @param token The JWT token to blacklist
     * @return AuthResponse indicating logout success
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public AuthResponse logout(Long userId, String token) {
        // Validate user exists
        userRepository.findById(userId).orElseThrow(() -> new BadRequestException("User not found"));
        
        // Blacklist the token if provided
        if (token != null && !token.isEmpty()) {
            // Remove "Bearer " prefix if present
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            // Blacklist the token
            jwtUtils.blacklistToken(token);
        }
        
        return new AuthResponse(true, "Logged out successfully");
    }

    /**
     * Changes a user's password after verifying the current password.
     * 
     * @param userId The user ID
     * @param currentPassword The current password for verification
     * @param newPassword The new password
     * @return AuthResponse indicating password change success
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public AuthResponse changePassword(Long userId, String currentPassword, String newPassword) {
        // Password validation
        if (newPassword == null || newPassword.length() < 8) {
            throw new BadRequestException("New password must be at least 8 characters");
        }
        
        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException("User not found"));
        
        // Verify current password
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), currentPassword));
        } catch (Exception e) {
            throw new BadRequestException("Current password is incorrect");
        }
        
        // Update password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        return new AuthResponse(true, "Password changed successfully");
    }

    /**
     * Verifies a user's email with the provided verification token.
     * 
     * @param token The verification token
     * @return true if the email is verified, false otherwise
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public boolean verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
            .orElseThrow(() -> new BadRequestException("Invalid verification token"));
        
        user.setVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);
        
        return true;
    }
    
    /**
     * Requests a password reset for a user with the provided email.
     * 
     * @param email The email of the user to reset password for
     * @return true if the password reset request is successful, false otherwise
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public boolean requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new BadRequestException("Email not found"));
        
        String resetToken = UUID.randomUUID().toString();
        user.setVerificationToken(resetToken);
        userRepository.save(user);
        
        // Send password reset email
        String resetUrl = "http://localhost:8080/api/auth/reset-password/confirm?token=" + resetToken;
        sendPasswordResetEmail(user.getEmail(), resetUrl);
        
        return true;
    }

    /**
     * Resets a user's password with the provided token and new password.
     * 
     * @param token The reset token
     * @param newPassword The new password
     * @return true if the password reset is successful, false otherwise
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public boolean resetPassword(String token, String newPassword) {
        if (newPassword.length() < 8) {
            throw new BadRequestException("Password must be at least 8 characters");
        }
        
        User user = userRepository.findByVerificationToken(token)
            .orElseThrow(() -> new BadRequestException("Invalid or expired token"));
        
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setVerificationToken(null);
        userRepository.save(user);
        
        return true;
    }
    
    /**
     * Updates a user's role.
     * Only admin users should be able to call this method.
     * 
     * @param userId The ID of the user to update
     * @param role The new role to assign
     * @return The updated user
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public User updateUserRole(Long userId, UserRole role) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException("User not found"));
        
        user.setRole(role);
        return userRepository.save(user);
    }
    
    /**
     * Promotes a user to CLUB_HEAD role.
     * This is typically called when a user becomes a club head.
     * 
     * @param userId The ID of the user to promote
     * @return The updated user
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public User promoteToClubHead(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException("User not found"));
        
        // Only promote regular users
        if (user.getRole() == UserRole.USER) {
            user.setRole(UserRole.CLUB_HEAD);
            return userRepository.save(user);
        }
        
        return user;
    }
    
    /**
     * Promotes a user to CLUB_EXECUTIVE role.
     * This is typically called when a user becomes a club executive.
     * 
     * @param userId The ID of the user to promote
     * @return The updated user
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public User promoteToClubExecutive(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException("User not found"));
        
        // Only promote regular users
        if (user.getRole() == UserRole.USER) {
            user.setRole(UserRole.CLUB_EXECUTIVE);
            return userRepository.save(user);
        }
        
        return user;
    }
    
    private void sendPasswordResetEmail(String email, String resetUrl) {
        // Using EmailService to send password reset email
        emailService.sendPasswordResetEmail(email, resetUrl);
    }
}