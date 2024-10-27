package com.example.pickme.models;

import androidx.annotation.NonNull;
import com.example.pickme.repositories.UserRepository;
import com.google.firebase.Timestamp;

/**
 * Class that can represent, validate, and stores a user in the app
 * Responsibilities:
 * - Models a user in the users collection.
 * - Validates user data to ensure formats match.
 * - Tracks user activity status and manages admin status.
 * - Contains full name string format.
 **/

public class User {

    private static final String defaultProfilePictureUrl = "default_profile_picture_url";
    private final UserRepository userRepository;

    // User Profile Information
    private String userId; // Unique string for user, for easy identification.
    private String firstName; // First name of user
    private String lastName; // Last name of user
    private String emailAddress; // Email address of user
    private String contactNumber; // Contact number of user
    private String profilePictureUrl = defaultProfilePictureUrl; // Customizable user profile picture
    private boolean isOnline; // Checks if the user is currently online

    // User Preferences & Permissions
    private String deviceId; // Attaches on device to user
    protected boolean isAdmin; // Permission to allow user admin status
    private boolean notificationEnabled; // Permission to allow notifications
    private boolean geoLocationEnabled; // Permission to track user's location

    // User Timestamps
    private final Timestamp createdAt; // When was the account created
    private Timestamp updatedAt; // When was the profile last updated
    private static User user; // Tracks the active user throughout the app's lifecycle

    // Timestamp Function
    public User(UserRepository userRepository, String userId) {
        this.userRepository = userRepository;
        this.userId = userId;
        this.isAdmin = false;
        this.createdAt = Timestamp.now();
    }

    // Constructor
    public User(UserRepository userRepository, String userId, String firstName, String lastName, String emailAddress, String contactNumber, String profilePictureUrl, boolean isAdmin, String deviceId, boolean notificationEnabled, boolean geoLocationEnabled) {
        this.userRepository = userRepository;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.contactNumber = contactNumber;
        this.profilePictureUrl = profilePictureUrl != null ? profilePictureUrl : defaultProfilePictureUrl;
        this.deviceId = deviceId;
        this.isAdmin = isAdmin;
        this.notificationEnabled = notificationEnabled;
        this.geoLocationEnabled = geoLocationEnabled;
        this.createdAt = Timestamp.now();
        this.updatedAt = this.createdAt;
    }

    //---------- Get/Set User Profile Information --------------------
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        this.updatedAt = Timestamp.now();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        this.updatedAt = Timestamp.now();
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
        this.updatedAt = Timestamp.now();
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String phoneNumber) {
        this.contactNumber = phoneNumber;
        this.updatedAt = Timestamp.now();
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
        this.updatedAt = Timestamp.now();
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    //---------- Get/Set User Preferences & Permissions --------------------
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        this.updatedAt = Timestamp.now();
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
        this.updatedAt = Timestamp.now();
    }

    public boolean isNotificationEnabled() {
        return notificationEnabled;
    }

    public void setNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
        this.updatedAt = Timestamp.now();
    }

    public boolean isGeoLocationEnabled() {
        return geoLocationEnabled;
    }

    public void setGeoLocationEnabled(boolean geoLocationEnabled) {
        this.geoLocationEnabled = geoLocationEnabled;
        this.updatedAt = Timestamp.now();
    }

    //---------- Get/Set User Profile Timestamps --------------------
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public static User getInstance() {
        return User.user;
    }

    public static void setInstance(User newUser) {
        User.user = newUser;
    }

    //---------- Validate User Information --------------------
    public boolean validateFirstName(String firstName) {
        return firstName != null && firstName.matches("[A-Za-z]+");
    }

    public boolean validateLastName(String lastName) {
        return lastName != null && lastName.matches("[A-Za-z]+");
    }

    public boolean validateEmailAddress(String emailAddress) {
        String[] validEmailAddressDomains = {".com", ".ca", ".net", ".org", ".kr", ".co", ".uk", "ir", ".ch"};

        if (emailAddress == null || !emailAddress.contains("@")) {
            return false;
        }

        for (String emailAddressDomain : validEmailAddressDomains) {
            if (emailAddress.endsWith(emailAddressDomain)) {
                return true;
            }
        }
        return false;
    }

    public boolean validateContactInformation(String contactNumber) {
        return contactNumber != null && contactNumber.matches("\\+?[0-9\\-() ]{7,15}");
    }

    public boolean validateUserInformation() {
        return validateFirstName(firstName) && validateLastName(lastName) &&
                validateEmailAddress(emailAddress) && validateContactInformation(contactNumber);
    }

    //---------- Information Transformations --------------------
    @NonNull
    public String fullName(String firstName, String lastName) {
      return (firstName + " " + lastName);
    }

}


