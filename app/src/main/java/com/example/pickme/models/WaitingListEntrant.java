package com.example.pickme.models;

import com.example.pickme.models.Enums.Status;
import com.example.pickme.utils.TimestampUtil;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.time.LocalDateTime;

/**
 * Represents an entrant on a waiting list
 * Responsibilities:
 * Models an entrant on a waiting list in the waiting list entrants subcollection
 **/

public class WaitingListEntrant {
    private String waitListEntrantId;
    private String entrantId;
    private Timestamp joinedAt;
    private GeoPoint geoLocation; // Optional field
    private Status status;
    private boolean notified;
    private final Timestamp createdAt;
    private Timestamp updatedAt;

    public WaitingListEntrant() {
        this.createdAt = Timestamp.now();
    }

    public WaitingListEntrant(String entrantId, LocalDateTime joinedAt, GeoPoint geoLocation, Status status) {
        this.entrantId = entrantId;
        this.joinedAt = TimestampUtil.toTimestamp(joinedAt);
        this.geoLocation = geoLocation;
        this.status = status;
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
    }

    public String getWaitListEntrantId() {
        return waitListEntrantId;
    }

    public void setEntrantId(String entrantId) {
        this.entrantId = entrantId;
        this.updatedAt = Timestamp.now();
    }

    public String getEntrantId() {
        return entrantId;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = TimestampUtil.toTimestamp(joinedAt);
        this.updatedAt = Timestamp.now();
    }

    public Timestamp getJoinedAt() {
        return joinedAt;
    }

    public void setGeoLocation(GeoPoint geoLocation) {
        this.geoLocation = geoLocation;
        this.updatedAt = Timestamp.now();
    }

    public GeoPoint getGeoLocation() {
        return geoLocation;
    }

    public void setStatus(Status status) {
        this.status = status;
        this.updatedAt = Timestamp.now();
    }

    public Status getStatus() {
        return status;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
        this.updatedAt = Timestamp.now();
    }

    public boolean isNotified() {
        return notified;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
}
