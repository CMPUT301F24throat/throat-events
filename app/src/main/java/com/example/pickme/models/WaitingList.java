package com.example.pickme.models;

import java.util.Date;

/**
 * Represents a waiting list for an event
 * Responsibilities:
 * Models a waiting list in the waiting lists collection
 **/

public class WaitingList {
    private String waitingListId;
    private String eventId;
    private Integer maxEntrants; // Optional field
    private final Date createdAt;
    private Date updatedAt;

    public WaitingList() {
        this.createdAt = new Date();
    }

    public WaitingList(String eventId, Integer maxEntrants) {
        this.eventId = eventId;
        this.maxEntrants = maxEntrants;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
        this.updatedAt = new Date();
    }

    public void setMaxEntrants(Integer maxEntrants) {
        this.maxEntrants = maxEntrants;
        this.updatedAt = new Date();
    }
}
