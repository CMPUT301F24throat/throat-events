package com.example.pickme.models.Enums;

/**
 * Enum representing the status of the waiting list in the system.
 */
public enum WaitingListStatus {
    /**
     * Waiting list is open for entrants to join.
     */
    OPEN,

    /**
     * Waiting list is closed and no longer accepting entrants.
     */
    CLOSED,

    /**
     * Waiting list is full and no longer accepting entrants.
     */
    FULL
}