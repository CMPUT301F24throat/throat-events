package com.example.pickme.repositories;

import com.example.pickme.models.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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

    /**
     * Creates a new event in the Firestore database.
     *
     * @param event The event to be added.
     * @param onCompleteListener The listener to notify upon completion.
     */
    public void addEvent(Event event, OnCompleteListener<Object> onCompleteListener) {
        DocumentReference eventRef = eventsRef.document(event.getEventId());
        db.runTransaction(transaction -> {
                    transaction.set(eventRef, event);

                    // Create an empty waitingList subcollection
                    CollectionReference waitingListRef = eventRef.collection("waitingList");
                    transaction.set(waitingListRef.document("placeholder"), new HashMap<>()); // Add a placeholder document

                    return null;
                }).addOnCompleteListener(onCompleteListener)
                .addOnFailureListener(e -> {
                    // Handle the error
                    System.err.println("Transaction failed: " + e.getMessage());
                });
    }

    /**
     * Updates an existing event in the Firestore database.
     *
     * @param event The event to be updated.
     * @param onCompleteListener The listener to notify upon completion.
     */
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

    /**
     * Deletes an event from the Firestore database.
     *
     * @param eventId The ID of the event to be deleted.
     * @param onCompleteListener The listener to notify upon completion.
     */
    public void deleteEvent(String eventId, OnCompleteListener<Object> onCompleteListener) {
        db.runTransaction(transaction -> {
                    // Reference to the event document you want to delete
                    DocumentReference eventRef = eventsRef.document(eventId);

                    // Delete the event document
                    transaction.delete(eventRef);

                    // Optionally, delete the subcollections (waitingList in this case)
                    CollectionReference waitingListRef = eventRef.collection("waitingList");

                    // Delete all documents in the "waitingList" subcollection
                    transaction.delete(waitingListRef.document("placeholder"));

                    return null;
                }).addOnCompleteListener(onCompleteListener)
                .addOnFailureListener(e -> {
                    // Handle the error
                    System.err.println("Transaction failed: " + e.getMessage());
                });
    }

    /**
     * Retrieves a list of events organized by a specific user.
     *
     * @param userId The ID of the organizer.
     * @param includePastEvents Whether to include past events in the result.
     * @param onCompleteListener The listener to notify upon completion.
     */
    public void getEventsByOrganizerId(String userId, boolean includePastEvents, OnCompleteListener<List<Event>> onCompleteListener) {
        eventsRef.whereEqualTo("organizerId", userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<Event> events = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                    Event event = document.toObject(Event.class);
                    if (event != null) {
                        if (includePastEvents || !hasEventPassed(event)) {
                            events.add(event);
                        }
                    }
                }
                onCompleteListener.onComplete(Tasks.forResult(events));
            } else {
                onCompleteListener.onComplete(Tasks.forException(task.getException()));
            }
        });
    }

    /**
     * Retrieves an event by its ID.
     *
     * @param eventId The ID of the event to be retrieved.
     * @param onCompleteListener The listener to notify upon completion.
     */
    public void getEventById(String eventId, OnCompleteListener<Event> onCompleteListener) {
        eventsRef.document(eventId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Event event = document.toObject(Event.class);
                    onCompleteListener.onComplete(Tasks.forResult(event));
                } else {
                    onCompleteListener.onComplete(Tasks.forException(new Exception("Event not found")));
                }
            } else {
                onCompleteListener.onComplete(Tasks.forException(task.getException()));
            }
        });
    }

    /**
     * Checks if the event date has passed.
     *
     * @param event The event to check.
     * @return true if the event date has passed, false otherwise.
     */
    public boolean hasEventPassed(Event event) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d yyyy, h:mm a");
        try {
            Date eventDate = dateFormat.parse(event.getEventDate());
            return eventDate.before(new Date());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid event date format.");
        }
    }

    /**
     * Retrieves all events from the Firestore database.
     *
     * @param onCompleteListener The listener to notify upon completion.
     */
    public void getAllEvents(OnCompleteListener<List<Event>> onCompleteListener) {
        eventsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<Event> events = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                    Event event = document.toObject(Event.class);
                    if (event != null) {
                        events.add(event);
                    }
                }
                onCompleteListener.onComplete(Tasks.forResult(events));
            } else {
                onCompleteListener.onComplete(Tasks.forException(task.getException()));
            }
        });
    }

    /**
     * Retrieves events within a specific date range.
     *
     * @param startDate The start date of the range.
     * @param endDate The end date of the range.
     * @param onCompleteListener The listener to notify upon completion.
     */
    public void getEventsByDateRange(Date startDate, Date endDate, OnCompleteListener<List<Event>> onCompleteListener) {
        eventsRef.whereGreaterThanOrEqualTo("eventDate", startDate)
                .whereLessThanOrEqualTo("eventDate", endDate)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Event> events = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            Event event = document.toObject(Event.class);
                            if (event != null) {
                                events.add(event);
                            }
                        }
                        onCompleteListener.onComplete(Tasks.forResult(events));
                    } else {
                        onCompleteListener.onComplete(Tasks.forException(task.getException()));
                    }
                });
    }

    /**
     * Retrieves events based on their location.
     *
     * @param location The location to filter events by.
     * @param onCompleteListener The listener to notify upon completion.
     */
    public void getEventsByLocation(String location, OnCompleteListener<List<Event>> onCompleteListener) {
        eventsRef.whereEqualTo("location", location).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<Event> events = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                    Event event = document.toObject(Event.class);
                    if (event != null) {
                        events.add(event);
                    }
                }
                onCompleteListener.onComplete(Tasks.forResult(events));
            } else {
                onCompleteListener.onComplete(Tasks.forException(task.getException()));
            }
        });
    }

    /**
     * Retrieves events based on their status.
     *
     * @param status The status to filter events by.
     * @param onCompleteListener The listener to notify upon completion.
     */
    public void getEventsByStatus(String status, OnCompleteListener<List<Event>> onCompleteListener) {
        eventsRef.whereEqualTo("status", status).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<Event> events = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                    Event event = document.toObject(Event.class);
                    if (event != null) {
                        events.add(event);
                    }
                }
                onCompleteListener.onComplete(Tasks.forResult(events));
            } else {
                onCompleteListener.onComplete(Tasks.forException(task.getException()));
            }
        });
    }

    /**
     * Retrieves events based on their category.
     *
     * @param category The category to filter events by.
     * @param onCompleteListener The listener to notify upon completion.
     */
    public void getEventsByCategory(String category, OnCompleteListener<List<Event>> onCompleteListener) {
        eventsRef.whereEqualTo("category", category).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<Event> events = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                    Event event = document.toObject(Event.class);
                    if (event != null) {
                        events.add(event);
                    }
                }
                onCompleteListener.onComplete(Tasks.forResult(events));
            } else {
                onCompleteListener.onComplete(Tasks.forException(task.getException()));
            }
        });
    }
}
/*
  Code Sources
  <p>
  Firebase Documentation:
  - Firestore Transactions and Batched Writes
  - CRUD Operations in Firestore
  - Firestore Exception Handling
  <p>
  Stack Overflow:
  - How to use runTransaction for Firestore CRUD operations
  - Firestore transaction error handling and best practices
  <p>
  Java Documentation:
  - Handling Completion Listeners in Firebase Documentation on using `OnCompleteListener` with Firestore operations.
 */