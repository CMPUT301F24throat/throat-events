package com.example.pickme.models;

import com.google.firebase.Timestamp;

/**
 * Class that represents a user in the app
 * Responsibilities:
 * Models a user in the users collection
 **/

public class User {

    // User Profile Information
    private String userID; // Unique string for all users, for easy identification.
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String contactNumber;
    private String profilePictureURL;

    // User Preferences & Permissions
    private String deviceID; // Attaches on device to user
    private boolean isAdmin;
    private boolean notificationEnabled;
    private boolean geoLocationEnabled;

   // User Profile Timestamps
    private final Timestamp createdAt;
    private Timestamp updatedAt;

    // Timestamp Function
    public User() {
        this.createdAt = Timestamp.now();
    }

    // Constructor
    public User(String firstName, String lastName, String emailAddress, String contactNumber, String profilePictureURL, boolean isAdmin, String deviceID, boolean notificationEnabled, boolean geoLocationEnabled) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.contactNumber = contactNumber;
        this.profilePictureURL = profilePictureURL;
        this.deviceID = deviceID;
        this.isAdmin = isAdmin;
        this.notificationEnabled = notificationEnabled;
        this.geoLocationEnabled = geoLocationEnabled;
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
    }

    //---------- Get/Set User Profile Information --------------------
    public String getUserID() {
        return userID;
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
        return profilePictureURL;
    }

    public void setProfilePictureURL(String profilePictureUrl) {
        this.profilePictureURL = profilePictureURL;
        this.updatedAt = Timestamp.now();
    }

    //---------- Get/Set User Preferences & Permissions --------------------
    public String isDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
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
        return contactNumber != null && contactNumber.matches("\\+?[0-9\\-\\(\\) ]{7,15}");
    }

    public boolean validateUserInformation() {
        return validateFirstName(firstName) && validateLastName(lastName) &&
                validateEmailAddress(emailAddress) && validateContactInformation(contactNumber);
    }

    //---------- Information Transformations --------------------

    @Override // Grabs user's full name
    public String toString() {
        return (firstName + " " + lastName);
    }
}