package com.example.pickme.models;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private String eventDate;            // Date and time of the event (as a String)
    private String posterImageId;        // URL of the poster image
    private String eventLocation;        // Location of the event
    private Integer maxWinners;           // Max number of winners
    private boolean geoLocationRequired; // Indicates if geolocation is required
    private Integer maxEntrants;         // Maximum number of entrants
    private ArrayList<WaitingListEntrant> waitingList; // Event waiting list; a list of waiting list entrants
    private Boolean hasLotteryExecuted;  // Flag to indicate if the lottery has been executed
    private final Timestamp createdAt;              // Creation timestamp
    private Timestamp updatedAt;              // Last updated timestamp

    /**
     * Default constructor (required for Firestore)
     */
    public Event() {
        this.createdAt = Timestamp.now();
    }

    /**
     * Constructor with parameters
     *
     * @param eventId Unique event ID
     * @param organizerId Organizer's user ID
     * @param facilityId Facility ID
     * @param eventTitle Title of the event
     * @param eventDescription Description of the event
     * @param eventDate Date and time of the event (as a String)
     * @param posterImageId URL of the poster image
     * @param eventLocation Location of the event
     * @param maxWinners Max number of winners
     * @param geoLocationRequired Indicates if geolocation is required
     * @param maxEntrants Maximum number of entrants
     * @param waitingList Event waiting list; a list of waiting list entrants
     */
    public Event(String eventId, String organizerId, String facilityId, String eventTitle,
                 String eventDescription, String eventDate, String posterImageId,
                 String eventLocation, Integer maxWinners, boolean geoLocationRequired, Integer maxEntrants, ArrayList<WaitingListEntrant> waitingList, Boolean hasLotteryExecuted) {
        this.eventId = eventId;
        this.organizerId = organizerId;
        this.facilityId = facilityId;
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.eventDate = eventDate;
        this.posterImageId = posterImageId;
        this.eventLocation = eventLocation;
        this.maxWinners = maxWinners;
        this.geoLocationRequired = geoLocationRequired;
        this.maxEntrants = maxEntrants;
        this.waitingList = waitingList;
        this.hasLotteryExecuted = hasLotteryExecuted;
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
    }

    // Getters and Setters

    /**
     * Gets the unique event ID.
     *
     * @return eventId
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Sets the unique event ID.
     *
     * @param eventId Unique event ID
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
        this.updatedAt = Timestamp.now();
    }

    /**
     * Gets the organizer's user ID.
     *
     * @return organizerId
     */
    public String getOrganizerId() {
        return organizerId;
    }

    /**
     * Sets the organizer's user ID.
     *
     * @param organizerId Organizer's user ID
     */
    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
        this.updatedAt = Timestamp.now();
    }

    /**
     * Gets the facility ID.
     *
     * @return facilityId
     */
    public String getFacilityId() {
        return facilityId;
    }

    /**
     * Sets the facility ID.
     *
     * @param facilityId Facility ID
     */
    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
        this.updatedAt = Timestamp.now();
    }

    /**
     * Gets the title of the event.
     *
     * @return eventTitle
     */
    public String getEventTitle() {
        return eventTitle;
    }

    /**
     * Sets the title of the event.
     *
     * @param eventTitle Title of the event
     */
    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
        this.updatedAt = Timestamp.now();
    }

    /**
     * Gets the description of the event.
     *
     * @return eventDescription
     */
    public String getEventDescription() {
        return eventDescription;
    }

    /**
     * Sets the description of the event.
     *
     * @param eventDescription Description of the event
     */
    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
        this.updatedAt = Timestamp.now();
    }

    /**
     * Gets the date and time of the event.
     *
     * @return eventDate
     */
    public String getEventDate() {
        return eventDate;
    }

    /**
     * Sets the date and time of the event.
     *
     * @param eventDate Date and time of the event (as a String)
     * @throws IllegalArgumentException if the date format is invalid
     */
    public void setEventDate(@NonNull String eventDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d yyyy, h:mm a");
        dateFormat.setLenient(false); // Make strict parsing

        try {
            dateFormat.parse(eventDate); // Attempt to parse the date
            this.updatedAt = Timestamp.now();
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use 'MMMM d yyyy, h:mm a'.");
        }

        this.eventDate = eventDate; // Assuming eventDate is a String type
    }

    /**
     * Gets the URL of the poster image.
     *
     * @return posterImageId
     */
    public String getPosterImageId() {
        return posterImageId;
    }

    /**
     * Sets the URL of the poster image.
     *
     * @param posterImageId URL of the poster image
     */
    public void setPosterImageId(String posterImageId) {
        this.posterImageId = posterImageId;
        this.updatedAt = Timestamp.now();
    }

    /**
     * Gets the location of the event.
     *
     * @return eventLocation
     */
    public String getEventLocation() {
        return eventLocation;
    }

    /**
     * Sets the location of the event.
     *
     * @param eventLocation Location of the event
     */
    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
        this.updatedAt = Timestamp.now();
    }

    /**
     * Gets the max number of winners.
     *
     * @return maxWinners
     */
    public Integer getMaxWinners() {
        return maxWinners;
    }

    /**
     * Sets the max number of winners.
     *
     * @param maxWinners Max number of winners
     * @throws IllegalArgumentException if maxWinners is not a non-negative numeric value
     */
    public void setMaxWinners(@NonNull Integer maxWinners) {
        if (maxWinners < 0) {  // Check if not numeric
            throw new IllegalArgumentException("Max winners must be a non-negative numeric value.");
        }

        this.maxWinners = maxWinners;  // Assuming maxWinners is a String type
        this.updatedAt = Timestamp.now();
    }

    /**
     * Checks if geolocation is required.
     *
     * @return geoLocationRequired
     */
    public boolean isGeoLocationRequired() {
        return geoLocationRequired;
    }

    /**
     * Sets if geolocation is required.
     *
     * @param geoLocationRequired Indicates if geolocation is required
     */
    public void setGeoLocationRequired(boolean geoLocationRequired) {
        this.geoLocationRequired = geoLocationRequired;
        this.updatedAt = Timestamp.now();
    }

    /**
     * Gets the maximum number of entrants.
     *
     * @return maxEntrants
     */
    public Integer getMaxEntrants() {
        return maxEntrants;
    }

    /**
     * Sets the maximum number of entrants.
     *
     * @param maxEntrants Maximum number of entrants
     * @throws IllegalArgumentException if maxEntrants is not a positive value
     */
    public void setMaxEntrants(@NonNull Integer maxEntrants) {
        if (maxEntrants <= 0) {  // Check for negative and zero values
            throw new IllegalArgumentException("Max entrants must be a positive value.");
        }

        this.maxEntrants = maxEntrants;  // Assuming maxEntrants is an Integer
    }

    /**
     * Gets the waiting list for the event.
     *
     * @return waitingList
     */
    public ArrayList<WaitingListEntrant> getWaitingList() {
        return waitingList;
    }

    /**
     * Sets the waiting list for the event.
     *
     * @param waitingList Event waiting list; a list of waiting list entrants
     */
    public void setWaitingList(ArrayList<WaitingListEntrant> waitingList) {
        this.waitingList = waitingList;
        this.updatedAt = Timestamp.now();
    }

    /**
     * Sets if the lottery has been executed.
     *
     * @param hasLotteryExecuted Indicates if the lottery has been executed
     */
    public void setHasLotteryExecuted(boolean hasLotteryExecuted) {
        this.hasLotteryExecuted = hasLotteryExecuted;
        this.updatedAt = Timestamp.now();
    }

    /**
     * Checks if the lottery has been executed.
     *
     * @return hasLotteryExecuted
     */
    public boolean hasLotteryExecuted() {
        return hasLotteryExecuted;
    }

    /**
     * Gets the creation timestamp.
     *
     * @return createdAt
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Gets the last updated timestamp.
     *
     * @return updatedAt
     */
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Checks if this event is equal to another object.
     *
     * @param obj The object to compare with
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
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(eventId); // Use eventId for hash code
    }

    /**
     * Checks if the event date has passed.
     *
     * @return true if the event date has passed, false otherwise
     */
    public boolean hasEventPassed() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d yyyy, h:mm a");
        try {
            Date eventDateParsed = dateFormat.parse(eventDate.split(" - ")[0]);
            Date currentDate = new Date();
            if (eventDateParsed != null) {
                return eventDateParsed.before(currentDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        return false;
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