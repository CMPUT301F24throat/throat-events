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
    private String facilityId;
    private String eventTitle;
    private String eventDescription;
    private Timestamp eventDate;
    private String promoQrCodeId;
    private String waitingListQrCodeId;
    private String posterImageId;
    private boolean geoLocationRequired;
    private Integer maxEntrants; // Optional field
    private final Timestamp createdAt;
    private Timestamp updatedAt;

    public Event() {
        this.createdAt = Timestamp.now();
    }

    public Event(String organizerId, String facilityId, String eventTitle, String eventDescription, String eventLocation, LocalDateTime eventDate, String promoQrCodeId, String waitingListQrCodeId, String posterImageId, boolean geoLocationRequired, Integer maxEntrants) {
        this.organizerId = organizerId;
        this.facilityId = facilityId;
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.eventDate = TimestampUtil.toTimestamp(eventDate);
        this.promoQrCodeId = promoQrCodeId;
        this.waitingListQrCodeId = waitingListQrCodeId;
        this.posterImageId = posterImageId;
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

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
        this.updatedAt = Timestamp.now();
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = TimestampUtil.toTimestamp(eventDate);
        this.updatedAt = Timestamp.now();
    }

    public Timestamp getEventDate() {
        return eventDate;
    }

    public void setPromoQrCodeId(String promoQrCodeId) {
        this.promoQrCodeId = promoQrCodeId;
        this.updatedAt = Timestamp.now();
    }

    public String getPromoQrCodeId() {
        return promoQrCodeId;
    }

    public void setWaitingListQrCodeId(String waitingListQrCodeId) {
        this.waitingListQrCodeId = waitingListQrCodeId;
        this.updatedAt = Timestamp.now();
    }

    public String getWaitingListQrCodeId() {
        return waitingListQrCodeId;
    }

    public void setPosterImageId(String posterImageId) {
        this.posterImageId = posterImageId;
        this.updatedAt = Timestamp.now();
    }

    public String getPosterImageId() {
        return posterImageId;
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
