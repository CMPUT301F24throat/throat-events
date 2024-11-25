package com.example.pickme.models.Enums;

public enum EntrantStatus {
    // User has joined the waiting list and is waiting for acceptance
    WAITING,

    // User was selected to attend the event - does not imply acceptance
    SELECTED,

    // User was selected to attend the event but REJECTED from accepting the invite
    REJECTED,

    // User's spot in the waiting list was cancelled
    CANCELLED,

    // User was selected to attend the event and ACCEPTED the invite
    ACCEPTED
}
