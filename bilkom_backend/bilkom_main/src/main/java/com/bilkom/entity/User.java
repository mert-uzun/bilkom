package com.bilkom.entity;

import com.bilkom.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.bilkom.enums.AvatarRelativePaths;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
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

    @Column(name = "verification_token")
    private String verificationToken;

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

    @Column(name = "last_login", nullable = false)
    private Timestamp lastLogin;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false, columnDefinition = "ENUM('USER', 'CLUB_HEAD', 'ADMIN') DEFAULT 'USER'")
    private UserRole role = UserRole.USER;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Tag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ClubMember> clubMemberships = new ArrayList<>();

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Event> createdEvents = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<EventParticipant> eventParticipations = new ArrayList<>();

    @Column(name = "email_notifications", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean emailNotificationsEnabled = true;

    @Column(name = "sms_notifications", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean smsNotificationsEnabled = false;

    @Column(name = "profile_visibility", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'PUBLIC'")
    @Enumerated(EnumType.STRING)
    private ProfileVisibility profileVisibility = ProfileVisibility.PUBLIC;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ClubExecutive> clubExecutives = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "avatar_path", nullable = false, columnDefinition = "ENUM('AVATAR_1', 'AVATAR_2', 'AVATAR_3', 'AVATAR_4', 'AVATAR_5', 'AVATAR_6', 'AVATAR_7', 'AVATAR_8', 'AVATAR_9', 'AVATAR_10', 'AVATAR_11', 'AVATAR_12', 'AVATAR_13', 'AVATAR_14', 'AVATAR_15', 'AVATAR_16') DEFAULT 'AVATAR_1'")
    private AvatarRelativePaths avatarPath = AvatarRelativePaths.AVATAR_1;

    /**
     * Enum for profile visibility settings.
     */
    public enum ProfileVisibility {
        PUBLIC, // Visible to everyone
        MEMBERS, // Visible only to club members
        PRIVATE // Visible only to the user and admins
    }

    @Column(name = "fcm_token", columnDefinition = "TEXT")
    private String fcmToken;

    @jakarta.persistence.PrePersist
    protected void onCreate() {
        if (createdAt == null)
            createdAt = new Timestamp(System.currentTimeMillis());
        if (lastLogin == null)
            lastLogin = createdAt;
    }

    @Override
    public String toString() {
        return "User{" + "userId=" + userId + ", email=" + email + ", passwordHash=" + passwordHash + ", firstName="
                + firstName + ", lastName=" + lastName
                + ", bilkentId=" + bilkentId + ", phoneNumber=" + phoneNumber + ", bloodType=" + bloodType
                + ", createdAt=" + createdAt + ", isVerified="
                + isVerified + ", isActive=" + isActive + ", lastLogin=" + lastLogin + ", role=" + role
                + ", avatarPath=" + avatarPath + ", tags=" + tags + ", clubMemberships="
                + clubMemberships + ", createdEvents=" + createdEvents + ", eventParticipations=" + eventParticipations
                + '}';
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

    public List<Tag> getTags() {
        return tags;
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

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isEmailNotificationsEnabled() {
        return emailNotificationsEnabled;
    }

    public void setEmailNotificationsEnabled(boolean emailNotificationsEnabled) {
        this.emailNotificationsEnabled = emailNotificationsEnabled;
    }

    public boolean isSmsNotificationsEnabled() {
        return smsNotificationsEnabled;
    }

    public void setSmsNotificationsEnabled(boolean smsNotificationsEnabled) {
        this.smsNotificationsEnabled = smsNotificationsEnabled;
    }

    public ProfileVisibility getProfileVisibility() {
        return profileVisibility;
    }

    public void setProfileVisibility(ProfileVisibility profileVisibility) {
        this.profileVisibility = profileVisibility;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public AvatarRelativePaths getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(AvatarRelativePaths avatarPath) {
        this.avatarPath = avatarPath;
    }

    public List<ClubExecutive> getClubExecutives() {
        return clubExecutives;
    }

    public void setClubExecutives(List<ClubExecutive> clubExecutives) {
        this.clubExecutives = clubExecutives;
    }
}
