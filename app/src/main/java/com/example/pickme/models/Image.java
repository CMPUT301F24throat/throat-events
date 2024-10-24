package com.example.pickme.models;

import com.example.pickme.models.Enums.ImageType;
import com.google.firebase.Timestamp;

/**
 * Represents uploaded images (profile pictures, event posters)
 * Responsibilities:
 * Models an image in the images collection
 * Stores metadata about uploaded images in the images collection, including the URL for Firebase Storage
 **/

public class Image {
    private String imageId;
    private String imageUrl;
    private ImageType type;
    private String imageAssociation;
    private String uploaderId;
    private final Timestamp createdAt;
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
