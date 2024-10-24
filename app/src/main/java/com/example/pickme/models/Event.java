package com.example.pickme.models;

import com.example.pickme.utils.TimestampUtil;
import com.google.firebase.Timestamp;

import java.time.LocalDateTime;

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
    private Timestamp eventDate;
    private String qrCodeHash;
    private String eventPosterUrl;
    private boolean geoLocationRequired;
    private Integer maxEntrants; // Optional field
    private final Timestamp createdAt;
    private Timestamp updatedAt;

    public Event() {
        this.createdAt = Timestamp.now();
    }

    public Event(String organizerId, String eventTitle, String eventDescription, String eventLocation, LocalDateTime eventDate, String qrCodeHash, String eventPosterUrl, boolean geoLocationRequired, Integer maxEntrants) {
        this.organizerId = organizerId;
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.eventLocation = eventLocation;
        this.eventDate = TimestampUtil.toTimestamp(eventDate);
        this.qrCodeHash = qrCodeHash;
        this.eventPosterUrl = eventPosterUrl;
        this.geoLocationRequired = geoLocationRequired;
        this.maxEntrants = maxEntrants;
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
    }

    public String getEventId() {
        return eventId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
        this.updatedAt = Timestamp.now();
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
        this.updatedAt = Timestamp.now();
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
        this.updatedAt = Timestamp.now();
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
        this.updatedAt = Timestamp.now();
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = TimestampUtil.toTimestamp(eventDate);
        this.updatedAt = Timestamp.now();
    }

    public Timestamp getEventDate() {
        return eventDate;
    }

    public void setQrCodeHash(String qrCodeHash) {
        this.qrCodeHash = qrCodeHash;
        this.updatedAt = Timestamp.now();
    }

    public String getQrCodeHash() {
        return qrCodeHash;
    }

    public void setEventPosterUrl(String eventPosterUrl) {
        this.eventPosterUrl = eventPosterUrl;
        this.updatedAt = Timestamp.now();
    }

    public String getEventPosterUrl() {
        return eventPosterUrl;
    }

    public void setGeoLocationRequired(boolean geoLocationRequired) {
        this.geoLocationRequired = geoLocationRequired;
        this.updatedAt = Timestamp.now();
    }

    public boolean isGeoLocationRequired() {
        return geoLocationRequired;
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
