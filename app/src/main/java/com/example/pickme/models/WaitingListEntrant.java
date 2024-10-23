package com.example.pickme.models;

import com.example.pickme.models.Enums.Status;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

/**
 * Represents an entrant on a waiting list
 * Responsibilities:
 * Models an entrant on a waiting list in the waiting list entrants collection
 **/

public class WaitingListEntrant {
    private String waitListEntrantId;
    private String entrantId;
    private Date joinedAt;
    private GeoPoint geoLocation; // Optional field
    private Status status;
    private boolean notified;
    private final Date createdAt;
    private Date updatedAt;

    public WaitingListEntrant() {
        this.createdAt = new Date();
    }

    public WaitingListEntrant(String entrantId, Date joinedAt, GeoPoint geoLocation, Status status) {
        this.entrantId = entrantId;
        this.joinedAt = joinedAt;
        this.geoLocation = geoLocation;
        this.status = status;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public void setEntrantId(String entrantId) {
        this.entrantId = entrantId;
        this.updatedAt = new Date();
    }

    public void setJoinedAt(Date joinedAt) {
        this.joinedAt = joinedAt;
        this.updatedAt = new Date();
    }

    public void setGeoLocation(GeoPoint geoLocation) {
        this.geoLocation = geoLocation;
        this.updatedAt = new Date();
    }

    public void setStatus(Status status) {
        this.status = status;
        this.updatedAt = new Date();
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
        this.updatedAt = new Date();
    }
}
