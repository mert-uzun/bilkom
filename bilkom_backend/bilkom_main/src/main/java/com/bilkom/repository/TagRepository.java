package com.bilkom.repository;

import com.bilkom.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * TagRepository is an interface that extends JpaRepository for performing CRUD operations on Tag entities.
 * It provides methods to interact with the database for Tag-related data.
 * 
 * @author Elif Bozkurt
 * @version 1.0
 */
public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findByNameIn(List<String> names);
}
