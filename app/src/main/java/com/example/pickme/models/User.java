package com.example.pickme.models;

import java.util.Date;

/**
 * Represents a user in the app
 * Responsibilities:
 * Acts as a blueprint for the user object stored in the users collection
 **/

public class User {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String profilePictureUrl; // Optional field
    private boolean isAdmin;
    private String deviceId;
    private boolean notificationEnabled;
    private boolean geoLocationEnabled;
    private final Date createdAt;
    private Date updatedAt;

    public User() {
        this.createdAt = new Date();
    }

    public User(String userId, String firstName, String lastName, String email, String phoneNumber, String profilePictureUrl, boolean isAdmin, String deviceId, boolean notificationEnabled, boolean geoLocationEnabled) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profilePictureUrl = profilePictureUrl;
        this.isAdmin = isAdmin;
        this.deviceId = deviceId;
        this.notificationEnabled = notificationEnabled;
        this.geoLocationEnabled = geoLocationEnabled;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        this.updatedAt = new Date();
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        this.updatedAt = new Date();
    }

    public void setEmail(String email) {
        this.email = email;
        this.updatedAt = new Date();
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.updatedAt = new Date();
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
        this.updatedAt = new Date();
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
        this.updatedAt = new Date();
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        this.updatedAt = new Date();
    }

    public void setNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
        this.updatedAt = new Date();
    }

    public void setGeoLocationEnabled(boolean geoLocationEnabled) {
        this.geoLocationEnabled = geoLocationEnabled;
        this.updatedAt = new Date();
    }
}
