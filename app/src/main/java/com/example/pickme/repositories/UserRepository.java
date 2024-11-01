package com.example.pickme.repositories;

import android.util.Log;

import com.example.pickme.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Handles interactions with the users collection
 * @author sophiecabungcal
 * @version 1.0
 * Responsibilities:
 * CRUD operations for user data
 */

public class UserRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference usersRef = db.collection("users");

    // Create a new user with auto-generated ID
    public void addUser(User user, OnCompleteListener<Object> onCompleteListener) {

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener((task) -> {
            db.runTransaction(transaction -> {
                DocumentReference newUserRef = usersRef.document();
                user.setUserId(newUserRef.getId());
                user.setRegToken(task.getResult());

                return null;
            }).addOnCompleteListener(onCompleteListener)
            .addOnFailureListener(e -> {
                Log.i("ERROR", "Transaction failed: " + e.getMessage());
            });

        });
    }

    // Read a user by ID
    public void getUserById(String userId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        usersRef.document(userId).get().addOnCompleteListener(onCompleteListener);
    }

    // Update a user
    public void updateUser(User user) {
        usersRef.document(user.getUserId()).set(user);
    }

    // Delete a user by ID
    public void deleteUser(String userId) {
        usersRef.document(userId).delete();
    }

    // Read all users
    public void getAllUsers(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        usersRef.get().addOnCompleteListener(onCompleteListener);
    }
}
