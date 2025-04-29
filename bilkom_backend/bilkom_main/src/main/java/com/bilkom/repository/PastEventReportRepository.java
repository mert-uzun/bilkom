package com.bilkom.repository;

import com.bilkom.entity.PastEventReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * PastEventReportRepository is an interface that extends JpaRepository for performing CRUD operations on PastEventReport entities.
 * It provides methods to interact with the database for PastEventReport-related data.
 * 
 * @author Elif Bozkurt
 * @version 1.0
 */
@Repository
public interface PastEventReportRepository extends JpaRepository<PastEventReport, Long> {
}