package com.example.pickme.models;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.pickme.models.Enums.ImageType;
import com.example.pickme.repositories.ImageRepository;
import com.example.pickme.utils.ImageQuery;
import com.google.firebase.Timestamp;

import java.util.Map;

/**
 * Represents an image uploaded by the user
 * @author sophiecabungcal, etdong
 * @version 1.1
 */
public class Image {

    //region Attributes

    // the URL of the image in Firebase Storage [non-nullable]
    private String imageUrl;

    // the imageType of image (profile picture, event poster) [non-nullable]
    private ImageType imageType;

    // references the associated entity id that the image is for (user, event) [non-nullable]
    private String imageAssociation;

    // the user id of the uploader [non-nullable]
    private String uploaderId;

    // the date/time the image was created [non-nullable]
    private final Timestamp createdAt;

    // the date/time the image was last updated [non-nullable]
    private Timestamp updatedAt;

    // ImageRepository instance for Firebase interaction
    private ImageRepository ir;

    //endregion

    //region Constructors
    /**
     * Constructs an image with necessary parameters to upload, download, and delete.
     * @param userId The user ID of the current instance
     * @param imageAssociation The association ID, either the user ID for a profile picture
     *                         or an event ID for an event poster
     */
    public Image(@NonNull String userId, @NonNull String imageAssociation) {
        this.ir = new ImageRepository();
        this.uploaderId = userId;
        this.imageAssociation = imageAssociation;
        this.imageType = userId.equals(imageAssociation) ?
                ImageType.PROFILE_PICTURE :
                ImageType.EVENT_POSTER;
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
    }

    /**
     * Constructs an image from a hashmap of data (usually obtained from queries)
     * @param data The hashmap of data
     */
    public Image(Map<String, Object> data) {
        this.uploaderId = (String) data.get("uploaderId");
        this.imageType = ImageType.valueOf((String) data.get("imageType"));
        this.imageUrl = (String) data.get("imageUrl");
        this.imageAssociation = (String) data.get("imageAssociation");
        this.createdAt = (Timestamp) data.get("createdAt");
        this.updatedAt = (Timestamp) data.get("updatedAt");
    }
    //endregion

    //region Setters
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        this.updatedAt = Timestamp.now();
    }

    public void setImageType(ImageType imageType) {
        this.imageType = imageType;
        this.updatedAt = Timestamp.now();
    }

    public void setImageAssociation(String imageAssociation) {
        this.imageAssociation = imageAssociation;
        this.updatedAt = Timestamp.now();
    }

    public void setUploaderId(String uploaderId) {
        this.uploaderId = uploaderId;
        this.updatedAt = Timestamp.now();
    }

    //endregion

    //region Getters
    public String getImageUrl() {
        return imageUrl;
    }

    public ImageType getImageType() {
        return imageType;
    }

    public String getImageAssociation() {
        return imageAssociation;
    }

    public String getUploaderId() {
        return uploaderId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    //endregion

    //region Class methods

    /**
     * Uploads an image with attached URI to FirebaseStorage,
     * then stores the image information to Firestore DB.
     *
     * @param imageUri The image URI to be attached; obtained from gallery picker
     */
    public void upload(@NonNull Uri imageUri) {
        ir.upload(this, imageUri);
    }

    /**
     * Download the image from Firestore DB with query matching this image class.
     * <br>
     * <b>Requires the ImageQuery callback to access the query data.</b>
     * @param callback <i>new ImageQuery()</i>
     * @see com.example.pickme.utils.ImageQuery
     */
    public void download(@NonNull ImageQuery callback) {
        ir.download(this, callback);
    }

    /**
     * Delete the image from Firestore DB with query matching this image class.
     */
    public void delete() {
        ir.delete(this);
    }

    /**
     * Generates a random image from the uploader ID.
     */
    public void generate() {
        Uri url = Uri.parse(String.format("https://www.gravatar.com/avatar/%s?s=55&d=identicon&r=PG", this.uploaderId));
        ir.uploadUrl(this, url);
    }

    //endregion


}
