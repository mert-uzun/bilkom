package com.bilkom.controller;

import com.bilkom.entity.User;
import com.bilkom.exception.BadRequestException;
import com.bilkom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import com.bilkom.dto.ClubDTO;
import com.bilkom.service.ClubMemberService;
import com.bilkom.service.ClubService;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private ClubMemberService clubMemberService;

    @Autowired
    private ClubService clubService;

    // GET all users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            return ResponseEntity.ok(userService.getAllUsers());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving users", e);
        }
    }

    // GET user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (BadRequestException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving user", e);
        }
    }

    // POST a new user
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
        } catch (BadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating user", e);
        }
    }

    // PUT to update user (legacy method)
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            return ResponseEntity.ok(userService.updateUser(id, user));
        } catch (BadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating user", e);
        }
    }

    // DELETE a user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (BadRequestException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting user", e);
        }
    }
    
    // GET user by Email
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        try {
            User user = userService.getUserByEmail(email);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving user", e);
        }
    }
    
    // GET user by Bilkent ID
    @GetMapping("/bilkentId/{bilkentId}")
    public ResponseEntity<User> getUserByBilkentId(@PathVariable String bilkentId) {
        try {
            User user = userService.getUserByBilkentId(bilkentId);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving user", e);
        }
    }
    
    // --- FIELD-SPECIFIC UPDATE ENDPOINTS ---
    
    // Update email
    @PutMapping("/{id}/email")
    public ResponseEntity<User> updateEmail(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            String email = payload.get("email");
            return ResponseEntity.ok(userService.updateEmail(id, email));
        } catch (BadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating email", e);
        }
    }
    
    // Update first name
    @PutMapping("/{id}/firstName")
    public ResponseEntity<User> updateFirstName(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            String firstName = payload.get("firstName");
            return ResponseEntity.ok(userService.updateFirstName(id, firstName));
        } catch (BadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating first name", e);
        }
    }
    
    // Update last name
    @PutMapping("/{id}/lastName")
    public ResponseEntity<User> updateLastName(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            String lastName = payload.get("lastName");
            return ResponseEntity.ok(userService.updateLastName(id, lastName));
        } catch (BadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating last name", e);
        }
    }
    
    // Update Bilkent ID
    @PutMapping("/{id}/bilkentId")
    public ResponseEntity<User> updateBilkentId(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            String bilkentId = payload.get("bilkentId");
            return ResponseEntity.ok(userService.updateBilkentId(id, bilkentId));
        } catch (BadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating Bilkent ID", e);
        }
    }
    
    // Update phone number
    @PutMapping("/{id}/phoneNumber")
    public ResponseEntity<User> updatePhoneNumber(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            String phoneNumber = payload.get("phoneNumber");
            return ResponseEntity.ok(userService.updatePhoneNumber(id, phoneNumber));
        } catch (BadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating phone number", e);
        }
    }
    
    // Update blood type
    @PutMapping("/{id}/bloodType")
    public ResponseEntity<User> updateBloodType(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            String bloodType = payload.get("bloodType");
            return ResponseEntity.ok(userService.updateBloodType(id, bloodType));
        } catch (BadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating blood type", e);
        }
    }
    
    // Update password
    @PutMapping("/{id}/password")
    public ResponseEntity<User> updatePassword(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            String currentPassword = payload.get("currentPassword");
            String newPassword = payload.get("newPassword");
            return ResponseEntity.ok(userService.updatePassword(id, currentPassword, newPassword));
        } catch (BadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating password", e);
        }
    }
    
    // ADMIN ENDPOINTS
    
    /**
     * Updates the verification status of a user.
     * 
     * @param id The ID of the user to update
     * @param payload A map containing the verification status
     * @return The updated user
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @PutMapping("/{id}/verification")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateVerificationStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> payload) {
        try {
            Boolean status = payload.get("verified");
            if (status == null) {
                throw new BadRequestException("Verification status is required");
            }
            return ResponseEntity.ok(userService.updateVerificationStatus(id, status));
        } catch (BadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating verification status", e);
        }
    }
    
    /**
     * Updates the active status of a user.
     * 
     * @param id The ID of the user to update
     * @param payload A map containing the active status
     * @return The updated user
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @PutMapping("/{id}/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateActiveStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> payload) {
        try {
            Boolean status = payload.get("active");
            if (status == null) {
                throw new BadRequestException("Active status is required");
            }
            return ResponseEntity.ok(userService.updateActiveStatus(id, status));
        } catch (BadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating active status", e);
        }
    }

    /**
     * Gets all clubs where the currently authenticated user is a member.
     * 
     * @return List of clubs where the current user is a member
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/me/clubs")
    public ResponseEntity<List<ClubDTO>> getCurrentUserClubs() {
        try {
            User currentUser = userService.getCurrentAuthenticatedUser();
            return ResponseEntity.ok(clubMemberService.getClubsByMember(currentUser.getUserId()));
        } catch (BadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving user's clubs", e);
        }
    }
    
    /**
     * Gets all clubs where the currently authenticated user is an executive.
     * 
     * @return List of clubs where the current user is an executive
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/me/executive-clubs")
    public ResponseEntity<List<ClubDTO>> getCurrentUserExecutiveClubs() {
        try {
            User currentUser = userService.getCurrentAuthenticatedUser();
            return ResponseEntity.ok(clubService.getClubsByExecutiveId(currentUser.getUserId()));
        } catch (BadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving user's executive clubs", e);
        }
    }
    
    /**
     * Gets all clubs where the currently authenticated user is the club head.
     * 
     * @return List of clubs where the current user is the club head
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/me/head-clubs")
    public ResponseEntity<List<ClubDTO>> getCurrentUserHeadClubs() {
        try {
            User currentUser = userService.getCurrentAuthenticatedUser();
            return ResponseEntity.ok(clubService.getClubsByHeadId(currentUser.getUserId()));
        } catch (BadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving user's head clubs", e);
        }
    }

    /**
     * Gets all club associations for the currently authenticated user with their role in each club.
     * This endpoint provides a comprehensive view of all clubs the user is associated with,
     * categorized by their role (member, executive, head).
     * 
     * @return Map containing all club associations for the current user
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/me/all-clubs")
    public ResponseEntity<Map<String, Object>> getAllUserClubAssociations() {
        try {
            User currentUser = userService.getCurrentAuthenticatedUser();
            return ResponseEntity.ok(userService.getAllClubAssociations(currentUser.getUserId()));
        } catch (BadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving user's club associations", e);
        }
    }
    
    /**
     * Gets all clubs where a specific user is a member.
     * 
     * @param id The user ID
     * @return List of clubs where the user is a member
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/{id}/clubs")
    public ResponseEntity<List<ClubDTO>> getUserClubs(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(clubMemberService.getClubsByMember(id));
        } catch (BadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving user's clubs", e);
        }
    }
    
    /**
     * Gets all clubs where a specific user is an executive.
     * 
     * @param id The user ID
     * @return List of clubs where the user is an executive
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/{id}/executive-clubs")
    public ResponseEntity<List<ClubDTO>> getUserExecutiveClubs(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(clubService.getClubsByExecutiveId(id));
        } catch (BadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving user's executive clubs", e);
        }
    }
    
    /**
     * Gets all clubs where a specific user is the club head.
     * 
     * @param id The user ID
     * @return List of clubs where the user is the club head
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/{id}/head-clubs")
    public ResponseEntity<List<ClubDTO>> getUserHeadClubs(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(clubService.getClubsByHeadId(id));
        } catch (BadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving user's head clubs", e);
        }
    }
    
    /**
     * Gets all club associations for a specific user with their role in each club.
     * This endpoint provides a comprehensive view of all clubs the user is associated with,
     * categorized by their role (member, executive, head).
     * 
     * @param id The user ID
     * @return Map containing all club associations for the user
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/{id}/all-clubs")
    public ResponseEntity<Map<String, Object>> getUserAllClubAssociations(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.getAllClubAssociations(id));
        } catch (BadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving user's club associations", e);
        }
    }
    
    @PutMapping("/{id}/fcm-token")
    public ResponseEntity<User> updateFcmToken(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            String fcmToken = payload.get("fcmToken");
            User user = userService.getUserById(id);
            user.setFcmToken(fcmToken);
            return ResponseEntity.ok(userService.updateUser(id, user));
        } catch (BadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating FCM token", e);
        }
    }
}
