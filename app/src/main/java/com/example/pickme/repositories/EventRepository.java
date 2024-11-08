package com.example.pickme.repositories;

import com.example.pickme.models.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Facilitates CRUD operations and interactions with the Firestore events collection.
 * Manages event data transactions, including adding, updating, deleting, and retrieving events.
 * Ensures data integrity through Firestore transactions and handles completion notifications for each operation.
 *
 * @version 2.0
 * @author Ayub Ali
 * Responsibilities:
 * - Perform CRUD operations on event data in Firestore.
 * - Manage transactions for consistent updates and deletion.
 * - Retrieve event data by ID or organizer user ID.
 * - Notify completion or failure of Firestore operations.
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

    public void updateEvent(Event event, OnCompleteListener<Object> onCompleteListener) {
        DocumentReference newEventRef = eventsRef.document(event.getEventId());
        db.runTransaction(transaction -> {
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
    public void getEventsByOrganizerUserId(String userId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        eventsRef.whereEqualTo("organizerId", userId).get().addOnCompleteListener(onCompleteListener);
    }
}

/**
 * Code Sources
 *
 * Firebase Documentation:
 * - Firestore Transactions and Batched Writes
 * - CRUD Operations in Firestore
 * - Firestore Exception Handling
 *
 * Stack Overflow:
 * - How to use runTransaction for Firestore CRUD operations
 * - Firestore transaction error handling and best practices
 *
 * Java Documentation:
 * - Handling Completion Listeners in Firebase Documentation on using `OnCompleteListener` with Firestore operations.
 */
