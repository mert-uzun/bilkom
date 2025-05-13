package com.bilkom.controller;

import com.bilkom.dto.LoginRequest;
import com.bilkom.dto.RegistrationRequest;
import com.bilkom.dto.AuthResponse;
import com.bilkom.exception.BadRequestException;
import com.bilkom.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Registers a new user.
     * 
     * @param request The registration request containing user details
     * @return ResponseEntity containing the AuthResponse
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegistrationRequest request) {
        try {
            // Email validation is already handled by RegistrationRequest validation
            return ResponseEntity.ok(authService.register(request));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(new AuthResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new AuthResponse(false, "Error during registration: " + e.getMessage()));
        }
    }

    /**
     * Logs in a user.
     * 
     * @param request The login request containing user credentials
     * @return ResponseEntity containing the AuthResponse
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok(authService.login(request));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(new AuthResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AuthResponse(false, "Error during login: " + e.getMessage()));
        }
    }

    /**
     * Logs out a user.
     * 
     * @param userId The user ID
     * @param authHeader The Authorization header containing the JWT token
     * @return ResponseEntity containing the AuthResponse
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(@RequestBody Map<String, Long> payload, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            Long userId = payload.get("userId");
            return ResponseEntity.ok(authService.logout(userId, authHeader));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(new AuthResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AuthResponse(false, "Error during logout: " + e.getMessage()));
        }
    }
    
    /**
     * Verifies a user's email.
     * 
     * @param queryToken Token from query parameter
     * @param payload JSON payload containing the verification token (alternative)
     * @return ResponseEntity containing the AuthResponse
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @PostMapping("/verify")
    public ResponseEntity<String> verifyEmail(
            @RequestParam(value = "token", required = false) String queryToken,
            @RequestBody(required = false) Map<String, String> payload) {
        try {
            String token = queryToken;
            
            // If not provided in query param, try to get from JSON body
            if (token == null && payload != null) {
                token = payload.get("token");
            }
            
            if (token == null) {
                return ResponseEntity.badRequest().body("Verification token is required");
            }
            
            boolean verified = authService.verifyEmail(token);
            if (verified) {
                return ResponseEntity.ok("Email verified successfully. You can now log in.");
            } else {
                return ResponseEntity.badRequest().body("Invalid or expired verification token.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during email verification: " + e.getMessage());
        }
    }
    
    /**
     * Requests a password reset.
     * 
     * @param email The email of the user
     * @return ResponseEntity containing the AuthResponse
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @PostMapping("/reset-password/request")
    public ResponseEntity<AuthResponse> requestPasswordReset(@RequestBody Map<String, String> payload) {
        try {
            String email = payload.get("email");
            boolean requestSent = authService.requestPasswordReset(email);
            if (requestSent) {
                return ResponseEntity.ok(new AuthResponse(true, "Password reset instructions sent to your email"));
            } else {
                return ResponseEntity.badRequest().body(new AuthResponse(false, "Email not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AuthResponse(false, "Error processing password reset request: " + e.getMessage()));
        }
    }
    
    /**
     * Confirms a password reset.
     * 
     * @param token The token of the user
     * @param newPassword The new password of the user
     * @return ResponseEntity containing the AuthResponse
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @PostMapping("/reset-password/confirm")
    public ResponseEntity<AuthResponse> confirmPasswordReset(@RequestBody Map<String, String> payload) {
        try {
            String token = payload.get("token");
            String newPassword = payload.get("newPassword");
            boolean reset = authService.resetPassword(token, newPassword);
            if (reset) {
                return ResponseEntity.ok(new AuthResponse(true, "Password reset successful"));
            } else {
                return ResponseEntity.badRequest().body(new AuthResponse(false, "Invalid or expired token"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AuthResponse(false, "Error resetting password: " + e.getMessage()));
        }
    }
    
    /**
     * Changes a user's password.
     * 
     * @param userId The user ID
     * @param currentPassword The current password of the user
     * @param newPassword The new password of the user
     * @return ResponseEntity containing the AuthResponse
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AuthResponse> changePassword(@RequestBody Map<String, Object> payload) {
        try {
            Long userId = Long.valueOf(payload.get("userId").toString());
            String currentPassword = payload.get("currentPassword").toString();
            String newPassword = payload.get("newPassword").toString();
            return ResponseEntity.ok(authService.changePassword(userId, currentPassword, newPassword));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(new AuthResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AuthResponse(false, "Error changing password: " + e.getMessage()));
        }
    }
}