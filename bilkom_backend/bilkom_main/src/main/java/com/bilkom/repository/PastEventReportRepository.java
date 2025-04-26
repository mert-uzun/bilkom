package com.bilkom.repository;

import com.bilkom.entity.PastEventReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PastEventReportRepository extends JpaRepository<PastEventReport, Long> {
}