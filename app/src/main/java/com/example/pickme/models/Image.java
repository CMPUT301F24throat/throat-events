package com.example.pickme.models;

import com.example.pickme.models.Enums.ImageType;

import java.util.Date;

/**
 * Represents uploaded images (profile pictures, event posters)
 * Responsibilities:
 * Stores metadata about uploaded images in the images collection,
 * including the URL for Firebase Storage
 **/

public class Image {
    private String imageId;
    private String imageUrl;
    private ImageType type;
    private String imageAssociation;
    private String uploaderId;
    private final Date createdAt;
    private Date updatedAt;

    public Image() {
        this.createdAt = new Date();
    }

    public Image(String imageUrl, ImageType type, String imageAssociation, String uploaderId) {
        this.imageUrl = imageUrl;
        this.type = type;
        this.imageAssociation = imageAssociation;
        this.uploaderId = uploaderId;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        this.updatedAt = new Date();
    }

    public void setType(ImageType type) {
        this.type = type;
        this.updatedAt = new Date();
    }

    public void setImageAssociation(String imageAssociation) {
        this.imageAssociation = imageAssociation;
        this.updatedAt = new Date();
    }

    public void setUploaderId(String uploaderId) {
        this.uploaderId = uploaderId;
        this.updatedAt = new Date();
    }
}
