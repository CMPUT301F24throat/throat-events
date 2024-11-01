package com.example.pickme.controllers;

import com.example.pickme.models.Event;
import java.util.ArrayList;

/**
 * Manages event creation and editing
 * Responsibilities:
 * - Create and update events
 * - Fetch event details
 * - Handle event operations (create, edit, remove)
 * - Manage waiting lists
 * - Handle lottery selection
 **/

public class EventViewModel {
    private Event selectedEvent;
    private ArrayList<Event> events;

    public EventViewModel() {
        // Constructor
    }

    // ---------- Selected Event --------------------
    public Event getSelectedEvent() {
        return null;
    }

    public void setSelectedEvent(Event event) {
        // Set selected event
    }

    // ---------- Events List --------------------
    public ArrayList<Event> getEvents() {
        return null;
    }

    public void addEvent(Event event) {
        // Add event
    }

    public boolean removeEvent(Event event) {
        return false;
    }

    // ---------- Event Management --------------------
    public boolean updateEvent(Event event) {
        return false;
    }

    // ---------- Lottery Selection --------------------
    public Event selectRandomParticipant(Event event) {
        return null;
    }

    public boolean hasAvailableSpots(Event event) {
        return false;
    }
}