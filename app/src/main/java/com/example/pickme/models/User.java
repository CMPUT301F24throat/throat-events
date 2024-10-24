package com.example.pickme.models;

import com.google.firebase.Timestamp;

/**
 * Represents a user in the app
 * Responsibilities:
 * Models a user in the users collection
 **/

public class User {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String profilePicImageId; // Optional field
    private boolean isAdmin;
    private String deviceId;
    private boolean notificationEnabled;
    private boolean geoLocationEnabled;
    private final Timestamp createdAt;
    private Timestamp updatedAt;

    public User() {
        this.createdAt = Timestamp.now();
    }

    public User(String firstName, String lastName, String email, String phoneNumber, String profilePicImageId, boolean isAdmin, String deviceId, boolean notificationEnabled, boolean geoLocationEnabled) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profilePicImageId = profilePicImageId;
        this.isAdmin = isAdmin;
        this.deviceId = deviceId;
        this.notificationEnabled = notificationEnabled;
        this.geoLocationEnabled = geoLocationEnabled;
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
    }

    public String getUserId() {
        return userId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        this.updatedAt = Timestamp.now();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        this.updatedAt = Timestamp.now();
    }

    public String getLastName() {
        return lastName;
    }

    public void setEmail(String email) {
        this.email = email;
        this.updatedAt = Timestamp.now();
    }

    public String getEmail() {
        return email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.updatedAt = Timestamp.now();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setprofilePicImageId(String profilePictureUrl) {
        this.profilePicImageId = profilePictureUrl;
        this.updatedAt = Timestamp.now();
    }

    public String getprofilePicImageId() {
        return profilePicImageId;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
        this.updatedAt = Timestamp.now();
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        this.updatedAt = Timestamp.now();
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
        this.updatedAt = Timestamp.now();
    }

    public boolean isNotificationEnabled() {
        return notificationEnabled;
    }

    public void setGeoLocationEnabled(boolean geoLocationEnabled) {
        this.geoLocationEnabled = geoLocationEnabled;
        this.updatedAt = Timestamp.now();
    }

    public boolean isGeoLocationEnabled() {
        return geoLocationEnabled;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
}
