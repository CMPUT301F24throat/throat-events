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
    private Date createdAt;
}
