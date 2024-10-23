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
    private String entrantId;
    private Date joinedAt;
    private GeoPoint geoLocation; // Optional field
    private Status status;
    private boolean notified;
    private Date updatedAt;
    private Date createdAt;
}
