package com.example.pickme.models;

import androidx.annotation.NonNull;

import com.example.pickme.repositories.UserRepository;
import com.example.pickme.utils.UserNotification;
import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class that can represent, validate, and stores a user in the app.
 *
 * @version 1.2
 *
 * Responsibilities:
 * - Models a user in the users collection.
 * - Validates user data to ensure formats match.
 * - Tracks user activity status and manages admin status.
 * - Contains full name string format.
 **/
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String defaultProfilePictureUrl = "default_profile_picture_url";
    private transient UserRepository userRepository;

    // User Profile Information
    private String userAuthId; // Unique string for user, for easy identification.
    private String firstName; // First name of user.
    private String lastName; // Last name of user.
    private String emailAddress; // Email address of user.
    private String contactNumber; // Contact number of user.
    private String profilePictureUrl = defaultProfilePictureUrl; // Customizable user profile picture.
    private boolean isOnline; // Checks if the user is currently online.
    private ArrayList<UserNotification> userNotifications = new ArrayList<UserNotification>(); //list of user's notifications
    private ArrayList<String> eventIDs = new ArrayList<>(); //list of events signed up to

    // User Preferences & Permissions
    private String deviceId; // Attaches on device to user
    private String regToken; //firebase token for notification sending
    protected boolean isAdmin; // Permission to allow user admin status
    private boolean notificationEnabled; // Permission to allow notifications
    private boolean geoLocationEnabled; // Permission to track user's location

    // User Timestamps
    private transient Timestamp createdAt; // When was the account created.
    private transient Timestamp updatedAt; // When was the profile last updated.
    private static User user; // Tracks the active user throughout the app's lifecycle.

    public User() {}

    // Constructor
    public User(UserRepository userRepository, String userAuthId, String firstName, String lastName, String emailAddress, String contactNumber, String profilePictureUrl, boolean isAdmin, String deviceId, boolean notificationEnabled, boolean geoLocationEnabled, boolean isOnline) {
        this.userRepository = userRepository;
        this.userAuthId = userAuthId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.contactNumber = contactNumber;
        this.profilePictureUrl = profilePictureUrl;
        this.deviceId = deviceId;
        this.isAdmin = isAdmin;
        this.notificationEnabled = notificationEnabled;
        this.geoLocationEnabled = geoLocationEnabled;
        this.isOnline = isOnline;
        this.createdAt = Timestamp.now();
        this.updatedAt = this.createdAt;
    }

    //---------- Get/Set User Profile Information --------------------
    public String getUserId() {
        return deviceId;
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

    public ArrayList<UserNotification> getUserNotifications() {
        return userNotifications;
    }

    public void setUserNotifications(ArrayList<UserNotification> userNotifications) {
        this.userNotifications = userNotifications;
    }

    public void addUserNotification(UserNotification userNotification){
        this.userNotifications.add(userNotification);
    }

    public ArrayList<String> getEventIDs() {
        return eventIDs;
    }

    public void setEventIDs(ArrayList<String> eventIDs) {
        this.eventIDs = eventIDs;
    }

    //---------- Get/Set User Preferences & Permissions --------------------
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        this.updatedAt = Timestamp.now();
    }

    public String getRegToken() { return regToken; }

    public void setRegToken(String token) {
        this.regToken = token;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
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

    public static synchronized User getInstance() {
        return User.user;
    }

    public static synchronized void setInstance(User newUser) {
        User.user = newUser;
    }

    //---------- Validate User Information --------------------
    public static boolean validateFirstName(String firstName) {
        return firstName != null && firstName.matches("^[A-Za-z]+(-[A-Za-z]+)*$"); // Credits: ChatGBT: How do I only validate hyphens in the middle.
    }

    public static boolean validateLastName(String lastName) {
        return lastName.matches("^[A-Za-z]+(-[A-Za-z]+)*$");
    }

    public static boolean validateEmailAddress(String emailAddress) {
        String[] validEmailAddressDomains = {".com", ".ca", ".net", ".org", ".kr", ".co", ".uk", "ir", ".ch"};

        if (!emailAddress.contains("@")) {
            return false;
        }

        for (String emailAddressDomain : validEmailAddressDomains) {
            if (emailAddress.endsWith(emailAddressDomain)) {
                return true;
            }
        }
        return false;
    }

    public static boolean validateContactInformation(String contactNumber) {
        return contactNumber.matches("\\+?[0-9\\-() ]{7,15}");
    }

    //---------- Information Transformations --------------------
    @NonNull
    public String fullName(String firstName, String lastName) {
        // Function to concatenate the user's full name.
        return (firstName + " " + lastName);
    }

    public void signup(String newFirstName, String newLastName) {
        // Function that sets a user's required first and optional last name.
        setFirstName(newFirstName);
        setLastName(newLastName);
        this.updatedAt = Timestamp.now();
    }
}


