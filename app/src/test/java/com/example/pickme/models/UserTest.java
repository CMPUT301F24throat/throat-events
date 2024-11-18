package com.example.pickme.models;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.pickme.repositories.UserRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for the User class.
 * This test class verifies the behavior, constraints, and validation logic of the User model class,
 * including constructors, updating, and verifying.
 *
 * @version 1.0
 * @author Kenneth Agonoy
 *
 * Responsibilities:
 * - Test that a user is created with the correct attributes.
 * - Ensures users input correct values.
 */

public class UserTest {

    private User user;

    @Mock
    private UserRepository mockUserRepository;

    // Testing Initialization
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        user = new User(mockUserRepository, "randomUserAuthId555", "John", "Doe",
                "john.doe@example.com", "+1234567890", "custom_profile_url",
                false, "randomDeviceId111", true, true, true);
    }

    @Test
    public void testUserCreation() {
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("john.doe@example.com", user.getEmailAddress());
    }

    // Testing Preferences & Permissions Setters and Getters
    @Test
    public void testSetAndGetFirstName() {
        user.setFirstName("Tyrone");
        assertEquals("Tyrone", "Tyrone", user.getFirstName());
    }

    @Test
    public void testSetAndGetLastName() {
        user.setLastName("Arianna");
        assertEquals("Arianna", "Arianna", user.getLastName());
    }

    @Test
    public void testSetAndGetEmailAddress() {
        user.setEmailAddress("alice.smith@example.com");
        assertEquals("alice.smith@example.com", user.getEmailAddress());
    }

    @Test
    public void testSetAndGetContactNumber() {
        user.setContactNumber("19876543210");
        assertEquals("19876543210", user.getContactNumber());
    }

    @Test
    public void testSetAndGetProfilePictureUrl() {
        user.setProfilePictureUrl("new_profile_picture_url");
        assertEquals("new_profile_picture_url", user.getProfilePictureUrl());
    }

    @Test
    public void testIsAndSetOnlineStatus() {
        user.setOnline(false);
        assertFalse(user.isOnline());
    }

    @Test
    public void testSetAndGetDeviceId() {
        user.setDeviceId("device456");
        assertEquals("device456", user.getDeviceId());
    }

    @Test
    public void testSetAndGetRegToken() {
        user.setRegToken("newRegToken");
        assertEquals("newRegToken", user.getRegToken());
    }

    @Test
    public void testIsAndSetAdminStatus() {
        user.setAdmin(false);
        assertFalse(user.isAdmin());
    }

    @Test
    public void testIsAndSetNotificationEnabled() {
        user.setNotificationEnabled(false);
        assertFalse(user.isNotificationEnabled());
    }

    @Test
    public void testIsAndSetGeoLocationEnabled() {
        user.setGeoLocationEnabled(false);
        assertFalse(user.isGeoLocationEnabled());
    }

    // Test Instance Management
    @Test
    public void testGetAndSetInstance() {
        User.setInstance(user);
        assertEquals(user, User.getInstance());
    }

    // Testing Validation Methods
    @Test
    public void testValidateFirstName() {
        assertTrue("John", User.validateFirstName("John"));
        assertTrue(User.validateLastName("John-Paul"));
        assertFalse(User.validateLastName("John232"));
    }

    @Test
    public void testValidateLastName() {
        assertTrue(User.validateFirstName("Dough"));
        assertTrue(User.validateLastName("Dough-Me"));
    }

    @Test
    public void testValidateEmailAddress() {
        assertTrue(User.validateEmailAddress("valid.email@example.com"));
        assertFalse(User.validateEmailAddress("invalid-email"));
        assertFalse(User.validateEmailAddress("missingdomain@com"));
        assertFalse(User.validateEmailAddress("missing.symbol.com"));
    }

    @Test
    public void testValidateContactInformation() {
        assertTrue(User.validateContactInformation("+1234567890"));
        assertFalse(User.validateContactInformation("12345"));
        assertFalse(User.validateContactInformation("invalid-phone"));
    }

    // Test Information Transformations
    @Test
    public void testFullName() {
        String fullName = user.fullName(user.getFirstName(), user.getLastName());
        assertEquals("John Doe", fullName);
    }

    // Test Signup Method
    @Test
    public void testSignup() {
        user.signup("NewFirstName", "NewLastName");
        assertEquals("NewFirstName", user.getFirstName());
        assertEquals("NewLastName", user.getLastName());
    }

}
