package com.example.pickme.models;

import com.example.pickme.repositories.UserRepository;

/**
 * Class that represents an admin in the app.
 *
 * @version 1.0
 *
 * Responsibilities:
 * - Models an administrator in the users collection as an extension of user.
 **/
public class Admin extends User {

    public Admin(UserRepository userRepository, String userId, String firstName, String lastName, String emailAddress, String contactNumber, String profilePictureUrl, boolean isAdmin, String deviceId, boolean notificationEnabled, boolean geoLocationEnabled, boolean isOnline) {
        super(userRepository, userId, firstName, lastName, emailAddress, contactNumber, profilePictureUrl, isAdmin, deviceId, notificationEnabled, geoLocationEnabled, isOnline);
    }
}
