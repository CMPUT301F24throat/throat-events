package com.example.pickme.models;

import com.example.pickme.models.Enums.ImageType;
import com.google.firebase.Timestamp;

/**
 * Represents an image uploaded by the user
 * @author sophiecabungcal
 * @version 1.0
 */
public class Image {

    /* Image Attributes */
    // the unique image id; auto-generated by Firebase [non-nullable]
    private String imageId;
    // the URL of the image in Firebase Storage [non-nullable]
    private String imageUrl;
    // the type of image (profile picture, event poster) [non-nullable]
    private ImageType type;
    // references the associated entity id that the image is for (user, event) [non-nullable]
    private String imageAssociation;
    // the user id of the uploader [non-nullable]
    private String uploaderId;
    // the date/time the image was created [non-nullable]
    private final Timestamp createdAt;
    // the date/time the image was last updated [non-nullable]
    private Timestamp updatedAt;

    public Image() {
        this.createdAt = Timestamp.now();
    }

    public Image(String imageUrl, ImageType type, String imageAssociation, String uploaderId) {
        this.imageUrl = imageUrl;
        this.type = type;
        this.imageAssociation = imageAssociation;
        this.uploaderId = uploaderId;
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        this.updatedAt = Timestamp.now();
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setType(ImageType type) {
        this.type = type;
        this.updatedAt = Timestamp.now();
    }

    public ImageType getType() {
        return type;
    }

    public void setImageAssociation(String imageAssociation) {
        this.imageAssociation = imageAssociation;
        this.updatedAt = Timestamp.now();
    }

    public String getImageAssociation() {
        return imageAssociation;
    }

    public void setUploaderId(String uploaderId) {
        this.uploaderId = uploaderId;
        this.updatedAt = Timestamp.now();
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
}
