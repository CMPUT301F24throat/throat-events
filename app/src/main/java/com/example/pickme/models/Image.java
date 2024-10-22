package com.example.pickme.models;

import java.time.LocalDateTime;

/**
 * Represents uploaded images (profile pictures, event posters)
 * Responsibilities:
 * Stores metadata about uploaded images in the images collection,
 * including the URL for Firebase Storage
 **/

public class Image {
    //region Attributes

    private String imageUrl;
    private imageType type;
    private String uploadedBy;
    private LocalDateTime dateCreated;

    public enum imageType {
        img_profile,
        img_event
    }
    //endregion

    //region Getters

    public String getImageUrl() {
        return imageUrl;
    }

    public imageType getType() {
        return type;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }
    //endregion

    //region Setters

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setType(imageType type) {
        this.type = type;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }
    //endregion
}

