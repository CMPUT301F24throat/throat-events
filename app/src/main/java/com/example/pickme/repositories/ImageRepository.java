package com.example.pickme.repositories;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.GridView;

import androidx.annotation.NonNull;

import com.example.pickme.models.Image;
import com.example.pickme.utils.GalleryAdapter;
import com.example.pickme.utils.ImageQuery;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Handles interactions with the images collection <br>
 * Responsibilities:
 * CRUD operations for image data
 * @author sophiecabungcal
 * @author etdong
 * @version 1.3
 */
public class ImageRepository {
    private final String TAG = "ImageRepository";

    //region Firebase initialization
    // authentication
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final String auth_uid = auth.getUid();

    // image storage
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference imgStorage = storage.getReference();

    // database access
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference imgCollection = db.collection("images");

    //endregion

    //region Constructors
    /**
     * Constructs a new ImageRepository for image CRUD operations.
     */
    public ImageRepository() {
        // temporary anonymous auth
        auth.signInAnonymously().addOnSuccessListener(authResult ->
                Log.d(TAG, "AUTH: uid " + auth_uid + " successful authentication")
        );
    }
    //endregion

    //region Class methods
     /**
     * Uploads a new image or updates an existing one with attached URI to FirebaseStorage,
     * then stores the image information to Firestore DB.
     *
     * @param i The image object to upload
     * @param uri The image URI to be attached
     */
    public void upload(@NonNull Image i, @NonNull Uri uri) {

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

        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // if existing image of the same type is found, update it
                        QuerySnapshot queryRes = task.getResult();
                        if (!queryRes.isEmpty()) {
                            Log.d(TAG, "upload: DB query successful, updating");
                            update(queryRes, uploaderId, uri);

                        } else {
                            Log.d(TAG, "upload: DB query returned empty, uploading");
                            uploadNew(i, uri);
                        }
                    }
                });
    }

    /**
     * upload() helper function for uploading new images
     * @param i The image object to upload
     * @param uri The image URI to be attached
     */
    private void uploadNew(@NonNull Image i, @NonNull Uri uri) {

        String uploaderId = i.getUploaderId();

        // creating document first to generate unique id
        DocumentReference imgDoc = imgCollection.document();
        String imageId = imgDoc.getId();

        // setting child storage reference to the unique id
        StorageReference imgRef = imgStorage.child(uploaderId).child(imageId);

        // storing the image in firebasestorage
        imgRef
                .putFile(uri)
                .addOnSuccessListener(taskSnapshot -> {
                    Log.d(TAG, String.format("upload: File %s upload successful", imageId));

                    // now get the image download url
                    imgRef.getDownloadUrl().addOnSuccessListener(url -> {
                        // db store
                        i.setImageUrl(url.toString());
                        db
                                .runTransaction(transaction -> {
                                    transaction.set(imgDoc, i);
                                    return i;
                                })
                                .addOnSuccessListener(image ->
                                        Log.d(TAG, String.format("upload: Document %s upload successful", imageId))
                                );
                    });
                });
    }

    /**
     * upload() helper function to update existing images
     * @param queryRes The query result to update
     * @param uploaderId The uploaderId of the existing images
     * @param uri The uri to update with
     */
    private void update(QuerySnapshot queryRes, String uploaderId, Uri uri) {

        DocumentReference doc = queryRes
                .getDocuments()
                .get(0)
                .getReference();

        String imageId = doc.getId();
        StorageReference imgRef = imgStorage.child(uploaderId).child(imageId);

        // update storage file
        imgRef
            .putFile(uri)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, String.format("update: File %s updated", imageId));

                    // update document
                    imgRef.getDownloadUrl().addOnSuccessListener(url -> {
                        // db store
                        doc.update("imageUrl", url)
                            .addOnSuccessListener(unused ->
                                    Log.d(TAG, String.format("update: Document %s updated", imageId))
                            );
                    });
                }
            });
    }

    /**
     * Download an image from Firestore db.
     * <br>
     * <b>Requires the ImageQuery callback to access the query data.</b>
     * @param i Image object to download to
     * @param callback <i>new ImageQuery()</i>
     * @see com.example.pickme.utils.ImageQuery
     */
    public void download(@NonNull Image i, @NonNull ImageQuery callback) {

        String uploaderId = i.getUploaderId();
        String imageAssociation = i.getImageAssociation();
        String imageType = i.getImageType().toString();

        Query query = imgCollection
                .where(Filter.and(
                        Filter.equalTo("imageType", imageType),
                        Filter.equalTo("uploaderId", uploaderId),
                        Filter.equalTo("imageAssociation", imageAssociation)));

        query.get()
                .addOnCompleteListener(querySnapshotTask -> {
                    if (querySnapshotTask.isSuccessful()) {
                        QuerySnapshot queryRes = querySnapshotTask.getResult();
                        if (!queryRes.isEmpty()) {
                            Map<String, Object> data = queryRes
                                    .getDocuments()
                                    .get(0)
                                    .getData();
                            assert data != null;
                            Image queriedImage = new Image(data);
                            callback.onSuccess(queriedImage);
                            Log.d(TAG, "download: Query successful, sent Image to callback");
                        } else {
                            Log.d(TAG, "download: Query returned empty");
                            callback.onEmpty();
                        }
                    }
                });
    }

    /**
     * Delete an image from Firestore db.
     * @param i Image object to be deleted
     */
    public void delete(@NonNull Image i) {

        String uploaderId = i.getUploaderId();
        String imageAssociation = i.getImageAssociation();
        String imageType = i.getImageType().toString();

        Query query = imgCollection
                .where(Filter.and(
                        Filter.equalTo("imageType", imageType),
                        Filter.equalTo("uploaderId", uploaderId),
                        Filter.equalTo("imageAssociation", imageAssociation)
                ));

        query.get()
                .addOnCompleteListener(querySnapshotTask -> {
                    if (querySnapshotTask.isSuccessful()) {
                        QuerySnapshot queryRes = querySnapshotTask.getResult();
                        if (!queryRes.isEmpty()) {
                            DocumentReference doc = queryRes
                                    .getDocuments()
                                    .get(0)
                                    .getReference();

                            String imageId = doc.getId();

                            Log.d(TAG, "delete: Query successful");
                            Log.d(TAG, String.format("delete: Deleting file %s", imageId));
                            imgStorage
                                    .child(uploaderId)
                                    .child(imageId)
                                    .delete()
                                    .addOnCompleteListener(deleteFileTask -> {
                                        if (deleteFileTask.isSuccessful()) {
                                            Log.d(TAG, String.format("delete: File %s deleted", imageId));
                                        } else {
                                            Log.d(TAG, String.format("delete: File %s deletion failed!", imageId));
                                        }
                                    });
                            Log.d(TAG, "DB: Deleting document " + imageId);
                            doc.delete()
                                    .addOnCompleteListener(deleteDocTask -> {
                                        if (deleteDocTask.isSuccessful()) {
                                            Log.d(TAG, String.format("delete: Document %s deleted", imageId));
                                        } else {
                                            Log.d(TAG, String.format("delete: Document %s deletion failed!", imageId));
                                        }
                                    });
                        } else {
                            Log.d(TAG, "delete: Query returned empty, no deletion occurred");
                        }
                    }
                });

    }

    /**
     * Gets every image URL stored in Firebase DB and puts it into the passed gridView object
     * @param context The activity in which the gallery resides
     * @param gallery The gridView you want to show the images
     */
    public void getAllImages(@NonNull Context context, @NonNull GridView gallery) {
        // querying images collection for everything
        imgCollection
                .get()
                .addOnCompleteListener(querySnapshotTask -> {
                    if (querySnapshotTask.isSuccessful()) {
                        Log.d(TAG, "DB: Query all successful");
                        QuerySnapshot queryRes = querySnapshotTask.getResult();
                        if (!queryRes.isEmpty()) {
                            // acquired list of image documents
                            List<DocumentSnapshot> docs = queryRes.getDocuments();

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
                    }
                });
    }
    //endregion

}
