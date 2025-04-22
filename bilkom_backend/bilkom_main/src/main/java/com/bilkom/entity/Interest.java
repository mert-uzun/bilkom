package com.bilkom.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

@Entity
@Table(name = "interests")
public class Interest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interest_id")
    private Long interestId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "interest_name", nullable = false)
    private String interestName;

    @Column(name = "interest_description", nullable = false)
    private String interestDescription;
}
