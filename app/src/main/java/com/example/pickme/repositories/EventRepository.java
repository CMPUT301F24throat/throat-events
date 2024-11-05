package com.example.pickme.repositories;

import com.example.pickme.models.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Handles interactions with the events collection
 * @author sophiecabungcal
 * @version 1.0
 * Responsibilities:
 * CRUD operations for event data
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
                .addOnFailureListener(e -> {
                    // Handle the error
                    System.err.println("Transaction failed: " + e.getMessage());
                });
    }

    public void deleteEvent(String eventId, OnCompleteListener<Void> onCompleteListener) {
        db.collection("events")
                .document(eventId)
                .delete()
                .addOnCompleteListener(onCompleteListener);
    }

    // Read an event by ID
    public void getEventById(String eventId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        eventsRef.document(eventId).get().addOnCompleteListener(onCompleteListener);
    }

    // Update an event
    public void updateEvent(Event event) {
        eventsRef.document(event.getEventId()).set(event);
    }

    // Delete an event by ID
    public void deleteEvent(String eventId) {
        eventsRef.document(eventId).delete();
    }

    // Read all events by organizer user ID
    public void getEventsByOrganizerUserId(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        eventsRef.get().addOnCompleteListener(onCompleteListener);
    }
}
