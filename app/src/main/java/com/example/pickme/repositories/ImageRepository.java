package com.example.pickme.repositories;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Handles interactions with the images collection
 * @author sophiecabungcal
 * @version 1.0
 * Responsibilities:
 * CRUD operations for image data
 */
public class ImageRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference imageRef = db.collection("images");

    // TODO: not sure how images work yet; need to figure out how to store them in Firebase Storage

}
