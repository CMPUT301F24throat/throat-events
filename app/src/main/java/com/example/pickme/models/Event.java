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
    private final Date createdAt;
    private Date updatedAt;

    public Event() {
        this.createdAt = new Date();
    }

    public Event(String eventId, String organizerId, String eventTitle, String eventDescription, String eventLocation, String eventDate, String qrCodeHash, String eventPosterUrl, boolean geoLocationRequired, Integer maxEntrants) {
        this.eventId = eventId;
        this.organizerId = organizerId;
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.eventLocation = eventLocation;
        this.eventDate = eventDate;
        this.qrCodeHash = qrCodeHash;
        this.eventPosterUrl = eventPosterUrl;
        this.geoLocationRequired = geoLocationRequired;
        this.maxEntrants = maxEntrants;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
        this.updatedAt = new Date();
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
        this.updatedAt = new Date();
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
        this.updatedAt = new Date();
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
        this.updatedAt = new Date();
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
        this.updatedAt = new Date();
    }

    public void setQrCodeHash(String qrCodeHash) {
        this.qrCodeHash = qrCodeHash;
        this.updatedAt = new Date();
    }

    public void setEventPosterUrl(String eventPosterUrl) {
        this.eventPosterUrl = eventPosterUrl;
        this.updatedAt = new Date();
    }

    public void setGeoLocationRequired(boolean geoLocationRequired) {
        this.geoLocationRequired = geoLocationRequired;
        this.updatedAt = new Date();
    }

    public void setMaxEntrants(Integer maxEntrants) {
        this.maxEntrants = maxEntrants;
        this.updatedAt = new Date();
    }
}
