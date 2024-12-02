package com.example.pickme.models;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.pickme.models.Enums.ImageType;
import com.example.pickme.repositories.ImageRepository;
import com.example.pickme.utils.ImageQuery;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.Timestamp;

import java.io.ByteArrayOutputStream;
import java.util.Random;

/**
 * Represents an image uploaded by the user
 *
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

    private boolean generated;

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
        this.ir = ImageRepository.getInstance();
        this.uploaderId = userId;
        this.imageAssociation = imageAssociation;

        if (userId == null || imageAssociation == null) {
            // Handle null values - set default image type
            this.imageType = ImageType.EVENT_POSTER; // Default to EVENT_POSTER
        } else {
            // Only call equals() if neither userId nor imageAssociation is null
            this.imageType = userId.equals(imageAssociation) ? ImageType.PROFILE_PICTURE : ImageType.EVENT_POSTER;
        }

        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
        this.generated = false;
    }


    public Image(@NonNull String userId, @NonNull String imageAssociation, ImageRepository ir) {
        this.ir = ir;
        this.uploaderId = userId;
        this.imageAssociation = imageAssociation;
        this.imageType = userId.equals(imageAssociation) ?
                ImageType.PROFILE_PICTURE :
                ImageType.EVENT_POSTER;
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
        this.generated = false;
    }

    /**
     * Firebase .toObject constructor
     */
    private Image() {
        this.createdAt = Timestamp.now();
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

    public boolean isGenerated() {
        return generated;
    }

    //endregion

    //region Class methods

    /**
     * Uploads an image with attached URI to FirebaseStorage,
     * then stores the image information to Firestore DB.
     *
     * @param imageUri The image URI to be attached; obtained from gallery picker
     */
    public void upload(@NonNull Uri imageUri, OnCompleteListener<Image> listener) {
        ir.upload(this, imageUri, listener);
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
    public void delete(OnCompleteListener<Image> listener) {
        ir.delete(this, listener);
    }

    /**
     * Generates a random image from the uploader ID.
     */
    public void generate(String initials, OnCompleteListener<Image> listener) {
        this.generated = true;
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(128), rnd.nextInt(128), rnd.nextInt(128));
        Bitmap b=Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        Paint p = new Paint();
        c.drawColor(color);
        p.setTextAlign(Paint.Align.CENTER);
        p.setColor(Color.parseColor("white"));
        p.setTextSize(128);
        c.drawText(
                initials,
                (float) c.getWidth() / 2,
                (((float) c.getHeight() / 2) - ((p.descent() + p.ascent()) / 2)),
                p
        );

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        byte[] data = bytes.toByteArray();

        ir.upload(this, data, listener);
    }

    //endregion


}