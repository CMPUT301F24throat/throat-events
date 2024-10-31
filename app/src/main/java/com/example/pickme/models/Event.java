package com.example.pickme.models;

import java.io.Serializable;

public class Event implements Serializable {
    private String eventId;             // Unique event ID
    private String organizerId;         // Organizer's user ID
    private String facilityId;          // Facility ID
    private String eventTitle;          // Title of the event
    private String eventDescription;    // Description of the event
    private String eventDate;           // Date and time of the event (as a String)
    private String promoQrCodeId;      // Promo QR code ID
    private String waitingListQrCodeId; // Waiting list QR code ID
    private String posterImageId;       // URL of the poster image
    private String eventLocation;       // Location of the event
    private String maxWinners;          // Maximum number of winners (String to match provided data)
    private boolean geoLocationRequired; // Indicates if geo location is required
    private Integer maxEntrants;        // Maximum number of entrants (Integer)
    private long createdAt;  // Timestamp for when the event was created
    private long updatedAt;         // Timestamp for when the event was last updated

    // Default constructor (required for Firestore)
    public Event() {
    }

    // Constructor with parameters
    public Event(String eventId, String organizerId, String facilityId, String eventTitle,
                 String eventDescription, String eventDate, String promoQrCodeId,
                 String waitingListQrCodeId, String posterImageId, String eventLocation,
                 String maxWinners, boolean geoLocationRequired, Integer maxEntrants,
                 long createdAt, long updatedAt) {
        this.eventId = eventId;
        this.organizerId = organizerId;
        this.facilityId = facilityId;
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.eventDate = eventDate;
        this.promoQrCodeId = promoQrCodeId;
        this.waitingListQrCodeId = waitingListQrCodeId;
        this.posterImageId = posterImageId;
        this.eventLocation = eventLocation;
        this.maxWinners = maxWinners;
        this.geoLocationRequired = geoLocationRequired;
        this.maxEntrants = maxEntrants;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getPromoQrCodeId() {
        return promoQrCodeId;
    }

    public void setPromoQrCodeId(String promoQrCodeId) {
        this.promoQrCodeId = promoQrCodeId;
    }

    public String getWaitingListQrCodeId() {
        return waitingListQrCodeId;
    }

    public void setWaitingListQrCodeId(String waitingListQrCodeId) {
        this.waitingListQrCodeId = waitingListQrCodeId;
    }

    public String getPosterImageId() {
        return posterImageId;
    }

    public void setPosterImageId(String posterImageId) {
        this.posterImageId = posterImageId;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getMaxWinners() {
        return maxWinners;
    }

    public void setMaxWinners(String maxWinners) {
        this.maxWinners = maxWinners;
    }

    public boolean isGeoLocationRequired() {
        return geoLocationRequired;
    }

    public void setGeoLocationRequired(boolean geoLocationRequired) {
        this.geoLocationRequired = geoLocationRequired;
    }

    public Integer getMaxEntrants() {
        return maxEntrants;
    }

    public void setMaxEntrants(Integer maxEntrants) {
        this.maxEntrants = maxEntrants;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
