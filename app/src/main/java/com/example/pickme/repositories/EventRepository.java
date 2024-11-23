package com.example.pickme.repositories;

import com.example.pickme.models.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Facilitates CRUD operations and interactions with the Firestore events collection.
 * Manages event data transactions, including adding, updating, deleting, and retrieving events.
 * Ensures data integrity through Firestore transactions and handles completion notifications for each operation.
 *
 * @version 2.0
 */
public class EventRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference eventsRef = db.collection("events");

    // Create a new event
    public void addEvent(Event event, OnCompleteListener<Object> onCompleteListener) {
        db.runTransaction(transaction -> {
                    DocumentReference newEventRef = eventsRef.document();
                    event.setEventId(newEventRef.getId());
                    transaction.set(newEventRef, event);

                    // Create an empty waitingList subcollection
                    CollectionReference waitingListRef = newEventRef.collection("waitingList");
//                    transaction.set(waitingListRef.document(), new Object()); // Add an empty document to initialize the subcollection

                    return null;
                }).addOnCompleteListener(onCompleteListener)
                .addOnFailureListener(e -> {
                    // Handle the error
                    System.err.println("Transaction failed: " + e.getMessage());
                });
    }

    // Update an event
    public void updateEvent(Event event, OnCompleteListener<Object> onCompleteListener) {
        DocumentReference eventRef = eventsRef.document(event.getEventId());
        db.runTransaction(transaction -> {
                    transaction.set(eventRef, event);
                    return null;
                }).addOnCompleteListener(onCompleteListener)
                .addOnFailureListener(e -> {
                    // Handle the error
                    System.err.println("Transaction failed: " + e.getMessage());
                });
    }

    // Delete an event
    public void deleteEvent(String eventId, OnCompleteListener<Void> onCompleteListener) {
        eventsRef.document(eventId).delete().addOnCompleteListener(onCompleteListener)
                .addOnFailureListener(e -> {
                    // Handle the error
                    System.err.println("Deletion failed: " + e.getMessage());
                });
    }

    // Read an event by ID
    public void getEventById(String eventId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        eventsRef.document(eventId).get().addOnCompleteListener(onCompleteListener);
    }

    // Read all events by organizer user ID
    public void getEventsByOrganizerId(String userId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        eventsRef.whereEqualTo("organizerId", userId).get().addOnCompleteListener(onCompleteListener);
    }

    // Get an event's waiting list current number of entrants
    public void getEventWaitingListCount(String eventId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        eventsRef.document(eventId).collection("waitingList")
                .whereNotEqualTo(FieldPath.documentId(), "emptyDoc") // Exclude the empty document
                .get().addOnCompleteListener(onCompleteListener);
    }

    //TODO: Get event waiting list, get event status, get event waiting list status
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
