package com.bilkom.service;

import com.bilkom.entity.User;
import com.bilkom.exception.BadRequestException;
import com.bilkom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> 
            new BadRequestException("User not found with id: " + id));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User getUserByBilkentId(String bilkentId) {
        return userRepository.findByBilkentId(bilkentId).orElse(null);
    }

    public User getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).orElse(null);
    }

    public List<User> getUsersByBloodType(String bloodType) {
        return userRepository.findByBloodType(bloodType);
    }

    public User getUserByFullName(String firstName, String lastName) {
        return userRepository.findByFirstNameAndLastName(firstName, lastName).orElse(null);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long id) {
        if (!isUserExists(id)) {
            throw new BadRequestException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    public boolean isUserExists(Long id) {
        return userRepository.existsById(id);
    }

    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean isBilkentIdExists(String bilkentId) {
        return userRepository.existsByBilkentId(bilkentId);
    }

    public boolean isPhoneNumberExists(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    public boolean isFullNameExists(String firstName, String lastName) {
        return userRepository.existsByFirstNameAndLastName(firstName, lastName);
    }

    public boolean isUserVerified(Long id) {
        User user = getUserById(id);
        return user != null && user.isVerified();
    } 

    public User createUser(User user) {
        if (isEmailExists(user.getEmail())) {
            throw new BadRequestException("Email already in use");
        }
        if (isBilkentIdExists(user.getBilkentId())) {
            throw new BadRequestException("Bilkent ID already in use");
        }
        if (isPhoneNumberExists(user.getPhoneNumber())) {
            throw new BadRequestException("Phone number already in use");
        }
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Long id, User userDetails) {
        User existingUser = getUserById(id);
        
        // Update user fields if they are not null
        if (userDetails.getEmail() != null && !userDetails.getEmail().equals(existingUser.getEmail())) {
            if (isEmailExists(userDetails.getEmail())) {
                throw new BadRequestException("Email already in use");
            }
            existingUser.setEmail(userDetails.getEmail());
        }
        
        if (userDetails.getFirstName() != null) {
            existingUser.setFirstName(userDetails.getFirstName());
        }
        
        if (userDetails.getLastName() != null) {
            existingUser.setLastName(userDetails.getLastName());
        }
        
        if (userDetails.getBilkentId() != null && !userDetails.getBilkentId().equals(existingUser.getBilkentId())) {
            if (isBilkentIdExists(userDetails.getBilkentId())) {
                throw new BadRequestException("Bilkent ID already in use");
            }
            existingUser.setBilkentId(userDetails.getBilkentId());
        }
        
        if (userDetails.getPhoneNumber() != null && !userDetails.getPhoneNumber().equals(existingUser.getPhoneNumber())) {
            if (isPhoneNumberExists(userDetails.getPhoneNumber())) {
                throw new BadRequestException("Phone number already in use");
            }
            existingUser.setPhoneNumber(userDetails.getPhoneNumber());
        }
        
        if (userDetails.getBloodType() != null) {
            existingUser.setBloodType(userDetails.getBloodType());
        }
        
        return userRepository.save(existingUser);
    }
}
