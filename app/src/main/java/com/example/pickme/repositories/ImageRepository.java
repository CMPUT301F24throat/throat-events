package com.example.pickme.repositories;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.GridView;

import androidx.annotation.NonNull;

import com.example.pickme.models.Image;
import com.example.pickme.utils.GalleryAdapter;
import com.example.pickme.utils.ImageQuery;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles interactions with the images collection <br>
 * Responsibilities:
 * CRUD operations for image data
 * @version 1.4
 */
public class ImageRepository {
    private final String TAG = "ImageRepository";
    // authentication
    private final String auth_uid;
    // image storage
    private final StorageReference imgStorage;
    // database access
    private final FirebaseFirestore db;
    private final CollectionReference imgCollection;
    private static ImageRepository instance;

    /**
     * Callback for handling duplicate checking
     */
    private interface duplicateCallback {
        void hasDuplicate(DocumentReference doc);
        void noDuplicate();
    }

    /**
     * Constructs a new ImageRepository for image CRUD operations.
     */
    public ImageRepository() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth_uid = auth.getUid();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        imgStorage = storage.getReference();

        db = FirebaseFirestore.getInstance();
        imgCollection = db.collection("images");

        // anonymous auth
        auth.signInAnonymously().addOnSuccessListener(authResult ->
                Log.d(TAG, "AUTH: uid " + auth_uid + " successful authentication")
        );
    }

    /**
     * Singleton pattern to ensure only one instance of the repository is created.
     *
     * @return The instance of the ImageRepository.
     */
    public static ImageRepository getInstance() {
        if (instance == null) {
            instance = new ImageRepository();
        }
        return instance;
    }

    /**
     * Checks Firestore DB for image duplicates and
     * calls back to duplicateCallback based on result.
     * @param i Image object to check duplicates for
     * @param callback The duplicateCallback for conditional handling
     */
    private void checkDuplicates(Image i, duplicateCallback callback) {
        String uploaderId = i.getUploaderId();
        String imageAssociation = i.getImageAssociation();
        String imageType = i.getImageType().toString();

        // check for an already existing image of the same type
        Query query = imgCollection
                .where(Filter.and(
                        Filter.equalTo("imageType", imageType),
                        Filter.equalTo("uploaderId", uploaderId),
                        Filter.equalTo("imageAssociation", imageAssociation)
                ));

        query.addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.e(TAG, "checkDuplicates: Listen failed.", e);
                return;
            }

            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                Log.d(TAG, "checkDuplicates: DB query successful, duplicate found");
                DocumentReference doc = querySnapshot
                        .getDocuments()
                        .get(0)
                        .getReference();
                callback.hasDuplicate(doc);
            } else {
                Log.d(TAG, "checkDuplicates: DB query returned empty, no duplicates found");
                callback.noDuplicate();
            }
        });
    }

    /**
     * Uploads a new image or updates an existing one with attached URI to FirebaseStorage,
     * then stores the image information to Firestore DB.
     *
     * @param i The image object to upload
     * @param uri The image URI to be attached
     */
    public void upload(@NonNull Image i, @NonNull Uri uri, OnCompleteListener<Image> listener) {
        checkDuplicates(i, new duplicateCallback() {
            @Override
            public void hasDuplicate(DocumentReference doc) {
                uploadUriToFirebase(i, doc, uri, listener);
            }

            @Override
            public void noDuplicate() {
                DocumentReference doc = imgCollection.document();
                uploadUriToFirebase(i, doc, uri, listener);
            }
        });
    }

    /**
     * Uploads an image URL to Firestore DB. Used for generated images.
     * @param i Image object of which the URL being uploaded is associated with
     * @param data The bytes of a bitmap to upload
     */
    public void upload(@NonNull Image i, byte[] data, OnCompleteListener<Image> listener) {
        checkDuplicates(i, new duplicateCallback() {
            @Override
            public void hasDuplicate(DocumentReference doc) {
                uploadBytes(i, doc, data, listener);
            }

            @Override
            public void noDuplicate() {
                DocumentReference doc = imgCollection.document();
                uploadBytes(i, doc, data, listener);
            }
        });
    }

    /**
     * upload() helper function to update existing images
     * @param i Image object of which the URL being uploaded is associated with
     * @param doc The document reference from the query result to update
     * @param uri The uri to update with
     */
    private void uploadUriToFirebase(Image i, DocumentReference doc, Uri uri, OnCompleteListener<Image> listener) {
        String imageId = doc.getId();
        String uploaderId = i.getUploaderId();
        String imageType = i.getImageType().toString();
        StorageReference imgRef = imgStorage.child(imageType).child(uploaderId).child(imageId);

        // update storage file
        imgRef
                .putFile(uri)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, String.format("update: File %s updated", imageId));

                        // update document
                        imgRef.getDownloadUrl().addOnSuccessListener(url -> {
                            // db store
                            i.setImageUrl(url.toString());
                            db
                                    .runTransaction(transaction -> {
                                        transaction.set(doc, i);
                                        return i;
                                    })
                                    .addOnCompleteListener(listener);
                        });
                    }
                });
    }

    private void uploadBytes(Image i, DocumentReference doc, byte[] data, OnCompleteListener<Image> listener) {
        String imageId = doc.getId();
        String uploaderId = i.getUploaderId();
        String imageType = i.getImageType().toString();
        StorageReference imgRef = imgStorage.child(imageType).child(uploaderId).child(imageId);

        // update storage file
        imgRef
                .putBytes(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, String.format("update: File %s updated", imageId));

                        // update document
                        imgRef.getDownloadUrl().addOnSuccessListener(url -> {
                            // db store
                            i.setImageUrl(url.toString());
                            db
                                    .runTransaction(transaction -> {
                                        transaction.set(doc, i);
                                        return i;
                                    })
                                    .addOnCompleteListener(listener);
                        });
                    }
                });
    }

    /**
     * Download an image from Firestore db.
     * <br>
     * <b>Requires the ImageQuery callback to access the query data.</b>
     * @param i Image object with download info
     * @param callback <i>new ImageQuery()</i>
     * @see com.example.pickme.utils.ImageQuery
     */
    public void download(@NonNull Image i, @NonNull ImageQuery callback) {
        String imageAssociation = i.getImageAssociation();
        String imageType = i.getImageType().toString();

        Query query = imgCollection
                .where(Filter.and(
                        Filter.equalTo("imageType", imageType),
                        Filter.equalTo("imageAssociation", imageAssociation)));

        query.addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.e(TAG, "download: Listen failed.", e);
                return;
            }

            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                Image queriedImage = querySnapshot
                        .getDocuments()
                        .get(0)
                        .toObject(Image.class);
                callback.onSuccess(queriedImage);
                Log.d(TAG, "download: Query successful, sent Image to callback");
            } else {
                Log.d(TAG, "download: Query returned empty");
                callback.onEmpty();
            }
        });
    }

    /**
     * Delete an image from Firestore db.
     * @param i Image object to be deleted
     */
    public void delete(@NonNull Image i, OnCompleteListener<Image> listener) {
        String uploaderId = i.getUploaderId();
        String imageAssociation = i.getImageAssociation();
        String imageType = i.getImageType().toString();

        Query query = imgCollection
                .where(Filter.and(
                        Filter.equalTo("imageType", imageType),
                        Filter.equalTo("uploaderId", uploaderId),
                        Filter.equalTo("imageAssociation", imageAssociation)
                ));

        query.addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.e(TAG, "delete: Listen failed.", e);
                return;
            }

            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                DocumentReference doc = querySnapshot
                        .getDocuments()
                        .get(0)
                        .getReference();

                String imageId = doc.getId();

                Log.d(TAG, "delete: Query successful");
                Log.d(TAG, String.format("delete: Deleting file %s", imageId));
                imgStorage
                        .child(imageType)
                        .child(uploaderId)
                        .child(imageId)
                        .delete()
                        .addOnCompleteListener(deleteFileTask -> {
                            if (deleteFileTask.isSuccessful()) {
                                Log.d(TAG, "DB: Deleting document " + imageId);
                                db
                                        .runTransaction(transaction -> {
                                            transaction.delete(doc);
                                            return i;
                                        })
                                        .addOnCompleteListener(listener);
                            } else {
                                Log.d(TAG, String.format("delete: File %s deletion failed!", imageId));
                            }
                        });

            } else {
                Log.d(TAG, "delete: Query returned empty, no deletion occurred");
            }
        });
    }

    /**
     * Gets every image URL stored in Firebase DB and puts it into the passed gridView object
     * @param context The activity in which the gallery resides
     * @param gallery The gridView you want to show the images
     */
    public void getAllImages(@NonNull Context context, @NonNull GridView gallery) {
        imgCollection.addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.e(TAG, "getAllImages: Listen failed.", e);
                return;
            }

            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                Log.d(TAG, "DB: Query all successful");
                // acquired list of image documents
                List<DocumentSnapshot> docs = querySnapshot.getDocuments();

                // extracting list of image urls
                ArrayList<String> imageUrls = new ArrayList<>();
                for (DocumentSnapshot doc : docs) {
                    String url = (String) doc.get("imageUrl");
                    imageUrls.add(url);
                }
                // clearing and resetting adapter
                gallery.setAdapter(null);
                GalleryAdapter adapter = new GalleryAdapter(context, imageUrls);
                gallery.setAdapter(adapter);
            } else {
                Log.d(TAG, "DB: Query all returned empty");
            }
        });
    }
}