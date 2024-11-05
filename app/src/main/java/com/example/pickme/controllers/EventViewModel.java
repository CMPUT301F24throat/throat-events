package com.example.pickme.controllers;

import com.example.pickme.models.Event;
import com.example.pickme.repositories.EventRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Manages event operations like fetching, adding, updating, and deleting events for the PickMe app.
 *
 * @author Ayub Ali
 * @version 1.0
 */

public class EventViewModel {
    private Event selectedEvent;
    private ArrayList<Event> events;
    private final EventRepository eventRepository;

    // Initialize repository and event list
    public EventViewModel() {
        this.eventRepository = new EventRepository();
        this.events = new ArrayList<>();
    }

    // ---------- Selected Event --------------------

    public Event getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(Event event) {
        this.selectedEvent = event;
    }

    // ---------- Events List --------------------

    public ArrayList<Event> getEvents() {
        return events;
    }

    // Fetch events from Firestore and update list
    public void fetchEvents(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        eventRepository.getEventsByOrganizerUserId(task -> {
            if (task.isSuccessful()) {
                events.clear();
                events.addAll(task.getResult().toObjects(Event.class));
            }
            onCompleteListener.onComplete(task);
        });
    }

    // Add a new event to Firestore and list
    public void addEvent(Event event, OnCompleteListener<Object> onCompleteListener) {
        eventRepository.addEvent(event, task -> {
            if (task.isSuccessful()) {
                events.add(event);
            }
            onCompleteListener.onComplete(task);
        });
    }

    // Delete event from Firestore and list
    public void deleteEvent(Event event, OnCompleteListener<Void> onCompleteListener) {
        eventRepository.deleteEvent(event.getEventId(), task -> {
            if (task.isSuccessful()) {
                events.remove(event);
            }
            onCompleteListener.onComplete(task);
        });
    }

    public boolean removeEvent(Event event) {
        return events.remove(event);
    }

    // Placeholder for event update logic
    public boolean updateEvent(Event event) {
        return true;
    }

    // ---------- Lottery Selection --------------------

    // Placeholder for random participant selection
    public Event selectRandomParticipant(Event event) {
        return null;
    }

    // Placeholder to check if event has open spots
    public boolean hasAvailableSpots(Event event) {
        return false;
    }
}

/**
 * Code Sources
 *
 * ChatGPT
 * - "How to structure ViewModel fetch operations."
 * - "Event handling using Firestore and ViewModel."
 *
 * Stack Overflow
 * - "Best practices for managing event lists in ViewModels."
 *
 * Firebase Documentation
 * - "Cloud Firestore: Document data structure and querying."
 */
