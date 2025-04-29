package com.bilkom.security;

import com.bilkom.entity.User;
import com.bilkom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Custom implementation of UserDetailsService to load users from our database.
 * Maps our User entity to Spring Security's UserDetails.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    
    /**
     * Loads user from the database by email.
     * 
     * @param email The email of the user to load
     * @return UserDetails object with user information and authorities
     * @throws UsernameNotFoundException if user is not found
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPasswordHash(), user.isActive() && user.isVerified(), true, true,
                true, Collections.singleton(new SimpleGrantedAuthority(user.getRole().getSpringSecurityRole()))
        );
    }
} 