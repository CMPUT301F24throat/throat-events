package com.example.pickme.repositories;

import com.google.firebase.firestore.CollectionReference;
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
}
