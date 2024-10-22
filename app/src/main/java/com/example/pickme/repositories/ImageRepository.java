package com.example.pickme.repositories;

import android.util.Log;
import com.example.pickme.models.Image;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.HashMap;

/**
 * Handles image uploads and metadata storage
 * Responsibilities:
 * CRUD operations for image data
 * Upload images (profile pictures, event posters) to Firebase Storage
 * Save image metadata to the images collection
 * Delete images when required (e.g., user removes profile picture)
 **/

public class ImageRepository {
    //region Attributes

    private final FirebaseStorage storage;
    private final StorageReference storageRef;
    private final FirebaseFirestore db;
    private final CollectionReference imageCol;
    //endregion

    public ImageRepository() {
        this.storage = FirebaseStorage.getInstance();
        this.storageRef = storage.getReference();
        this.db = FirebaseFirestore.getInstance();
        this.imageCol = db.collection("images");
    }

    //region Methods
    // unfinished
    public void upload() {
        Image image = new Image();

        HashMap<String, Object> imgData = new HashMap<>();
        imgData.put("imageUrl", "placeholder");
        imgData.put("type", image.getType());
        imgData.put("uploadedBy", image.getUploadedBy());
        imgData.put("dateCreated", image.getDateCreated());
        imageCol
                .document()
                .set(imgData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "DocumentSnapshot successfully written!");
                    }
                });
    }
    //endregion
}
