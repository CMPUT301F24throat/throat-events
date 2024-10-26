package com.example.pickme.models;

import com.google.firebase.Timestamp;

/**
 * Represents a user in the app
 * @author sophiecabungcal
 * @version 1.0
 */
public class User {

    /* User Attributes */
    // the unique user id; auto-generated by Firebase [non-nullable]
    private String userId;
    // the user's first name [non-nullable]
    private String firstName;
    // the user's last name [non-nullable]
    private String lastName;
    // the user's email [non-nullable]
    private String email;
    // the user's phone number [nullable]
    private String phoneNumber;
    // the user's profile picture image id referencing imageId from images collection [nullable]
    private String profilePicImageId;
    // boolean value representing whether the user is an admin [non-nullable]
    private boolean isAdmin;
    // the device id of the user's device [nullable]
    private String deviceId;
    // boolean value representing whether the user has enabled notifications [non-nullable]
    private boolean notificationEnabled;
    // boolean value representing whether the user has enabled geo location [non-nullable]
    private boolean geoLocationEnabled;
    // the date/time the user was created [non-nullable]
    private final Timestamp createdAt;
    // the date/time the user was last updated [non-nullable]
    private Timestamp updatedAt;

    private static User user;

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

    public void setUserId(String userId) {
        this.userId = userId;
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

    public static void setInstance(User u){
        User.user = u;
    }

    public static User getInstance(){
        return User.user;
    }
}
