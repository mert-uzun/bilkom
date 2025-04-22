package com.bilkom.controller;

import com.bilkom.dto.LoginRequest;
import com.bilkom.dto.RegistrationRequest;
import com.bilkom.dto.AuthResponse;
import com.bilkom.exception.BadRequestException;
import com.bilkom.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

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

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok(authService.login(request));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(new AuthResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new AuthResponse(false, "Error during login: " + e.getMessage()));
        }
    }
    
    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        try {
            boolean verified = authService.verifyEmail(token);
            if (verified) {
                return ResponseEntity.ok("Email verified successfully. You can now log in.");
            } else {
                return ResponseEntity.badRequest().body("Invalid or expired verification token.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error during email verification: " + e.getMessage());
        }
    }
    
    @PostMapping("/reset-password/request")
    public ResponseEntity<AuthResponse> requestPasswordReset(@RequestParam("email") String email) {
        try {
            boolean requestSent = authService.requestPasswordReset(email);
            if (requestSent) {
                return ResponseEntity.ok(new AuthResponse(true, "Password reset instructions sent to your email"));
            } else {
                return ResponseEntity.badRequest().body(new AuthResponse(false, "Email not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new AuthResponse(false, "Error processing password reset request: " + e.getMessage()));
        }
    }
    
    @PostMapping("/reset-password/confirm")
    public ResponseEntity<AuthResponse> confirmPasswordReset(
            @RequestParam("token") String token,
            @RequestParam("newPassword") String newPassword) {
        try {
            boolean reset = authService.resetPassword(token, newPassword);
            if (reset) {
                return ResponseEntity.ok(new AuthResponse(true, "Password reset successful"));
            } else {
                return ResponseEntity.badRequest().body(new AuthResponse(false, "Invalid or expired token"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new AuthResponse(false, "Error resetting password: " + e.getMessage()));
        }
    }
}