package com.bilkom.repository;

import com.bilkom.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {
    // Find club by name
    List<Club> findByName(String name);
    
    // Find all active clubs
    List<Club> findByIsActiveTrue();
    
    // Find clubs by name containing (case-insensitive)
    List<Club> findByNameContainingIgnoreCase(String name);
    
    // Check if club exists by name
    boolean existsByName(String name);
} 