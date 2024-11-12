package com.example.pickme.controllers;

import com.example.pickme.models.Event;
import com.example.pickme.repositories.EventRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Manages the logic and state for event-related operations.
 * This ViewModel serves as an intermediary between the UI and the EventRepository,
 * facilitating CRUD operations, event selection, and other event-related interactions.
 *
 * @version 2.0
 * @author Ayub Ali
 * Responsibilities:
 * - Handle CRUD operations for events by interfacing with EventRepository.
 * - Maintain and provide access to a selected event.
 * - Maintain a list of events for the organizer.
 * - Manage lottery selection and available spot checks for events.
 * - Handle Firestore completion notifications for event operations.
 */

public class EventViewModel {
    private Event selectedEvent;
    private ArrayList<Event> events;
    private final EventRepository eventRepository;

    public EventViewModel() {
        this.eventRepository = new EventRepository();
        this.events = new ArrayList<>(); // Initialize the event list
    }

    public EventViewModel(EventRepository eventRepository) {
        this.eventRepository = eventRepository; // Use the injected repository
        this.events = new ArrayList<>(); // Initialize the event list
    }


    // ---------- Selected Event --------------------
    public Event getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(Event event) {
        this.selectedEvent = event; // Set selected event
    }

    // ---------- Events List --------------------
    public ArrayList<Event> getEvents() {
        return events; // Return the list of events
    }

    // Fetch events from Firestore
    public void fetchEvents(String userId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        eventRepository.getEventsByOrganizerId(userId, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    events.clear(); // Clear existing events
                    events.addAll(task.getResult().toObjects(Event.class)); // Add fetched events to the list
                    onCompleteListener.onComplete(task); // Notify completion
                } else {
                    onCompleteListener.onComplete(task); // Notify failure
                }
            }
        });
    }

    // Add event to Firestore and the list
    public void addEvent(Event event, OnCompleteListener<Object> onCompleteListener) {
        eventRepository.addEvent(event, new OnCompleteListener<Object>() {
            @Override
            public void onComplete(Task<Object> task) {
                if (task.isSuccessful()) {
                    events.add(event); // Add the event to the local list
                    onCompleteListener.onComplete(task); // Notify completion
                } else {
                    onCompleteListener.onComplete(task); // Notify failure
                }
            }
        });
    }

    public void updateEvent(Event event, OnCompleteListener<Object> onCompleteListener) {
        eventRepository.updateEvent(event, new OnCompleteListener<Object>() {
            @Override
            public void onComplete(Task<Object> task) {
                if (task.isSuccessful()) {
                    for (int i = 0; i < events.size(); i++) {
                        if (events.get(i).getEventId().equals(event.getEventId())) {
                            events.set(i, event); // Update the event in the local list
                            break;
                        }
                    }
                    onCompleteListener.onComplete(task); // Notify completion
                } else {
                    onCompleteListener.onComplete(task); // Notify failure
                }
            }
        });
    }

    public void deleteEvent(Event event, OnCompleteListener<Void> onCompleteListener) {
        eventRepository.deleteEvent(event.getEventId(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    events.remove(event); // Remove event from local list
                }
                onCompleteListener.onComplete(task); // Notify completion (success or failure)
            }
        });
    }

    // ---------- Lottery Selection --------------------
    public Event selectRandomParticipant(Event event) {
        return null;
    }

    public boolean hasAvailableSpots(Event event) {
        return false;
    }
}

/**
 * Code Sources
 *
 * Firebase Documentation:
 * - Firestore Data Model- Details on how to structure data in Firestore.
 * - Transaction Management in Firestore - Best practices for handling transaction-based operations.
 *
 * Android Developers:
 * - Using ViewModel for Managing UI-related Data - Best practices for using ViewModel in Android applications.
 *
 * Stack Overflow:
 * - Passing data between ViewModel and Repository layers in Android.
 * - How to manage asynchronous data with OnCompleteListener in Firestore.
 */