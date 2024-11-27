package com.example.pickme.models;

/**
 * Represents an event's waiting list.
 * This class contains information about the waiting list, including the event ID, maximum entrants, maximum winners, and the current number of entrants.
 * It provides methods to get and set these attributes.
 *
 * @version 1.0
 */
public class WaitingList {

    // The unique ID of the event which the waiting list belongs to [non-nullable] - from the event object
    private String eventId;
    // The maximum number of entrants allowed on the waiting list [nullable]
    private Integer maxEntrants;
    // The maximum number of winners for the lottery [non-nullable] - from the event object
    private Integer maxWinners;
    // The number of entrants currently on the waiting list [non-nullable]
    private Integer numEntrants;

    /**
     * Gets the unique ID of the event which the waiting list belongs to.
     *
     * @return The event ID.
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Sets the unique ID of the event which the waiting list belongs to.
     *
     * @param eventId The event ID.
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Gets the maximum number of entrants allowed on the waiting list.
     *
     * @return The maximum number of entrants.
     */
    public Integer getMaxEntrants() {
        return maxEntrants;
    }

    /**
     * Sets the maximum number of entrants allowed on the waiting list.
     *
     * @param maxEntrants The maximum number of entrants.
     */
    public void setMaxEntrants(Integer maxEntrants) {
        this.maxEntrants = maxEntrants;
    }

    /**
     * Gets the maximum number of winners for the lottery.
     *
     * @return The maximum number of winners.
     */
    public Integer getMaxWinners() {
        return maxWinners;
    }

    /**
     * Sets the maximum number of winners for the lottery.
     *
     * @param maxWinners The maximum number of winners.
     */
    public void setMaxWinners(Integer maxWinners) {
        this.maxWinners = maxWinners;
    }

    /**
     * Gets the number of entrants currently on the waiting list.
     *
     * @return The number of entrants.
     */
    public Integer getNumEntrants() {
        return numEntrants;
    }

    /**
     * Sets the number of entrants currently on the waiting list.
     *
     * @param numEntrants The number of entrants.
     */
    public void setNumEntrants(Integer numEntrants) {
        this.numEntrants = numEntrants;
    }
}