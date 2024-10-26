package com.example.pickme.repositories;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pickme.models.Enums.ImageType;
import com.example.pickme.models.Event;
import com.example.pickme.models.Image;
import com.example.pickme.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

/**
 * Handles interactions with the images collection
 * @author sophiecabungcal
 * @author etdong
 * @version 1.1
 * Responsibilities:
 * CRUD operations for image data
 */
public class ImageRepository {
    private final String TAG = "ImageRepository";
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
                Log.d(TAG, "AUTH: Successful authentication");
            }
        });
    }

    // TODO: fix nesting due to concurrency issues
    /**
     * Uploads an event poster URI to FirebaseStorage,
     * then stores the image information to Firestore DB.
     *
     * @param u      The user object (uploader)
     * @param e      The event object (event poster)
     * @param imgUri The image URI to upload (e.g. one obtained from an image picker)
     */
    public void upload(@NotNull User u, Event e, @NotNull Uri imgUri) {
        // creating document first to generate unique id
        DocumentReference imgDoc = imgCollection.document();

        // setting child storage reference to the unique id
        StorageReference imgRef = imgStorage.child(imgDoc.getId());

        // storing the image in firebasestorage
        imgRef
            .putFile(imgUri)
            .addOnSuccessListener(taskSnapshot -> {
                Log.d(TAG, "STORAGE: URI " + imgUri + " upload successful");

                // now get the image download url...
                imgRef.getDownloadUrl().addOnSuccessListener(uri -> {

                    // ... and create a new image object to store to DB
                    Image img = new Image();
                    img.setImageUrl(uri.toString());
                    img.setUploaderId(u.getUserId());

                    // check for event object
                    if (e == null) {
                        // if null is passed, the image is a profile picture
                        img.setType(ImageType.PROFILE_PICTURE);
                        img.setImageAssociation(u.getUserId());
                    } else {
                        // and if there's an associated event, it's an event poster
                        img.setType(ImageType.EVENT_POSTER);
                        img.setImageAssociation(e.getEventId());
                    }


                    // db store
                    imgDoc
                        .set(img.getUploadPackage())
                        .addOnSuccessListener(unused ->
                                Log.d(TAG,"DB: Upload successful"));
                });
            });
    }

    /**
     * Uploads a profile image URI to FirebaseStorage,
     * then stores the image information to Firestore DB.
     *
     * @param u      The user object (uploader/subject)
     * @param imgUri The image URI to upload (e.g. one obtained from an image picker)
     */
    public void upload(@NotNull User u, @NotNull Uri imgUri) {
        upload(u, null, imgUri);
    }

    /**
     * Callback interface required for accessing asynchronous query data. <br>
     * <i>onQuerySuccess(String imageUrl)</i> to access the imageUrl <br>
     * <i>onQueryEmpty()</i> to handle empty queries
     */
    public interface queryCallback {
        void onQuerySuccess(String imageUrl);
        void onQueryEmpty();
    }

    /**
     * Download an event poster from Firestore db.
     * <br>
     * <b>Requires the callback included in ImageRepository to access the query data.</b>
     * @param u The user matching to uploaderId
     * @param e The event matching to imageAssociation
     * @param callback <i>new ImageRepository.queryCallback</i>
     */
    public void download(@NotNull User u, Event e, @NotNull queryCallback callback) {
        if (e == null) {
            Query profileImg = imgCollection
                    .where(Filter.and(
                            Filter.equalTo("imageType", "PROFILE_PICTURE"),
                            Filter.equalTo("uploaderId", u.getUserId()),
                            Filter.equalTo("imageAssociation", u.getUserId())));

            profileImg.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot queryRes = task.getResult();
                                if (!queryRes.isEmpty()) {
                                    String url = (String) queryRes.getDocuments().get(0).get("imageUrl");
                                    callback.onQuerySuccess(url);
                                    Log.d(TAG, "DB: Query successful, sent to callback imageUrl");
                                } else {
                                    Log.d(TAG, "DB: Query returned empty");
                                    callback.onQueryEmpty();
                                }
                            }
                        }
                    });
        } else {
            Query eventPoster = imgCollection
                    .where(Filter.and(
                            Filter.equalTo("imageType", "EVENT_POSTER"),
                            Filter.equalTo("uploaderId", u.getUserId()),
                            Filter.equalTo("imageAssociation", e.getEventId())));

            eventPoster
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot queryRes = task.getResult();
                                if (!queryRes.isEmpty()) {
                                    String url = (String) queryRes.getDocuments().get(0).get("imageUrl");
                                    callback.onQuerySuccess(url);
                                    Log.d(TAG, "DB: Query successful, sent to callback imageUrl");
                                } else {
                                    Log.d(TAG, "DB: Query returned empty");
                                    callback.onQueryEmpty();
                                }

                            }
                        }
                    });
        }
    }

    /**
     * Download a profile picture from Firestore db.
     * <br>
     * <b>Requires the callback included in ImageRepository to access the query data.</b>
     * @param u The user matching to uploaderId and imageAssociation
     * @param callback ImageRepository.queryCallback with onQuerySuccess overload
     *
     */
    public void download(@NotNull User u, @NotNull queryCallback callback) {
        download(u, null, callback);
    }

    public void delete() {
    }
}
