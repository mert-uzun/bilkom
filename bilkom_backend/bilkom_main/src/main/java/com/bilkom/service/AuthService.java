package com.bilkom.service;

import com.bilkom.dto.LoginRequest;
import com.bilkom.dto.RegistrationRequest;
import com.bilkom.dto.AuthResponse;
import com.bilkom.entity.User;
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

    public AuthResponse register(RegistrationRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        // Check if Bilkent ID exists
        if (userRepository.existsByBilkentId(request.getBilkentId())) {
            throw new BadRequestException("Bilkent ID already registered");
        }

        // Check if phone number exists
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new BadRequestException("Phone number already registered");
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
        
        // Generate verification token
        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        
        userRepository.save(user);
        
        // Send verification email
        String verificationUrl = "http://localhost:8080/api/auth/verify?token=" + verificationToken;
        emailService.sendVerificationEmail(user.getEmail(), verificationUrl);
        
        return new AuthResponse(true, "Registration successful. Please check your email to verify your account.");
    }

    public AuthResponse login(LoginRequest request) {
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
    }
    
    public boolean verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
            .orElseThrow(() -> new BadRequestException("Invalid verification token"));
        
        user.setVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);
        
        return true;
    }
    
    public boolean requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new BadRequestException("Email not found"));
        
        String resetToken = UUID.randomUUID().toString();
        user.setVerificationToken(resetToken);
        userRepository.save(user);
        
        // Send password reset email
        String resetUrl = "http://localhost:8080/api/auth/reset-password?token=" + resetToken;
        sendPasswordResetEmail(user.getEmail(), resetUrl);
        
        return true;
    }
    
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
    
    private void sendPasswordResetEmail(String email, String resetUrl) {
        // Using EmailService to send password reset email
        emailService.sendPasswordResetEmail(email, resetUrl);
    }
}