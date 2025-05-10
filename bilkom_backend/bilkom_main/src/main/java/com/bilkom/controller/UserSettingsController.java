package com.bilkom.controller;

import com.bilkom.dto.AuthResponse;
import com.bilkom.dto.UserDTO;
import com.bilkom.dto.AvatarUpdateRequest;
import com.bilkom.dto.AvatarListResponse;
import com.bilkom.entity.User;
import com.bilkom.exception.BadRequestException;
import com.bilkom.service.AuthService;
import com.bilkom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for user settings operations.
 * Provides endpoints for users to update their profile, change password, and logout.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
@RestController
@RequestMapping("/user-settings")
public class UserSettingsController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthService authService;
    
    /**
     * Gets the current user's profile.
     * 
     * @param userId The user ID
     * @return The user's profile
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/profile/{userId}")
    @PreAuthorize("authentication.principal.username == @userService.getUserById(#userId).email")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable("userId") Long userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(new UserDTO(user));
    }
    
    /**
     * Updates the user's profile information.
     * 
     * @param userId The user ID
     * @param userDetails The updated user details
     * @return The updated user profile
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @PutMapping("/profile/{userId}")
    @PreAuthorize("authentication.principal.username == @userService.getUserById(#userId).email")
    public ResponseEntity<UserDTO> updateProfile(@PathVariable("userId") Long userId, @RequestBody User userDetails) {
        User user = userService.getUserById(userId);
        
        // Only update allowed fields (not allowing email change here for security)
        if (userDetails.getFirstName() != null){
            user.setFirstName(userDetails.getFirstName());  
        }
        if (userDetails.getLastName() != null){
            user.setLastName(userDetails.getLastName());
        }
        if (userDetails.getPhoneNumber() != null){
            user.setPhoneNumber(userDetails.getPhoneNumber());
        }
        if (userDetails.getBloodType() != null){
            user.setBloodType(userDetails.getBloodType());
        }
        
        User updatedUser = userService.updateUser(userId, user);
        return ResponseEntity.ok(new UserDTO(updatedUser));
    }
    
    /**
     * Changes the user's password.
     * 
     * @param userId The user ID
     * @param passwordData Map containing current and new password
     * @return Response indicating success or failure
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @PostMapping("/change-password/{userId}")
    @PreAuthorize("authentication.principal.username == @userService.getUserById(#userId).email")
    public ResponseEntity<AuthResponse> changePassword(@PathVariable("userId") Long userId, @RequestBody Map<String, String> passwordData) {
        
        String currentPassword = passwordData.get("currentPassword");
        String newPassword = passwordData.get("newPassword");
        
        if (currentPassword == null || newPassword == null) {
            throw new BadRequestException("Current password and new password are required");
        }
        
        return ResponseEntity.ok(authService.changePassword(userId, currentPassword, newPassword));
    }
    
    /**
     * Logs out the current user.
     * 
     * @param userId The user ID
     * @param authHeader The Authorization header containing the JWT token
     * @return Response indicating success
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @PostMapping("/logout/{userId}")
    @PreAuthorize("authentication.principal.username == @userService.getUserById(#userId).email")
    public ResponseEntity<AuthResponse> logout(@PathVariable("userId") Long userId, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(authService.logout(userId, authHeader));
    }
    
    /**
     * Updates the user's notification preferences.
     * 
     * @param userId The user ID
     * @param preferences Map of notification preferences
     * @return The updated user profile
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @PutMapping("/notifications/{userId}")
    @PreAuthorize("authentication.principal.username == @userService.getUserById(#userId).email")
    public ResponseEntity<UserDTO> updateNotificationPreferences(@PathVariable("userId") Long userId, @RequestBody Map<String, Boolean> preferences) {
        
        User user = userService.getUserById(userId);
        
        // Update notification preferences using the added fields
        if (preferences.containsKey("emailNotifications")) {
            user.setEmailNotificationsEnabled(preferences.get("emailNotifications"));
        }
        
        if (preferences.containsKey("smsNotifications")) {
            user.setSmsNotificationsEnabled(preferences.get("smsNotifications"));
        }
        
        User updatedUser = userService.updateUser(userId, user);
        return ResponseEntity.ok(new UserDTO(updatedUser));
    }
    
    /**
     * Updates the user's privacy settings.
     * 
     * @param userId The user ID
     * @param settings Map of privacy settings
     * @return The updated user profile
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @PutMapping("/privacy/{userId}")
    @PreAuthorize("authentication.principal.username == @userService.getUserById(#userId).email")
    public ResponseEntity<UserDTO> updatePrivacySettings(@PathVariable("userId") Long userId, @RequestBody Map<String, String> settings) {
        
        User user = userService.getUserById(userId);
        
        // Update privacy settings using the added enum
        if (settings.containsKey("profileVisibility")) {
            try {
                User.ProfileVisibility visibility = User.ProfileVisibility.valueOf(settings.get("profileVisibility").toUpperCase());
                user.setProfileVisibility(visibility);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid profile visibility value. Valid values are: PUBLIC, MEMBERS, PRIVATE");
            }
        }
        
        User updatedUser = userService.updateUser(userId, user);
        return ResponseEntity.ok(new UserDTO(updatedUser));
    }
    
    /**
     * Gets all available avatars.
     * 
     * @return List of all available avatars with paths
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/avatars")
    public ResponseEntity<AvatarListResponse> getAvatars() {
        return ResponseEntity.ok(new AvatarListResponse());
    }
    
    /**
     * Updates the user's avatar.
     * 
     * @param userId The user ID
     * @param request The avatar update request containing the avatar path
     * @return The updated user profile
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @PutMapping("/avatar/{userId}")
    @PreAuthorize("authentication.principal.username == @userService.getUserById(#userId).email")
    public ResponseEntity<UserDTO> updateAvatar(@PathVariable("userId") Long userId, @RequestBody AvatarUpdateRequest request) {
        
        if (request.getAvatarPath() == null || request.getAvatarPath().isEmpty()) {
            throw new BadRequestException("Avatar path is required");
        }
        
        User updatedUser = userService.updateAvatar(userId, request.getAvatarPath());
        return ResponseEntity.ok(new UserDTO(updatedUser));
    }
} 