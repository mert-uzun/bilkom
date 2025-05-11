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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * Controller for user settings operations.
 * Provides endpoints for users to update their profile, password, preferences, and privacy settings.
 * 
 * @author Elif Bozkurt
 * @version 2.0
 */
@RestController
@RequestMapping("/user-settings")
public class UserSettingsController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    /**
     * Verifies that the currently authenticated user is allowed to access the given userId.
     * 
     * @param userId the ID of the user being accessed
     * @throws ResponseStatusException if the authenticated user is not authorized
     * 
     * @author Elif Bozkurt
     * @version 2.0
     */
    private void verifyUserAccess(Long userId) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        String targetEmail = userService.getUserById(userId).getEmail();
        if (!currentEmail.equals(targetEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }

    /**
     * Retrieves the profile of a specific user.
     *
     * @param userId the user ID
     * @return the user's profile
     * 
     * @author Elif Bozkurt
     * @version 2.0
     */
    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable("userId") Long userId) {
        verifyUserAccess(userId);
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(new UserDTO(user));
    }

    /**
     * Updates a user's profile information.
     *
     * @param userId the user ID
     * @param userDetails the updated user information
     * @return the updated user profile
     * 
     * @author Elif Bozkurt
     * @version 2.0
     */
    @PutMapping("/profile/{userId}")
    public ResponseEntity<UserDTO> updateProfile(@PathVariable("userId") Long userId, @RequestBody User userDetails) {
        verifyUserAccess(userId);
        User user = userService.getUserById(userId);

        if (userDetails.getFirstName() != null) user.setFirstName(userDetails.getFirstName());
        if (userDetails.getLastName() != null) user.setLastName(userDetails.getLastName());
        if (userDetails.getPhoneNumber() != null) user.setPhoneNumber(userDetails.getPhoneNumber());
        if (userDetails.getBloodType() != null) user.setBloodType(userDetails.getBloodType());

        User updatedUser = userService.updateUser(userId, user);
        return ResponseEntity.ok(new UserDTO(updatedUser));
    }

    /**
     * Changes the user's password.
     *
     * @param userId the user ID
     * @param passwordData a map containing "currentPassword" and "newPassword"
     * @return an authentication response
     * 
     * @author Elif Bozkurt
     * @version 2.0
     */
    @PostMapping("/change-password/{userId}")
    public ResponseEntity<AuthResponse> changePassword(@PathVariable("userId") Long userId, @RequestBody Map<String, String> passwordData) {
        verifyUserAccess(userId);

        String currentPassword = passwordData.get("currentPassword");
        String newPassword = passwordData.get("newPassword");

        if (currentPassword == null || newPassword == null) {
            throw new BadRequestException("Current password and new password are required");
        }

        return ResponseEntity.ok(authService.changePassword(userId, currentPassword, newPassword));
    }

    /**
     * Logs out the current user by invalidating the JWT token.
     *
     * @param userId the user ID
     * @param authHeader the Authorization header containing the JWT
     * @return an authentication response
     * 
     * @author Elif Bozkurt
     * @version 2.0
     */
    @PostMapping("/logout/{userId}")
    public ResponseEntity<AuthResponse> logout(@PathVariable("userId") Long userId, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        verifyUserAccess(userId);
        return ResponseEntity.ok(authService.logout(userId, authHeader));
    }

    /**
     * Updates the user's email and SMS notification preferences.
     *
     * @param userId the user ID
     * @param preferences a map containing "emailNotifications" and/or "smsNotifications"
     * @return the updated user profile
     * 
     * @author Elif Bozkurt
     * @version 2.0
     */
    @PutMapping("/notifications/{userId}")
    public ResponseEntity<UserDTO> updateNotificationPreferences(@PathVariable("userId") Long userId, @RequestBody Map<String, Boolean> preferences) {
        verifyUserAccess(userId);
        User user = userService.getUserById(userId);

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
     * @param userId the user ID
     * @param settings a map containing the "profileVisibility" setting
     * @return the updated user profile
     * 
     * @author Elif Bozkurt
     * @version 2.0
     */
    @PutMapping("/privacy/{userId}")
    public ResponseEntity<UserDTO> updatePrivacySettings(@PathVariable("userId") Long userId, @RequestBody Map<String, String> settings) {
        verifyUserAccess(userId);
        User user = userService.getUserById(userId);

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
     * Retrieves the list of all available avatar image paths.
     *
     * @return a list of avatar paths
     * 
     * @author Elif Bozkurt
     * @version 2.0
     */
    @GetMapping("/avatars")
    public ResponseEntity<AvatarListResponse> getAvatars() {
        return ResponseEntity.ok(new AvatarListResponse());
    }

    /**
     * Updates the user's avatar image path.
     *
     * @param userId the user ID
     * @param request the avatar update request
     * @return the updated user profile
     * 
     * @author Elif Bozkurt
     * @version 2.0
     */
    @PutMapping("/avatar/{userId}")
    public ResponseEntity<UserDTO> updateAvatar(@PathVariable("userId") Long userId, @RequestBody AvatarUpdateRequest request) {
        verifyUserAccess(userId);

        if (request.getAvatarPath() == null || request.getAvatarPath().isEmpty()) {
            throw new BadRequestException("Avatar path is required");
        }

        User updatedUser = userService.updateAvatar(userId, request.getAvatarPath());
        return ResponseEntity.ok(new UserDTO(updatedUser));
    }
}