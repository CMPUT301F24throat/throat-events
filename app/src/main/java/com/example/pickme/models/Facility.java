package com.example.pickme.models;

import com.google.firebase.Timestamp;

/**
 * Represents a facility created by an organizer.
 * This class contains information about the facility such as its ID, owner ID, name, location,
 * creation time, and last updated time.
 *
 * <p>Each facility is uniquely identified by a facility ID and is associated with an owner ID
 * referencing the user ID from the users collection of the facility owner.</p>
 *
 * <p>The creation time is set when the facility is instantiated and cannot be changed.
 * The last updated time is updated whenever any of the facility's attributes are modified.</p>
 *
 * <p>Note: The facility ID is auto-generated by Firebase and is non-nullable.</p>
 *
 * @version 1.0
 */
public class Facility {

    /* Facility Attributes */
    // The unique facility ID; auto-generated by Firebase [non-nullable]
    private String facilityId;
    // The owner ID referencing user ID from users collection of facility owner [non-nullable]
    private String ownerId;
    // The name of the facility [non-nullable]
    private String facilityName;
    // The location of the facility [nullable]
    private String location;
    // The date/time the facility was created [non-nullable]
    private final Timestamp createdAt;
    // The date/time the facility was last updated [non-nullable]
    private Timestamp updatedAt;

    /**
     * Default constructor that initializes the creation time to the current time.
     */
    public Facility() {
        this.createdAt = Timestamp.now();
    }

    /**
     * Constructor that initializes the facility with the specified owner ID, facility name, and location.
     * The creation and last updated times are set to the current time.
     *
     * @param ownerId the owner ID of the facility
     * @param facilityName the name of the facility
     * @param location the location of the facility
     */
    public Facility(String ownerId, String facilityName, String location) {
        this.ownerId = ownerId;
        this.facilityName = facilityName;
        this.location = location;
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
    }

    /**
     * Sets the facility ID.
     *
     * @param facilityId the unique facility ID
     */
    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    /**
     * Returns the facility ID.
     *
     * @return the facility ID
     */
    public String getFacilityId() {
        return facilityId;
    }

    /**
     * Sets the owner ID and updates the last updated time to the current time.
     *
     * @param ownerId the owner ID of the facility
     */
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
        this.updatedAt = Timestamp.now();
    }

    /**
     * Returns the owner ID.
     *
     * @return the owner ID
     */
    public String getOwnerId() {
        return ownerId;
    }

    /**
     * Sets the facility name and updates the last updated time to the current time.
     *
     * @param facilityName the name of the facility
     */
    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
        this.updatedAt = Timestamp.now();
    }

    /**
     * Returns the facility name.
     *
     * @return the name of the facility
     */
    public String getFacilityName() {
        return facilityName;
    }

    /**
     * Sets the location of the facility and updates the last updated time to the current time.
     *
     * @param location the location of the facility
     */
    public void setLocation(String location) {
        this.location = location;
        this.updatedAt = Timestamp.now();
    }

    /**
     * Returns the location of the facility.
     *
     * @return the location of the facility
     */
    public String getLocation() {
        return location;
    }

    /**
     * Returns the creation time of the facility.
     *
     * @return the creation time of the facility
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Returns the last updated time of the facility.
     *
     * @return the last updated time of the facility
     */
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}