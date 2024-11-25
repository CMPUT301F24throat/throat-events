package com.example.pickme.models;

/**
 * Represents an event's waiting list
 * @author sophiecabungcal
 * @version 1.0
 */
public class WaitingList {

    private String eventId;
    private Integer maxEntrants;
    private Integer maxWinners;
    private Integer numEntrants;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Integer getMaxEntrants() {
        return maxEntrants;
    }

    public void setMaxEntrants(Integer maxEntrants) {
        this.maxEntrants = maxEntrants;
    }

    public Integer getMaxWinners() {
        return maxWinners;
    }

    public void setMaxWinners(Integer maxWinners) {
        this.maxWinners = maxWinners;
    }

    public Integer getNumEntrants() {
        return numEntrants;
    }

    public void setNumEntrants(Integer numEntrants) {
        this.numEntrants = numEntrants;
    }

}
