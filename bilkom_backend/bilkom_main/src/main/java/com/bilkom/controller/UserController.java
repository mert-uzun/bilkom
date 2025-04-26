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

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

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
    
    // Admin endpoints
    
    // Update verification status
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
    
    // Update active status
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
}
