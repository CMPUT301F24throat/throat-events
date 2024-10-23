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
    private Date createdAt;

}
