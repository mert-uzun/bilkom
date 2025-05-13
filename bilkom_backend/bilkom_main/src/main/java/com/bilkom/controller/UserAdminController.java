package com.bilkom.controller;

import com.bilkom.dto.UserDTO;
import com.bilkom.entity.User;
import com.bilkom.enums.UserRole;
import com.bilkom.service.AuthService;
import com.bilkom.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for admin operations on users, such as updating roles, activating/deactivating users, and verifying users.
 * Doesn't include club registration verification processes, these are handled in AdminController.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
@RestController
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserAdminController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthService authService;
    
    /**
     * Gets all users in the system.
     * 
     * @return List of all users
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDTO> userDTOs = users.stream().map(UserDTO::new).collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }
    
    /**
     * Updates a user's role.
     * 
     * @param id The user ID
     * @param payload JSON payload containing the new role
     * @return The updated user
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @PutMapping("/{id}/role")
    public ResponseEntity<UserDTO> updateUserRole(@PathVariable("id") Long id, @RequestBody Map<String, String> payload) {
        UserRole role = UserRole.valueOf(payload.get("role"));
        User updatedUser = authService.updateUserRole(id, role);
        return ResponseEntity.ok(new UserDTO(updatedUser));
    }
    
    /**
     * Activates or deactivates a user.
     * 
     * @param id The user ID
     * @param payload JSON payload containing the active flag
     * @return The updated user
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @PutMapping("/{id}/active")
    public ResponseEntity<UserDTO> setUserActive(@PathVariable("id") Long id, @RequestBody Map<String, Boolean> payload) {
        Boolean active = payload.get("active");
        User user = userService.getUserById(id);
        user.setActive(active);
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(new UserDTO(updatedUser));
    }
    
    /**
     * Makes a user verified.
     * 
     * @param id The user ID
     * @param payload JSON payload containing the verified flag
     * @return The updated user
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @PutMapping("/{id}/verified")
    public ResponseEntity<UserDTO> setUserVerified(@PathVariable("id") Long id, @RequestBody Map<String, Boolean> payload) {
        Boolean verified = payload.get("verified");
        User user = userService.getUserById(id);
        user.setVerified(verified);
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(new UserDTO(updatedUser));
    }
} 