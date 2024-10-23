package com.example.pickme.models;

import java.time.LocalDateTime;

/**
 * Represents an event created by an organizer
 * Responsibilities:
 * Models an event in the events collection
 * and contains references to the organizer and waiting list
 **/

public class Event {
    private String eventId;
    private String title;
    private String description;
    private LocalDateTime eventDate;
    private LocalDateTime registrationDeadline;
    private int capacity;
    private String location;
    private boolean geoLocationRequired;
    private String organizerId;
    private String waitingListStatus;  // OPEN, CLOSED

    public Event() {
        // Empty for Firestore
    }

    // ---------- EventId --------------------
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    // ---------- Title --------------------
    public String getTitle() {
        return title;
    }

    public boolean setTitle(String title) {
        if(title.isEmpty()) return false;
        this.title = title;
        return true;
    }

    // ---------- Description --------------------
    public String getDescription() {
        return description;
    }

    public boolean setDescription(String description) {
        if(description.isEmpty()) return false;
        this.description = description;
        return true;
    }

    // ---------- Event Date --------------------
    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    // ---------- Registration Deadline --------------------
    public LocalDateTime getRegistrationDeadline() {
        return registrationDeadline;
    }

    public void setRegistrationDeadline(LocalDateTime deadline) {
        this.registrationDeadline = deadline;
    }

    // ---------- Capacity --------------------
    public int getCapacity() {
        return capacity;
    }

    public boolean setCapacity(int capacity) {
        if(capacity <= 0) return false;
        this.capacity = capacity;
        return true;
    }

    // ---------- Location --------------------
    public String getLocation() {
        return location;
    }

    public boolean setLocation(String location) {
        if(location.isEmpty()) return false;
        this.location = location;
        return true;
    }

    // ---------- GeoLocation --------------------
    public boolean isGeoLocationRequired() {
        return geoLocationRequired;
    }

    public void setGeoLocationRequired(boolean geoLocationRequired) {
        this.geoLocationRequired = geoLocationRequired;
    }

    // ---------- OrganizerId --------------------
    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    // ---------- Waiting List Status --------------------
    public String getWaitingListStatus() {
        return waitingListStatus;
    }

    public void setWaitingListStatus(String status) {
        this.waitingListStatus = status;
    }
}