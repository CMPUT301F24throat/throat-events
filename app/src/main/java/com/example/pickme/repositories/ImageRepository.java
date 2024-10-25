package com.example.pickme.repositories;

import android.net.Uri;
import android.util.Log;

import com.example.pickme.models.Enums.ImageType;
import com.example.pickme.models.Image;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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

    public ImageRepository() {
        // temporary anonymous auth
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInAnonymously().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("ImageRepository", "AUTH: Successful authentication");
            }
        });
    }

    // TODO: fix nesting due to concurrency issues,
    //  separate methods for user/event uploads (once grabbing user/event id's becomes available)
    /**
     * Uploads an image URI to FirebaseStorage, then stores the image information to Firestore DB
     * @param imgUri The image URI to upload (e.g. one obtained from an image picker)
     */
    public void upload(Uri imgUri) {
        // creating document first to generate unique id
        DocumentReference imgDoc = imgCollection.document();

        // setting child storage reference to the unique id
        StorageReference imgRef = imgStorage.child(imgDoc.getId());

        // storing the image in firebasestorage
        imgRef
            .putFile(imgUri)
            .addOnSuccessListener(taskSnapshot -> {
                Log.d("ImageRepository", "STORAGE: URI " + imgUri + " upload successful");

                // now get the image download url...
                imgRef.getDownloadUrl().addOnSuccessListener(uri -> {

                    // ... and create a new image object to store to DB
                    // TODO: Change fields to user/event getter methods
                    Image img = new Image(
                            uri.toString(),
                            ImageType.PROFILE_PICTURE,
                            "user",
                            "1234");

                    // db store
                    imgDoc
                        .set(img.getUploadPackage())
                        .addOnSuccessListener(unused ->
                                Log.d("ImageRepository","DB: Upload successful"));
                });
            });
    }

    public void download() {
    }
}
