package com.example.pickme.repositories;

import com.example.pickme.models.WaitingListEntrant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Handles interactions with the waiting list entrants subcollection
 * waitingListEntrant subcollection is a subcollection of the waitingList subcollection of the events collection
 * @author sophiecabungcal
 * @version 1.0
 * Responsibilities:
 * CRUD operations for waiting list data
 */
public class WaitingListEntrantRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Get the reference to the waitingListEntrants subcollection for a specific waitingList document
    public CollectionReference getWaitingListEntrantsRef(String eventId, String waitingListId) {
        return db.collection("events")
                .document(eventId)
                .collection("waitingList")
                .document(waitingListId)
                .collection("waitingListEntrants");
    }

    // Get the reference to a specific waitingListEntrant document
    public DocumentReference getWaitingListEntrantDocRef(String eventId, String waitingListId, String entrantId) {
        return getWaitingListEntrantsRef(eventId, waitingListId).document(entrantId);
    }

    // Create a new waitingListEntrant
    public void addWaitingListEntrant(String eventId, String waitingListId, WaitingListEntrant waitingListEntrant, OnCompleteListener<Object> onCompleteListener) {
        db.runTransaction(transaction -> {
                    DocumentReference newWaitingListEntrantRef = getWaitingListEntrantsRef(eventId, waitingListId).document();
                    waitingListEntrant.setWaitListEntrantId(newWaitingListEntrantRef.getId());
                    transaction.set(newWaitingListEntrantRef, waitingListEntrant);
                    return null;
                }).addOnCompleteListener(onCompleteListener)
                .addOnFailureListener(e -> {
                    // Handle the error
                    System.err.println("Transaction failed: " + e.getMessage());
                });
    }

    // Read a waitingListEntrant by ID
    public void getWaitingListEntrantById(String eventId, String waitingListId, String entrantId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        getWaitingListEntrantDocRef(eventId, waitingListId, entrantId).get().addOnCompleteListener(onCompleteListener);
    }

    // Update a waitingListEntrant
    public void updateWaitingListEntrant(String eventId, String waitingListId, String entrantId, WaitingListEntrant waitingListEntrant, OnCompleteListener<Void> onCompleteListener) {
        getWaitingListEntrantDocRef(eventId, waitingListId, entrantId).set(waitingListEntrant).addOnCompleteListener(onCompleteListener);
    }

    // Delete a waitingListEntrant by ID
    public void deleteWaitingListEntrant(String eventId, String waitingListId, String entrantId, OnCompleteListener<Void> onCompleteListener) {
        getWaitingListEntrantDocRef(eventId, waitingListId, entrantId).delete().addOnCompleteListener(onCompleteListener);
    }
}