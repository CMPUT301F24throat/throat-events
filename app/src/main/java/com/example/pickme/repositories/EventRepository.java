package com.example.pickme.repositories;

import com.example.pickme.models.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Handles interactions with the events collection
 * Responsibilities:
 * CRUD operations for event data
 * Fetch event data based on eventId
 * Create new events and update existing events
 * Manage the event's waiting list (add/remove users)
 * Handle event posters (Firebase Storage) and geolocation requirements
 **/

public class EventRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference eventsRef = db.collection("events");

    // Create a new event
    public void addEvent(Event event, OnCompleteListener<DocumentReference> onCompleteListener) {
        eventsRef.add(event).addOnCompleteListener(onCompleteListener);
    }

    // Read an event by ID
    public void getEventById(String eventId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        eventsRef.document(eventId).get().addOnCompleteListener(onCompleteListener);
    }

    // Update an event
    public void updateEvent(Event event) {
        eventsRef.document(event.getEventId()).set(event);
    }
}
