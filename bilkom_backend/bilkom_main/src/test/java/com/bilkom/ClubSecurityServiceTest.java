package com.bilkom;

import com.bilkom.entity.Club;
import com.bilkom.entity.ClubExecutive;
import com.bilkom.entity.User;
import com.bilkom.enums.ClubRegistrationStatus;
import com.bilkom.enums.UserRole;
import com.bilkom.repository.ClubExecutiveRepository;
import com.bilkom.repository.ClubRepository;
import com.bilkom.repository.UserRepository;
import com.bilkom.service.ClubSecurityService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Timestamp;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for ClubSecurityService to ensure proper authorization checks for club operations.
 * Uses real repositories instead of mocks to test with the actual database.
 * 
 * @author Mert Uzun
 * @version 1.1
 */
@SpringBootTest
@ActiveProfiles("test")
public class ClubSecurityServiceTest {

    @Autowired
    private ClubSecurityService clubSecurityService;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private ClubExecutiveRepository clubExecutiveRepository;

    @Autowired
    private UserRepository userRepository;

    private User clubHead;
    private User clubExecutive;
    private User regularMember;
    private User adminUser;
    private Club testClub;
    private Club inactiveClub;
    private Long clubId;
    private Long inactiveClubId;
    private ClubExecutive executiveRelationship;

    @BeforeEach
    public void setUp() {
        // Create and save test users to the database
        clubHead = createUser("club.head@bilkent.edu.tr", UserRole.CLUB_HEAD);
        clubExecutive = createUser("club.executive@bilkent.edu.tr", UserRole.CLUB_EXECUTIVE);
        regularMember = createUser("regular.member@bilkent.edu.tr", UserRole.USER);
        adminUser = createUser("admin.user@bilkent.edu.tr", UserRole.ADMIN);

        // Create and save test club to the database
        testClub = new Club();
        testClub.setClubName("Test Club " + System.currentTimeMillis());
        testClub.setClubDescription("Test Description");
        testClub.setClubHead(clubHead);
        testClub.setStatus(ClubRegistrationStatus.APPROVED);
        testClub.setActive(true);
        testClub.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        
        testClub = clubRepository.save(testClub);
        clubId = testClub.getClubId();
        
        // Create an inactive club
        inactiveClub = new Club();
        inactiveClub.setClubName("Inactive Club " + System.currentTimeMillis());
        inactiveClub.setClubDescription("Inactive Description");
        inactiveClub.setClubHead(clubHead);
        inactiveClub.setStatus(ClubRegistrationStatus.APPROVED);
        inactiveClub.setActive(false);
        inactiveClub.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        
        inactiveClub = clubRepository.save(inactiveClub);
        inactiveClubId = inactiveClub.getClubId();
        
        // Create a real club executive relationship
        executiveRelationship = new ClubExecutive();
        executiveRelationship.setClub(testClub);
        executiveRelationship.setUser(clubExecutive);
        executiveRelationship.setPosition("Test Position");
        executiveRelationship.setActive(true);
        executiveRelationship.setJoinDate(new Timestamp(System.currentTimeMillis()));
        executiveRelationship = clubExecutiveRepository.save(executiveRelationship);
    }

    @AfterEach
    public void tearDown() {
        // Clean up - delete created entities
        // The annotation should rollback these changes,
        // but explicit cleanup ensures test isolation
        clubExecutiveRepository.deleteAll();
        clubRepository.delete(testClub);
        clubRepository.delete(inactiveClub);
        userRepository.delete(clubHead);
        userRepository.delete(clubExecutive);
        userRepository.delete(regularMember);
        userRepository.delete(adminUser);
    }

    @Test
    public void testClubHeadHasAccess() {
        // Test with real club head
        boolean hasAccess = clubSecurityService.isUserClubExecutiveOrHead(clubHead.getUserId(), clubId);
        assertTrue(hasAccess, "Club head should have access to club resources");
    }

    @Test
    public void testClubExecutiveHasAccess() {
        // Test with real club executive
        boolean hasAccess = clubSecurityService.isUserClubExecutiveOrHead(clubExecutive.getUserId(), clubId);
        assertTrue(hasAccess, "Club executive should have access to club resources");
    }

    @Test
    public void testRegularMemberNoAccess() {
        // Test with regular member
        boolean hasAccess = clubSecurityService.isUserClubExecutiveOrHead(regularMember.getUserId(), clubId);
        assertFalse(hasAccess, "Regular member should not have access to club resources");
    }

    @Test
    public void testNonExistentClub() {
        // Test access to non-existent club
        boolean hasAccess = clubSecurityService.isUserClubExecutiveOrHead(clubHead.getUserId(), 99999L);
        assertFalse(hasAccess, "No one should have access to a non-existent club");
    }

    @Test
    public void testInactiveExecutive() {
        // Get the executive relationship and set it to inactive
        executiveRelationship.setActive(false);
        clubExecutiveRepository.save(executiveRelationship);
        
        // Test with inactive executive
        boolean hasAccess = clubSecurityService.isUserClubExecutiveOrHead(clubExecutive.getUserId(), clubId);
        assertFalse(hasAccess, "Inactive executive should not have access to club resources");
    }

    @Test
    public void testAdminHasAccessToAllClubs() {
        // Test that admin has access to any club - this depends on implementation of ClubSecurityService
        // This assumes your security service has a method to check admin access
        boolean hasAccess = clubSecurityService.canUserManageClub(adminUser.getUserId(), clubId);
        assertTrue(hasAccess, "Admin should have access to all club resources");
    }
    
    @Test
    public void testClubHeadCanEditClub() {
        boolean canEdit = clubSecurityService.canUserEditClub(clubHead.getUserId(), clubId);
        assertTrue(canEdit, "Club head should be able to edit club");
    }
    
    @Test
    public void testExecutiveCannotEditClub() {
        boolean canEdit = clubSecurityService.canUserEditClub(clubExecutive.getUserId(), clubId);
        assertFalse(canEdit, "Club executive should not be able to edit club details");
    }
    
    @Test
    public void testCannotManageInactiveClub() {
        boolean canManage = clubSecurityService.canUserManageClub(clubHead.getUserId(), inactiveClubId);
        assertFalse(canManage, "Users should not be able to manage inactive clubs");
    }
    
    @Test
    public void testHeadCanManageEvents() {
        boolean canManage = clubSecurityService.canUserManageClubEvents(clubHead.getUserId(), clubId);
        assertTrue(canManage, "Club head should be able to manage club events");
    }
    
    @Test
    public void testExecutiveCanManageEvents() {
        boolean canManage = clubSecurityService.canUserManageClubEvents(clubExecutive.getUserId(), clubId);
        assertTrue(canManage, "Club executive should be able to manage club events");
    }
    
    @Test
    public void testRegularUserCannotManageEvents() {
        boolean canManage = clubSecurityService.canUserManageClubEvents(regularMember.getUserId(), clubId);
        assertFalse(canManage, "Regular user should not be able to manage club events");
    }

    private User createUser(String email, UserRole role) {
        User user = new User();
        user.setEmail(email);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setBilkentId("20" + UUID.randomUUID().toString().substring(0, 8));
        user.setPasswordHash("hashedPassword");
        user.setPhoneNumber("+90555" + (int)(Math.random() * 10000000));
        user.setBloodType("A+");
        user.setRole(role);
        user.setActive(true);
        user.setVerified(true);
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        return userRepository.save(user);
    }
} 