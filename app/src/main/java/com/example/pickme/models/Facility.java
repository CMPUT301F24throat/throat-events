package com.example.pickme.models;

import com.google.firebase.Timestamp;

public class Facility {
    private String facilityId;
    private String ownerId;
    private String facilityName;
    private final Timestamp createdAt;
    private Timestamp updatedAt;

    public Facility() {
        this.createdAt = Timestamp.now();
    }
}
