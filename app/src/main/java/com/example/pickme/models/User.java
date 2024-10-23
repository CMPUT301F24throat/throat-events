package com.example.pickme.models;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a user in the app
 * Responsibilities:
 * Acts as a blueprint for the user object stored in the users collection
 **/

public final class User {

    // Basic Profile Information
    private String userID; // Unique string for all users, for easy identification.
    private String userName; // Input username string for all users.
    private String emailAddress;
    private String contactNumber;
    private String profilePictureID; // Finds URL to profile picture.
    private LocalDateTime createdAt;

    // Preferences & Account Type
    private boolean isAdmin;
    private boolean enableGeoLocation;
    // private String[] deviceID = {"123", "22131"};

    public static class NotificationPreferences {
        private boolean eventNotifications;
        private boolean organizerNotifications;
        private boolean adminNotifications;
    }

    public User() {
        // Firebase framework will go here
    }

    public User(String userID, String userName, String emailAddress, String contactNumber, String profilePictureID, LocalDateTime createdAt, boolean isAdmin, boolean enableGeoLocation, String[] deviceID, boolean notificationPreferences) {
        this.userID = userID;
        this.userName = userName;
        this.emailAddress = emailAddress;
        this.contactNumber = contactNumber;
        this.profilePictureID = profilePictureID;
        this.createdAt = createdAt;
        this.isAdmin = isAdmin;
        this.enableGeoLocation = enableGeoLocation;
        // this.notificationPreferences = ();
        // this.deviceIDs.add(deviceID);
    }

    // Getters
    public String getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getProfilePictureID() {
        return profilePictureID;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public boolean isEnableGeoLocation() {
        return enableGeoLocation;
    }

    // getter for deviceIDS

    // getter for notification preferences

    // Setters
    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setUserName(String userName) {
        if (userName.isEmpty()) {
            throw new IllegalArgumentException("Username is empty");
        }
        this.userName = userName;
    }

    public void setEmailAddress(String emailAddress) {
        if (!emailAddress.contains("@")) {
            throw new IllegalArgumentException("Invalid input.");
        }
        this.emailAddress = emailAddress;
    }

    public void setContactNumber(String contactNumber) {
        if (contactNumber.isEmpty()) {
            throw new IllegalArgumentException("Invalid input.");
        }
        this.contactNumber = contactNumber;
    }

    public void setProfilePictureID(String profilePictureID) {
        this.profilePictureID = profilePictureID;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public void setEnableGeoLocation(boolean enableGeoLocation) {
        this.enableGeoLocation = enableGeoLocation;
    }

    // set for notification preferences

    // set for deviceIDs

    // Grabs hashCode for userID which is their unique code
    @Override
    public int hashCode() {
        return Objects.hash(userID);
    }
}

