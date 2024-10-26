package com.example.pickme.models;

import androidx.annotation.NonNull;
import com.google.firebase.Timestamp;

/**
 * Class that represents a user in the app
 * Responsibilities:
 * Models a user in the users collection
 **/

public class User {

    // User Profile Information
    private String userId; // Unique string for user, for easy identification.
    private String firstName; // First name of user
    private String lastName; // Last name of user
    private String emailAddress; // Email address of user
    private String contactNumber; // Contact number of user
    private String profilePictureUrl; // Customizable user profile picture

    // User Preferences & Permissions
    private String deviceId; // Attaches on device to user
    protected boolean isAdmin; // Permission to allow user admin status
    private boolean notificationEnabled; // Permission to allow notifications
    private boolean geoLocationEnabled; // Permission to track user's location

   // User Profile Timestamps
    private final Timestamp createdAt; // When was the account created
    private Timestamp updatedAt; // When was the profile last updated

    // Timestamp Function
    public User(String userId) {
        this.userId = userId;
        this.isAdmin = false;
        this.createdAt = Timestamp.now();
    }

    // Constructors
    public User(String userId, String firstName, String lastName, String emailAddress, String contactNumber, String profilePictureUrl, boolean isAdmin, String deviceId, boolean notificationEnabled, boolean geoLocationEnabled) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.contactNumber = contactNumber;
        this.profilePictureUrl = profilePictureUrl;
        this.deviceId = deviceId;
        this.isAdmin = isAdmin;
        this.notificationEnabled = notificationEnabled;
        this.geoLocationEnabled = geoLocationEnabled;
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
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

    //---------- Get/Set User Preferences & Permissions --------------------
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceIDd) {
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

    //---------- Validate User Information --------------------
    public boolean validateFirstName(String firstName) {
        return firstName != null && firstName.matches("[A-Za-z]+");
    }

    public boolean validateLastName(String lastName) {
        return lastName != null && lastName.matches("[A-Za-z]+");
    }

    public boolean validateEmailAddress(String emailAddress) {
        String[] validEmailAddressDomains = {".com", ".ca", ".net", ".org", ".kr", ".co", ".uk", "ir", ".ch"};

        if (emailAddress == null || emailAddress.contains("@")) {
            return false;
        }

        for (String emailAddressDomain : validEmailAddressDomains) {
            if (!emailAddress.endsWith(emailAddressDomain)) {
                return false;
            }
        }
        return true;
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
    @Override // Grabs user's full name
    public String toString() {
        return (firstName + " " + lastName);
    }
}