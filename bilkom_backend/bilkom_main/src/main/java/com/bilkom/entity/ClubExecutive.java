package com.bilkom.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import java.sql.Timestamp;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Id;

@Entity
@Table(name = "club_executives")
public class ClubExecutive {
    @Id
    @OneToOne
    @JoinColumn(name = "executive_id", referencedColumnName = "user_id", nullable = false, columnDefinition = "BIGINT")
    private User user;

    @ManyToOne
    @JoinColumn(name = "club_id", nullable = false, columnDefinition = "BIGINT")
    private Club club;

    @Column(name = "position", nullable = false, columnDefinition = "VARCHAR(255)")
    private String position;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean isActive; 
}
