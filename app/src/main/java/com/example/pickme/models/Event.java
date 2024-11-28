package com.example.pickme.models;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

/**
 * Data model class representing an event in the system.
 * Stores all relevant details and constraints for each event, including title, description, date,
 * location, geolocation requirements, QR codes, and timestamps.
 *
 * @version 2.0
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
    private Date eventDate;              // Date of the event
    private String startTime;            // Start time of the event
    private String endTime;              // End time of the event
    private String promoQrCodeId;        // Promo QR code ID
    private String waitingListQrCodeId;  // Waiting list QR code ID
    private String posterImageId;        // URL of the poster image
    private String eventLocation;        // Location of the event
    private String maxWinners;           // Max number of winners
    private boolean geoLocationRequired; // Indicates if geolocation is required
    private Integer maxEntrants;         // Maximum number of entrants
    private ArrayList<WaitingListEntrant> waitingList = new ArrayList<>(); // Event waiting list; holds entrants on the waiting list
    private final Timestamp createdAt;   // Creation timestamp
    private Timestamp updatedAt;         // Last updated timestamp

    /**
     * Default constructor (required for Firestore)
     */
    public Event() {
        this.createdAt = Timestamp.now();
    }

    /**
     * Constructor with parameters
     *
     * @param eventId              Unique event ID
     * @param organizerId          Organizer's user ID
     * @param facilityId           Facility ID
     * @param eventTitle           Title of the event
     * @param eventDescription     Description of the event
     * @param eventDate            Date of the event
     * @param startTime            Start time of the event
     * @param endTime              End time of the event
     * @param promoQrCodeId        Promo QR code ID
     * @param waitingListQrCodeId  Waiting list QR code ID
     * @param posterImageId        URL of the poster image
     * @param eventLocation        Location of the event
     * @param maxWinners           Max number of winners
     * @param geoLocationRequired  Indicates if geolocation is required
     * @param maxEntrants          Maximum number of entrants
     * @param waitingList          Event waiting list; holds entrants on the waiting list
     */
    public Event(String eventId, String organizerId, String facilityId, String eventTitle,
                 String eventDescription, Date eventDate, String startTime, String endTime,
                 String promoQrCodeId, String waitingListQrCodeId, String posterImageId,
                 String eventLocation, String maxWinners, boolean geoLocationRequired,
                 Integer maxEntrants, ArrayList<WaitingListEntrant> waitingList) {
        this.eventId = eventId;
        this.organizerId = organizerId;
        this.facilityId = facilityId;
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.eventDate = eventDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.promoQrCodeId = promoQrCodeId;
        this.waitingListQrCodeId = waitingListQrCodeId;
        this.posterImageId = posterImageId;
        this.eventLocation = eventLocation;
        this.maxWinners = maxWinners;
        this.geoLocationRequired = geoLocationRequired;
        this.maxEntrants = maxEntrants;
        this.waitingList = waitingList;
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
    }

    // Getters and Setters

    /**
     * Gets the unique event ID.
     *
     * @return the event ID
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Sets the unique event ID.
     *
     * @param eventId the event ID to set
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Gets the organizer's user ID.
     *
     * @return the organizer ID
     */
    public String getOrganizerId() {
        return organizerId;
    }

    /**
     * Sets the organizer's user ID.
     *
     * @param organizerId the organizer ID to set
     */
    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    /**
     * Gets the facility ID.
     *
     * @return the facility ID
     */
    public String getFacilityId() {
        return facilityId;
    }

    /**
     * Sets the facility ID.
     *
     * @param facilityId the facility ID to set
     */
    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    /**
     * Gets the title of the event.
     *
     * @return the event title
     */
    public String getEventTitle() {
        return eventTitle;
    }

    /**
     * Sets the title of the event.
     *
     * @param eventTitle the event title to set
     */
    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    /**
     * Gets the description of the event.
     *
     * @return the event description
     */
    public String getEventDescription() {
        return eventDescription;
    }

    /**
     * Sets the description of the event.
     *
     * @param eventDescription the event description to set
     */
    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    /**
     * Gets the date of the event.
     *
     * @return the event date
     */
    public Date getEventDate() {
        return eventDate;
    }

    /**
     * Sets the date of the event.
     *
     * @param eventDate the event date to set
     */
    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
        this.updatedAt = Timestamp.now();
    }

    /**
     * Gets the start time of the event.
     *
     * @return the start time
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * Sets the start time of the event.
     *
     * @param startTime the start time to set
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
        this.updatedAt = Timestamp.now();
    }

    /**
     * Gets the end time of the event.
     *
     * @return the end time
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * Sets the end time of the event.
     *
     * @param endTime the end time to set
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
        this.updatedAt = Timestamp.now();
    }

    /**
     * Gets the promo QR code ID.
     *
     * @return the promo QR code ID
     */
    public String getPromoQrCodeId() {
        return promoQrCodeId;
    }

    /**
     * Sets the promo QR code ID.
     *
     * @param promoQrCodeId the promo QR code ID to set
     */
    public void setPromoQrCodeId(String promoQrCodeId) {
        this.promoQrCodeId = promoQrCodeId;
        this.updatedAt = Timestamp.now();
    }

    /**
     * Gets the waiting list QR code ID.
     *
     * @return the waiting list QR code ID
     */
    public String getWaitingListQrCodeId() {
        return waitingListQrCodeId;
    }

    /**
     * Sets the waiting list QR code ID.
     *
     * @param waitingListQrCodeId the waiting list QR code ID to set
     */
    public void setWaitingListQrCodeId(String waitingListQrCodeId) {
        this.waitingListQrCodeId = waitingListQrCodeId;
        this.updatedAt = Timestamp.now();
    }

    /**
     * Gets the URL of the poster image.
     *
     * @return the poster image ID
     */
    public String getPosterImageId() {
        return posterImageId;
    }

    /**
     * Sets the URL of the poster image.
     *
     * @param posterImageId the poster image ID to set
     */
    public void setPosterImageId(String posterImageId) {
        this.posterImageId = posterImageId;
        this.updatedAt = Timestamp.now();
    }

    /**
     * Gets the location of the event.
     *
     * @return the event location
     */
    public String getEventLocation() {
        return eventLocation;
    }

    /**
     * Sets the location of the event.
     *
     * @param eventLocation the event location to set
     */
    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
        this.updatedAt = Timestamp.now();
    }

    /**
     * Gets the max number of winners.
     *
     * @return the max winners
     */
    public String getMaxWinners() {
        return maxWinners;
    }

    /**
     * Sets the max number of winners.
     *
     * @param maxWinners the max winners to set
     */
    public void setMaxWinners(String maxWinners) {
        this.maxWinners = maxWinners;
        this.updatedAt = Timestamp.now();
    }

    /**
     * Checks if geolocation is required.
     *
     * @return true if geolocation is required, false otherwise
     */
    public boolean isGeoLocationRequired() {
        return geoLocationRequired;
    }

    /**
     * Sets the geolocation requirement.
     *
     * @param geoLocationRequired the geolocation requirement to set
     */
    public void setGeoLocationRequired(boolean geoLocationRequired) {
        this.geoLocationRequired = geoLocationRequired;
        this.updatedAt = Timestamp.now();
    }

    /**
     * Gets the maximum number of entrants.
     *
     * @return the max entrants
     */
    public Integer getMaxEntrants() {
        return maxEntrants;
    }

    /**
     * Sets the maximum number of entrants.
     *
     * @param maxEntrants the max entrants to set
     */
    public void setMaxEntrants(Integer maxEntrants) {
        this.maxEntrants = maxEntrants;
        this.updatedAt = Timestamp.now();
    }

    /**
     * Gets the event waiting list.
     *
     * @return the waiting list
     */
    public ArrayList<WaitingListEntrant> getWaitingList() {
        return waitingList;
    }

    /**
     * Sets the event waiting list.
     *
     * @param waitingList the waiting list to set
     */
    public void setWaitingList(ArrayList<WaitingListEntrant> waitingList) {
        this.waitingList = waitingList;
        this.updatedAt = Timestamp.now();
    }

    /**
     * Gets the creation timestamp.
     *
     * @return the creation timestamp
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Gets the last updated timestamp.
     *
     * @return the last updated timestamp
     */
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Checks if this event is equal to another object.
     *
     * @param obj the object to compare
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // Check for reference equality
        if (obj == null || getClass() != obj.getClass()) return false; // Check for null or same class
        Event event = (Event) obj; // Cast to Event
        return eventId.equals(event.eventId); // Compare based on eventId (or any unique field)
    }

    /**
     * Generates a hash code for this event.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(eventId); // Use eventId for hash code
    }
}

/*
  Code Sources
  <p>
  ChatGPT
  - "Explanation on handling event properties using classes in Java."
  - "Firestore documentation on serializable classes in Android."
  <p>
  Stack Overflow
  - "Java Serializable vs Parcelable for data classes."
  <p>
  Android Developers
  - "Best practices for defining data classes in Android."
 */