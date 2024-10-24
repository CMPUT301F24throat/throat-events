package com.example.pickme.repositories;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Handles image uploads and metadata storage
 * Responsibilities:
 * CRUD operations for image data
 * Upload images (profile pictures, event posters) to Firebase Storage
 * Save image metadata to the images collection
 * Delete images when required (e.g., user removes profile picture)
 **/

public class ImageRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference imageRef = db.collection("images");

    // Create a new image

}
