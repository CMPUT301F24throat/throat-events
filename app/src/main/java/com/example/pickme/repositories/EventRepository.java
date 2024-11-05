package com.example.pickme.repositories;

import com.example.pickme.models.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Repository for handling CRUD operations in the events collection within Firestore.
 *
 * Responsibilities:
 * - Provides functions to create, read, update, and delete events.
 *
 * @version 2.0
 * @author Ayub Ali, sophiecabungcal
 */
public class EventRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference eventsRef = db.collection("events");
    private final WaitingListRepository waitingListRepository = new WaitingListRepository();

    // Create a new event
    public void addEvent(Event event, OnCompleteListener<Object> onCompleteListener) {
        db.runTransaction(transaction -> {
                    DocumentReference newEventRef = eventsRef.document();
                    event.setEventId(newEventRef.getId());
                    transaction.set(newEventRef, event);
                    return null;
                }).addOnCompleteListener(onCompleteListener)
                .addOnFailureListener(e -> System.err.println("Transaction failed: " + e.getMessage()));
    }

    // Delete an event by ID
    public void deleteEvent(String eventId, OnCompleteListener<Void> onCompleteListener) {
        db.collection("events")
                .document(eventId)
                .delete()
                .addOnCompleteListener(onCompleteListener);
    }

    // Read an event by its ID
    public void getEventById(String eventId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        eventsRef.document(eventId).get().addOnCompleteListener(onCompleteListener);
    }

    // Update an event
    public void updateEvent(Event event) {
        eventsRef.document(event.getEventId()).set(event);
    }

    // Read all events by organizer user ID
    public void getEventsByOrganizerUserId(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        eventsRef.get().addOnCompleteListener(onCompleteListener);
    }
}

/**
 * Code Sources
 *
 * ChatGPT
 * - "Handling Firestore data transactions in Android."
 *
 * Stack Overflow
 * - "Firestore: Best practices for CRUD operations in repositories."
 *
 * Firebase Documentation
 * - Firestore Data Model and Collection Management
 */
