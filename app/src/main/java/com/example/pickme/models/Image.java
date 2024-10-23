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
    private Date createdAt;
}
