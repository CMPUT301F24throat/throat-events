package com.example.pickme.repositories;

import com.example.pickme.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Handles all interactions with the users collection in Firestore
 * Responsibilities:
 * CRUD operations for user data
 **/

public class UserRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference usersRef = db.collection("users");

    // Create a new user
    public void addUser(User user, OnCompleteListener<DocumentReference> onCompleteListener) {
        usersRef.add(user).addOnCompleteListener(onCompleteListener);
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
