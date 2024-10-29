package com.example.pickme.repositories;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pickme.models.Image;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Handles interactions with the images collection
 * @author sophiecabungcal
 * @author etdong
 * @version 1.2
 * Responsibilities:
 * CRUD operations for image data
 */
public class ImageRepository {
    private final String TAG = "ImageRepository";

    // authentication
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final String auth_uid = auth.getUid();

    // image storage
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference imgStorage = storage.getReference();

    // database access
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference imgCollection = db.collection("images");

    public ImageRepository() {
        // temporary anonymous auth
        auth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.d(TAG, "AUTH: uid " + auth_uid + " successful authentication");
            }
        });
    }

    /**
     * Callback interface required for accessing asynchronous query data.
     * @Overload <b>onQuerySuccess(Image image)</b> to access the queried image
     * @Overload <b>onQueryEmpty()</b> to handle empty queries
     */
    public interface queryCallback {
        void onQuerySuccess(Image image);
        void onQueryEmpty();
    }

    /**
     * Callback interface specifically for accessing the entire collection.
     * @Overload <b>onQuerySuccess(List<DocumentSnapshot> docs)</b> to access the list of docs
     * @Overload <b>onQueryEmpty()</b> to handle empty queries
     */
    public interface collectionCallback {
        void onQuerySuccess(List<DocumentSnapshot> docs);
        void onQueryEmpty();
    }

    // TODO: fix nesting due to concurrency issues
     /**
     * Uploads an image with attached URI to FirebaseStorage,
     * then stores the image information to Firestore DB.
     *
     * @param i The image object to upload
     * @param uri The image URI to be attached
     */
    public void upload(@NotNull Image i, @NotNull Uri uri) {
        String uploaderId = i.getUploaderId();

        // creating document first to generate unique id
        DocumentReference imgDoc = imgCollection.document();
        String imageId = imgDoc.getId();

        // setting child storage reference to the unique id
        StorageReference imgRef = imgStorage.child(uploaderId).child(imageId);

        // storing the image in firebasestorage
        imgRef
                .putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "STORAGE: URI " + uri + " upload successful");

                    // now get the image download url...
                    imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // db store
                            i.setImageUrl(uri.toString());
                            db.runTransaction(new Transaction.Function<Image>() {
                                @Override
                                public Image apply(@NonNull Transaction transaction) {
                                    transaction.set(imgDoc, i);
                                    return i;
                                }
                            }).addOnSuccessListener(new OnSuccessListener<Image>() {
                                @Override
                                public void onSuccess(Image image) {
                                    Log.d(TAG, "DB: Image " + imgDoc.getId() + " upload successful");
                                }
                            });
                        }
                    });
                }
            });
    }

    /**
     * Download an image from Firestore db.
     * <br>
     * <b>Requires the callback included in ImageRepository to access the query data.</b>
     * @param i Image object to download to
     * @param callback <i>new ImageRepository.queryCallback()</i>
     * @see ImageRepository.queryCallback
     */
    public void download(Image i, queryCallback callback) {

        String uploaderId = i.getUploaderId();
        String imageAssociation = i.getImageAssociation();
        String imageType = i.getImageType().toString();

        Query query = imgCollection
                .where(Filter.and(
                        Filter.equalTo("imageType", imageType),
                        Filter.equalTo("uploaderId", uploaderId),
                        Filter.equalTo("imageAssociation", imageAssociation)));

        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot queryRes = task.getResult();
                            if (!queryRes.isEmpty()) {
                                Map<String, Object> data = queryRes
                                        .getDocuments()
                                        .get(0)
                                        .getData();
                                Image queriedImage = new Image(data);
                                callback.onQuerySuccess(queriedImage);
                                Log.d(TAG, "DB: Query successful, sent Image to callback");
                            } else {
                                Log.d(TAG, "DB: Query returned empty");
                                callback.onQueryEmpty();
                            }
                        }
                    }
                });
    }

    /**
     * Delete an image from Firestore db.
     * @param i Image object to be deleted
     */
    public void delete(Image i) {

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
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot queryRes = task.getResult();
                            if (!queryRes.isEmpty()) {
                                DocumentReference doc = queryRes
                                        .getDocuments()
                                        .get(0)
                                        .getReference();

                                String imageId = doc.getId();

                                Log.d(TAG, "DB: Query successful");
                                Log.d(TAG, "STORAGE: Deleting file " + imageId);
                                imgStorage
                                        .child(uploaderId)
                                        .child(imageId)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "STORAGE: File " + imageId + " deleted");
                                    }
                                });
                                Log.d(TAG, "DB: Deleting document " + imageId);
                                doc.delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d(TAG, "DB: Document " + imageId + " deleted");
                                            }
                                        });
                            } else {
                                Log.d(TAG, "DB: Query returned empty, no deletion occurred");
                            }
                        }
                    }
                });

    }

    public void getAllImages(collectionCallback callback) {
        imgCollection.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot queryRes = task.getResult();
                            if (!queryRes.isEmpty()) {
                                List<DocumentSnapshot> docs = queryRes.getDocuments();
                                callback.onQuerySuccess(docs);
                                Log.d(TAG, "DB: Query successful, sent to callback docs");
                            } else {
                                Log.d(TAG, "DB: Query returned empty");
                                callback.onQueryEmpty();
                            }
                        }
                    }
                });
    }

}
