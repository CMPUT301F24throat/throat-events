package com.example.pickme.repositories;

import android.util.Log;

import com.example.pickme.models.Event;
import com.example.pickme.models.WaitingListEntrant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Facilitates CRUD operations and interactions with the Firestore events collection.
 * Manages event data transactions, including adding, updating, deleting, and retrieving events.
 * Ensures data integrity through Firestore transactions and handles completion notifications for each operation.
 *
 * @version 2.0
 */
public class EventRepository {
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private CollectionReference eventsRef;
    private static EventRepository instance;

    /**
     * Default constructor that initializes Firebase Firestore and FirebaseAuth instances.
     */
    private EventRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
        this.eventsRef = db.collection("events");
    }

    /**
     * Singleton pattern to ensure only one instance of the repository is created.
     *
     * @return The instance of the EventRepository.
     */
    public static synchronized EventRepository getInstance(){
        if(instance == null)
            instance = new EventRepository();

        return instance;
    }

    /**
     * Constructor for dependency injection.
     *
     * @param db Firebase Firestore instance
     * @param auth Firebase Auth instance
     * @param eventsRef Collection reference for events
     */
    public EventRepository(FirebaseFirestore db, FirebaseAuth auth, CollectionReference eventsRef) {
        this.db = db;
        this.auth = auth;
        this.eventsRef = eventsRef;
    }

    /**
     * Creates a new event in the Firestore database.
     *
     * @param event The event to be added.
     * @param onCompleteListener The listener to notify upon completion.
     */
    public void addEvent(Event event, OnCompleteListener<String> onCompleteListener) {
        db.runTransaction(transaction -> {
            DocumentReference newEventRef = eventsRef.document();
            event.setEventId(newEventRef.getId());
            event.setHasLotteryExecuted(false);
            event.setWaitingList(new ArrayList<WaitingListEntrant>());
            transaction.set(newEventRef, event);

            return newEventRef.getId();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                onCompleteListener.onComplete(Tasks.forResult(task.getResult()));
            } else {
                onCompleteListener.onComplete(Tasks.forException(Objects.requireNonNull(task.getException())));
            }
        }).addOnFailureListener(e -> {
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
                    System.err.println("Transaction failed: " + e.getMessage());
                });
    }

    /**
     * Deletes an event from the Firestore database.
     *
     * @param eventId The ID of the event to be deleted.
     * @param onCompleteListener The listener to notify upon completion.
     */
    public void deleteEvent(String eventId, OnCompleteListener<Void> onCompleteListener) {
        eventsRef.document(eventId).delete().addOnCompleteListener(onCompleteListener)
                .addOnFailureListener(e -> {
                    System.err.println("Deletion failed: " + e.getMessage());
                });
    }

    /**
     * Retrieves a list of events organized by a specific user.
     *
     * @param userDeviceId       The ID of the organizer.
     * @param includePastEvents  Whether to include past events in the result.
     * @param eventListener The listener to notify upon completion.
     */
    public void getEventsByOrganizerId(String userDeviceId, boolean includePastEvents, EventListener<QuerySnapshot> eventListener) {
        eventsRef.whereEqualTo("organizerId", userDeviceId).addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.w("EventRepository", "Listen failed.", e);
                return;
            }

            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                List<Event> events = new ArrayList<>();
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    Event event = document.toObject(Event.class);
                    if (event != null) {
                        if (includePastEvents || !hasEventPassed(event)) {
                            events.add(event);
                        }
                    }
                }
                eventListener.onEvent(querySnapshot, null);
            } else {
                Log.d("EventRepository", "Current data: null");
            }
        });
    }

    /**
     * Retrieves an event by its ID.
     *
     * @param eventId The ID of the event to be retrieved.
     * @param eventListener The listener to notify upon completion.
     */
    public void getEventById(String eventId, EventListener<DocumentSnapshot> eventListener) {
        eventsRef.document(eventId).addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.w("EventRepository", "Listen failed.", e);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                Event event = documentSnapshot.toObject(Event.class);
                eventListener.onEvent(documentSnapshot, null);
            } else {
                Log.d("EventRepository", "Current data: null");
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
            return Objects.requireNonNull(eventDate).before(new Date());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid event date format.");
        }
    }

    /**
     * Retrieves all events from the Firestore database.
     *
     * @param eventListener The listener to notify upon completion.
     */
    public void getAllEvents(EventListener<QuerySnapshot> eventListener) {
        eventsRef.addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.w("EventRepository", "Listen failed.", e);
                return;
            }

            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                List<Event> events = new ArrayList<>();
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    Event event = document.toObject(Event.class);
                    if (event != null) {
                        events.add(event);
                    }
                }
                eventListener.onEvent(querySnapshot, null);
            } else {
                Log.d("EventRepository", "Current data: null");
            }
        });
    }

    /**
     * Retrieves events within a specific date range.
     *
     * @param startDate The start date of the range.
     * @param endDate The end date of the range.
     * @param eventListener The listener to notify upon completion.
     */
    public void getEventsByDateRange(Date startDate, Date endDate, EventListener<QuerySnapshot> eventListener) {
        eventsRef.whereGreaterThanOrEqualTo("eventDate", startDate)
                .whereLessThanOrEqualTo("eventDate", endDate)
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Log.w("EventRepository", "Listen failed.", e);
                        return;
                    }

                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        List<Event> events = new ArrayList<>();
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            Event event = document.toObject(Event.class);
                            if (event != null) {
                                events.add(event);
                            }
                        }
                        eventListener.onEvent(querySnapshot, null);
                    } else {
                        Log.d("EventRepository", "Current data: null");
                    }
                });
    }

    /**
     * Retrieves events based on their location.
     *
     * @param location The location to filter events by.
     * @param eventListener The listener to notify upon completion.
     */
    public void getEventsByLocation(String location, EventListener<QuerySnapshot> eventListener) {
        eventsRef.whereEqualTo("location", location).addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.w("EventRepository", "Listen failed.", e);
                return;
            }

            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                List<Event> events = new ArrayList<>();
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    Event event = document.toObject(Event.class);
                    if (event != null) {
                        events.add(event);
                    }
                }
                eventListener.onEvent(querySnapshot, null);
            } else {
                Log.d("EventRepository", "Current data: null");
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