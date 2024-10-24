package com.example.pickme.models;

import com.google.firebase.Timestamp;

/**
 * Represents a waiting list for an event
 * Responsibilities:
 * Models a waiting list in the waiting lists subcollection
 **/

public class WaitingList {
    private String waitingListId;
    private String eventId;
    private Integer maxEntrants; // Optional field
    private final Timestamp createdAt;
    private Timestamp updatedAt;

    public WaitingList() {
        this.createdAt = Timestamp.now();
    }

    public WaitingList(String eventId, Integer maxEntrants) {
        this.eventId = eventId;
        this.maxEntrants = maxEntrants;
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
    }

    public String getWaitingListId() {
        return waitingListId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
        this.updatedAt = Timestamp.now();
    }

    public String getEventId() {
        return eventId;
    }

    public void setMaxEntrants(Integer maxEntrants) {
        this.maxEntrants = maxEntrants;
        this.updatedAt = Timestamp.now();
    }

    public Integer getMaxEntrants() {
        return maxEntrants;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
}
