package com.example.pickme.models;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * Data model class representing an event in the system.
 * Stores all relevant details and constraints for each event, including title, description, date,
 * location, geolocation requirements, QR codes, and timestamps.
 *
 * @version 2.0
 * @author Ayub Ali
 * Responsibilities:
 * - Define the properties and structure for event-related data.
 * - Validate key fields such as max entrants and event date format.
 * - Manage event identity and equality for list operations and comparisons.
 */

public class Event implements Serializable {

    private String eventId;              // Unique event ID
    private String organizerId;          // Organizer's user ID
    private String facilityId;           // Facility ID
    private String eventTitle;           // Title of the event
    private String eventDescription;     // Description of the event
    private String eventDate;            // Date and time of the event (as a String)
    private String promoQrCodeId;        // Promo QR code ID
    private String waitingListQrCodeId;  // Waiting list QR code ID
    private String posterImageId;        // URL of the poster image
    private String eventLocation;        // Location of the event
    private String maxWinners;           // Max number of winners
    private boolean geoLocationRequired; // Indicates if geolocation is required
    private Integer maxEntrants;         // Maximum number of entrants
    private Integer entrants;         // Number of entrants registered
    private long createdAt;              // Creation timestamp
    private long updatedAt;              // Last updated timestamp

    // Default constructor (required for Firestore)
    public Event() {
    }

    // Constructor with parameters
    public Event(String eventId, String organizerId, String facilityId, String eventTitle,
                 String eventDescription, String eventDate, String promoQrCodeId,
                 String waitingListQrCodeId, String posterImageId, String eventLocation,
                 String maxWinners, boolean geoLocationRequired, Integer maxEntrants,
                 Integer entrants,
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
        this.entrants = entrants;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getEventId() {return eventId;}

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getEntrants() {
        return entrants;
    }

    public void setEntrants(Integer entrants) {
        this.entrants = entrants;
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
        if (eventDate == null) {
            throw new IllegalArgumentException("Event date cannot be null.");
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d yyyy, h:mm a");
        dateFormat.setLenient(false); // Make strict parsing

        try {
            dateFormat.parse(eventDate); // Attempt to parse the date
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use 'MMMM d yyyy, h:mm a'.");
        }

        this.eventDate = eventDate; // Assuming eventDate is a String type
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
        if (maxWinners == null || !maxWinners.matches("\\d+")) {  // Check if not numeric
            throw new IllegalArgumentException("Max winners must be a non-negative numeric value.");
        }

        this.maxWinners = maxWinners;  // Assuming maxWinners is a String type
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
        if (maxEntrants == null) {
            throw new IllegalArgumentException("Max entrants cannot be null.");
        }
        if (maxEntrants <= 0) {  // Check for negative and zero values
            throw new IllegalArgumentException("Max entrants must be a positive value.");
        }

        this.maxEntrants = maxEntrants;  // Assuming maxEntrants is an Integer
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // Check for reference equality
        if (obj == null || getClass() != obj.getClass()) return false; // Check for null or same class
        Event event = (Event) obj; // Cast to Event
        return eventId.equals(event.eventId); // Compare based on eventId (or any unique field)
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId); // Use eventId for hash code
    }

}

/**
 * Code Sources
 *
 * ChatGPT
 * - "Explanation on handling event properties using classes in Java."
 * - "Firestore documentation on serializable classes in Android."
 *
 * Stack Overflow
 * - "Java Serializable vs Parcelable for data classes."
 *
 * Android Developers
 * - "Best practices for defining data classes in Android."
 */