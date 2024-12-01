package com.example.pickme.models.Enums;

/**
 * Enum representing the status of an entrant in the system.
 */
public enum EntrantStatus {
    /**
     * This applies to all entrants, and is only used when organizer is sending a message
     */
    ALL,

    /**
     * User has joined the waiting list and is waiting for acceptance.
     */
    WAITING,

    /**
     * User was selected to attend the event - does not imply acceptance.
     */
    SELECTED,

    /**
     * User was selected to attend the event but rejected from accepting the invite.
     */
    REJECTED,

    /**
     * User's spot in the waiting list was cancelled.
     */
    CANCELLED,

    /**
     * User was selected to attend the event and accepted the invite.
     */
    ACCEPTED
}