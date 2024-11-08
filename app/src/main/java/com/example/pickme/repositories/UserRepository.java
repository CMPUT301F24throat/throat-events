package com.example.pickme.repositories;

import android.util.Log;

import com.example.pickme.models.Image;
import com.example.pickme.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Handles interactions with the users collection in our Firebase Firestore.
 *
 * @author Kenneth (aesoji)
 * @version 1.2
 *
 * Responsibilities:
 * - CRUD operations for user data based on DeviceID
 */
public class UserRepository {
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private CollectionReference usersRef;

    public UserRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();

        // Initializes usersRef after proper assignment.
        if (db != null) {
            this.usersRef = db.collection("users");
        }
    }

    public UserRepository(FirebaseFirestore db, FirebaseAuth auth, CollectionReference usersRef) {
        this.db = db;
        this.auth = auth;
        this.usersRef = usersRef;
    }

    public void getUserByDeviceId(String deviceId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        if (deviceId == null || deviceId.isEmpty()) {
            Log.e("UserRepository", "DeviceID is required for fetching a user.");
            return;
        }
        usersRef.document(deviceId).get().addOnCompleteListener(onCompleteListener);
    }

    public void addUser(String firstName, String lastName, String email, String contact, String deviceId, OnUserCreatedCallback callback) {
        // Adds a user to our database using DeviceId as the DocumentID
        if (deviceId == null || deviceId.isEmpty()) {
            callback.onFailure("Device ID is missing. Unable to create user account.");
            return;
        }

        auth.signInAnonymously().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String userAuthId = Objects.requireNonNull(task.getResult().getUser()).getUid();

                // Creates the User object
//                String initials = String.valueOf(firstName.charAt(0)) + lastName.charAt(0);

                String initials = String.valueOf(firstName.charAt(0));
                Image newImage = new Image(deviceId, deviceId);
                newImage.generate(initials, task1 -> {
                    if (task1.isSuccessful()) {
                        newImage.setImageUrl(task1.getResult().getImageUrl());
                        User newUser = new User(this, userAuthId, firstName, lastName, email, contact, newImage.getImageUrl(), false, deviceId, true, false, true);
                        newUser.signup(firstName, lastName);
                        User.setInstance(newUser);

                        // Save user data to Firestore using DeviceID as the document ID
                        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task2 -> {
                            newUser.setRegToken(task2.getResult());
                            db.collection("users").document(deviceId)
                                    .set(newUser)
                                    .addOnSuccessListener(aVoid -> callback.onSuccess(newUser))
                                    .addOnFailureListener(e -> callback.onFailure("Failed to save user data: " + e.getMessage()));
                        });
                    }
                });

            } else {
                callback.onFailure("Authentication failed!");
            }
        });
    }

        public void updateUser (User user, OnCompleteListener < Void > onCompleteListener){
            if (user == null || user.getDeviceId() == null || user.getDeviceId().isEmpty()) {
                Log.e("UserRepository", "Invalid user or user ID.");
                return;
            }

            Map<String, Object> updates = new HashMap<>();
            updates.put("firstName", user.getFirstName());
            updates.put("lastName", user.getLastName());
            updates.put("emailAddress", user.getEmailAddress());
            updates.put("contactNumber", user.getContactNumber());
            updates.put("geoLocationEnabled", user.isGeoLocationEnabled());
            updates.put("notificationEnabled", user.isNotificationEnabled());
            updates.put("profilePictureUrl", user.getProfilePictureUrl());

            usersRef.document(user.getDeviceId())
                    .update(updates)
                    .addOnCompleteListener(onCompleteListener)
                    .addOnFailureListener(e -> Log.e("UserRepository", "Failed to update user data in Firestore", e));
        }

        public void deleteUser (String deviceId){
            if (deviceId == null || deviceId.isEmpty()) {
                Log.e("UserRepository", "DeviceID is required for deleting a user.");
                return;
            }

            usersRef.document(deviceId)
                    .delete()
                    .addOnSuccessListener(aVoid -> Log.d("UserRepository", "User deleted successfully"))
                    .addOnFailureListener(e -> Log.e("UserRepository", "Failed to delete user: " + e.getMessage(), e));
        }

        public void getAllUsers (OnCompleteListener < QuerySnapshot > onCompleteListener) {
            usersRef.get().addOnCompleteListener(onCompleteListener);
        }

        public interface OnUserCreatedCallback {
            // Defines an interface for callback.
            void onSuccess(User user);

            void onFailure(String errorMessage);
        }

    }
