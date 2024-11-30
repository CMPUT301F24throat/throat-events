package com.example.pickme.repositories;

import android.content.Context;
import android.util.Log;

import com.example.pickme.models.Event;
import com.example.pickme.models.User;
import com.example.pickme.models.WaitingListEntrant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    private ArrayList<Event> listToUpdate;
    private Event eventToUpdate;
    private boolean listening = false;

    private static EventRepository instance;

    public static EventRepository getInstance(){
        if(instance == null)
            instance = new EventRepository();

        return instance;
    }

    /**
     * Default constructor that initializes Firebase Firestore and FirebaseAuth instances.
     */
    private EventRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
        this.eventsRef = db.collection("events");
    }

    /**
     * Constructor for dependency injection.
     *
     * @param db Firebase Firestore instance
     * @param auth Firebase Auth instance
     * @param eventsRef Collection reference for events
     */
    private EventRepository(FirebaseFirestore db, FirebaseAuth auth, CollectionReference eventsRef) {
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
    public void addEvent(Event event, OnCompleteListener<Object> onCompleteListener) {
        db.runTransaction(transaction -> {
                    DocumentReference newEventRef = eventsRef.document();
                    event.setEventId(newEventRef.getId());
                    event.setHasLotteryExecuted(false);
                    event.setWaitingList(new ArrayList<WaitingListEntrant>());
                    transaction.set(newEventRef, event);

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
     * @param onCompleteListener The listener to notify upon completion.
     */
    public void getEventsByOrganizerId(String userDeviceId, boolean includePastEvents, OnCompleteListener<List<Event>> onCompleteListener) {
        eventsRef.whereEqualTo("organizerId", userDeviceId).get().addOnCompleteListener(task -> {
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

    public void addSnapshotListener(Context context){
        if(listening)
            return;

        User user = User.getInstance();

        Log.i("EVENT", "Adding Listener");

        eventsRef.addSnapshotListener((query, error) -> {
            if(!listening){
                listening = true;
                return;
            }

            if(error != null){
                Log.e("EVENT", "Listen failed: ", error);
                listening = false;
                return;
            }

            if(query == null)
                return;

            //we're not looking to update anything
            if(listToUpdate == null && eventToUpdate == null)
                return;

            for(DocumentChange change : query.getDocumentChanges()){
                Event event = change.getDocument().toObject(Event.class);

                if(change.getType() == DocumentChange.Type.MODIFIED){

                    if(eventToUpdate != null && event.getEventId().equals(eventToUpdate.getEventId())){
                        eventToUpdate.update(event);
                    }

                    if(listToUpdate != null){
                        for(Event e : listToUpdate){
                            if(!e.getEventId().equals(event.getEventId()))
                                continue;

                            e.update(event);
                        }
                    }

                }

                else if(change.getType() == DocumentChange.Type.REMOVED){

                    if(eventToUpdate != null && event.getEventId().equals(eventToUpdate.getEventId())){
                        eventToUpdate.setEventId(null);
                    }

                    if(listToUpdate != null){
                        for(Event e : listToUpdate){
                            if(!e.getEventId().equals(event.getEventId()))
                                continue;

                            e.setEventId(null);
                        }
                    }
                }
            }
        });

    }

    public void attachList(ArrayList<Event> events){
        this.listToUpdate = events;
    }

    public void attachEvent(Event event){
        this.eventToUpdate = event;
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