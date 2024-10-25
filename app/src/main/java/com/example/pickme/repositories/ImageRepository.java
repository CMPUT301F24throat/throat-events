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
    // image storage
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference imgStorage = storage.getReference();

    // database access
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference imgCollection = db.collection("images");

    // authentication
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    // TODO: not sure how images work yet; need to figure out how to store them in Firebase Storage

}
