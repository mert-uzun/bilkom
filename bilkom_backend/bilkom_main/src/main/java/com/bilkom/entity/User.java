package com.bilkom.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, columnDefinition = "BIGINT")
    private Long userId;

    @Column(name = "email", nullable = false, unique = true, columnDefinition = "VARCHAR(255)")
    private String email;

    @Column(name = "password_hash", nullable = false, columnDefinition = "VARCHAR(255)")
    private String passwordHash;

    @Column(name = "first_name", nullable = false, columnDefinition = "VARCHAR(255)")
    private String firstName;

    @Column(name = "last_name", nullable = false, columnDefinition = "VARCHAR(255)")
    private String lastName;

    @Column(name = "bilkent_id", nullable = false, unique = true, columnDefinition = "VARCHAR(15)")
    private String bilkentId;

    @Column(name = "user_role", nullable = false, columnDefinition = "VARCHAR(20)")
    private String userRole;

    @Column(name = "phone_number", nullable = false, unique = true, columnDefinition = "VARCHAR(15)")
    private String phoneNumber;

    @Column(name = "blood_type", nullable = false, columnDefinition = "VARCHAR(5)")
    private String bloodType;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    @Column(name = "is_verified", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isVerified = false;

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean isActive = true;

    @Column(name = "last_login", nullable = false, columnDefinition = "TIMESTAMP DEFAULT created_at")
    private Timestamp lastLogin;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Interest> interests = new ArrayList<>();
    
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClubMember> clubMemberships = new ArrayList<>();
    
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Event> createdEvents = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventParticipant> eventParticipations = new ArrayList<>();

    @Override
    public String toString() {
        return "User{" + "userId=" + userId + ", email=" + email + ", passwordHash=" + passwordHash + ", firstName=" + firstName + ", lastName=" + lastName + ", bilkentId=" + bilkentId + ", userRole=" + userRole + ", phoneNumber=" + phoneNumber + ", bloodType=" + bloodType + ", createdAt=" + createdAt + ", isVerified=" + isVerified + ", isActive=" + isActive + ", lastLogin=" + lastLogin + ", interests=" + interests + ", clubMemberships=" + clubMemberships + ", createdEvents=" + createdEvents + ", eventParticipations=" + eventParticipations + '}';
    }

    // GETTERS AND SETTERS
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBilkentId() {
        return bilkentId;
    }

    public void setBilkentId(String bilkentId) {
        this.bilkentId = bilkentId;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void updateLastLogin() {
        this.lastLogin = new Timestamp(System.currentTimeMillis());
    }

    public List<Interest> getInterests() {
        return interests;
    }

    public List<ClubMember> getClubMemberships() {
        return clubMemberships;
    }

    public List<Event> getCreatedEvents() {
        return createdEvents;
    }

    public List<EventParticipant> getEventParticipations() {
        return eventParticipations;
    }
}
