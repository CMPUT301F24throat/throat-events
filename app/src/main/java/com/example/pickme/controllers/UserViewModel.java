package com.example.pickme.controllers;

import com.example.pickme.models.User;
import com.example.pickme.repositories.UserRepository;
import com.google.firebase.Timestamp;

/**
 * Manages the user-related logic, like fetching user details, updating user profiles,
 * handling user preferences, and uploading profile images
 * <p>
 * Responsibilities:
 * - Get the user data from the UserRepository
 * - Expose LiveData<User> to observe changes in user data
 * - Handle user-specific actions like updating personal info, profile picture, notification preferences
 **/



public class UserViewModel {

    private final UserRepository userRepository;

    // Constructor that correctly matches the class name
    public UserViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Method to update user info and log changes
    public void updateUserInfo(User user) {


        // Log changes if needed
        logChanges(user.getUserId(), "firstName", "John", "Jane"); // Example values

        // Send updated user info to Firestore
        userRepository.updateUser(user);
    }

    // Private method to log changes
    private void logChanges(String userId, String field, String oldValue, String newValue) {
        String logEntry = Timestamp.now() + " | UserID: " + userId + " | Field: " + field +
                " changed from " + oldValue + " to " + newValue;
        System.out.println(logEntry); // Or save to an audit log
    }
}
