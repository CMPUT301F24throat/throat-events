package com.example.pickme.models;

import java.util.Date;

/**
 * Represents an event created by an organizer
 * Responsibilities:
 * Models an event in the events collection
 * and contains references to the organizer and waiting list
 **/

public class Event {
    private String eventId;
    private String organizerId;
    private String eventTitle;
    private String eventDescription;
    private String eventLocation;
    private String eventDate;
    private String qrCodeHash;
    private String eventPosterUrl;
    private boolean geoLocationRequired;
    private Integer maxEntrants; // Optional field
    private Date createdAt;
}
