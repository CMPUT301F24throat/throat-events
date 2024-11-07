package com.example.pickme.controllers;

import com.example.pickme.models.Event;
import com.example.pickme.repositories.EventRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

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
    public void fetchEvents(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        eventRepository.getEventsByOrganizerUserId(new OnCompleteListener<QuerySnapshot>() {
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
