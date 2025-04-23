package com.bilkom.repository;

import com.bilkom.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByVerificationToken(String token);
    Optional<User> findById(Long id);
    Optional<User> findByBilkentId(String bilkentId);
    Optional<User> findByPhoneNumber(String phoneNumber);
    List<User> findByBloodType(String bloodType);
    Optional<User> findByFirstNameAndLastName(String firstName, String lastName);
    List<User> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    boolean existsByUserIdAndIsVerified(Long userId, boolean isVerified);
    boolean existsByFirstNameAndLastName(String firstName, String lastName);
    boolean existsByBilkentId(String bilkentId);
    boolean existsByPhoneNumber(String phoneNumber);
}