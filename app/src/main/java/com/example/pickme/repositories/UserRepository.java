package com.example.pickme.repositories;

import android.util.Log;

import com.example.pickme.models.Image;
import com.example.pickme.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;
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
 * <p>
 * Responsibilities:
 * - CRUD operations for user data based on DeviceID
 *
 * @version 1.2
 */
public class UserRepository {
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private CollectionReference usersRef;

    /**
     * Default constructor that initializes Firebase Firestore and FirebaseAuth instances.
     */
    public UserRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();

        // Initializes usersRef after proper assignment.
        if (db != null) {
            this.usersRef = db.collection("users");
        }
    }

    /**
     * Constructor for dependency injection.
     *
     * @param db Firebase Firestore instance
     * @param auth Firebase Auth instance
     * @param usersRef Collection reference for users
     */
    public UserRepository(FirebaseFirestore db, FirebaseAuth auth, CollectionReference usersRef) {
        this.db = db;
        this.auth = auth;
        this.usersRef = usersRef;
    }

    /**
     * Fetches a user document by their device ID.
     *
     * @param deviceId The device ID of the user
     * @param onCompleteListener Listener to handle the completion of the task
     */
    public void getUserDocumentByDeviceId(String deviceId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        if (deviceId == null || deviceId.isEmpty()) {
            Log.e("UserRepository", "DeviceID is required for fetching a user.");
            return;
        }
        usersRef.document(deviceId).get().addOnCompleteListener(onCompleteListener);
    }

    /**
     * Adds a new user to the Firestore database.
     *
     * @param firstName The first name of the user
     * @param lastName The last name of the user
     * @param email The email address of the user
     * @param contact The contact number of the user
     * @param deviceId The device ID of the user
     * @param callback Callback to handle the result of the user creation
     */
    public void addUser(String firstName, String lastName, String email, String contact, String deviceId, OnUserCreatedCallback callback) {
        if (deviceId == null || deviceId.isEmpty()) {
            callback.onFailure("Device ID is missing. Unable to create user account.");
            return;
        }

        auth.signInAnonymously().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String userAuthId = Objects.requireNonNull(task.getResult().getUser()).getUid();

                String initials = String.valueOf(firstName.charAt(0));
                Image newImage = new Image(deviceId, deviceId);
                newImage.generate(initials, task1 -> {
                    if (task1.isSuccessful()) {
                        newImage.setImageUrl(task1.getResult().getImageUrl());
                        User newUser = new User(this, userAuthId, firstName, lastName, email, contact, newImage.getImageUrl(), false, deviceId, true, false, true);
                        newUser.signup(firstName, lastName);
                        User.setInstance(newUser);

                        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task2 -> {
                            newUser.setRegToken(task2.getResult());
                            db.collection("users").document(deviceId)  // set the user document ID to the deviceId
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

    /**
     * Updates an existing user's information in the Firestore database.
     *
     * @param user The user object containing updated information
     * @param onCompleteListener Listener to handle the completion of the task
     */
    public void updateUser(User user, OnCompleteListener<Void> onCompleteListener) {
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

    /**
     * Deletes a user from the Firestore database.
     *
     * @param deviceId The device ID of the user to be deleted
     */
    public void deleteUser(String deviceId) {
        if (deviceId == null || deviceId.isEmpty()) {
            Log.e("UserRepository", "DeviceID is required for deleting a user.");
            return;
        }

        usersRef.document(deviceId)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("UserRepository", "User deleted successfully"))
                .addOnFailureListener(e -> Log.e("UserRepository", "Failed to delete user: " + e.getMessage(), e));
    }

    /**
     * Fetches all users from the Firestore database.
     *
     * @param onCompleteListener Listener to handle the completion of the task
     */
    public void getAllUsers(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        usersRef.get().addOnCompleteListener(onCompleteListener);
    }

    /**
     * Fetches a user by their device ID (same as the document ID).
     *
     * @param deviceId           The device ID of the user
     * @param onCompleteListener Listener to handle the completion of the task
     */
    public void getUserByDeviceId(String deviceId, OnCompleteListener<User> onCompleteListener) {
        if (deviceId == null || deviceId.isEmpty()) {
            Log.e("UserRepository", "User ID is required for fetching a user.");
            return;
        }
        usersRef.document(deviceId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                User user = document.toObject(User.class);
                onCompleteListener.onComplete(Tasks.forResult(user));
            } else {
                onCompleteListener.onComplete(Tasks.forException(task.getException()));
            }
        });
    }

    /**
     * Callback interface for user creation.
     */
    public interface OnUserCreatedCallback {
        void onSuccess(User user);
        void onFailure(String errorMessage);
    }
}