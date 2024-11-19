package com.example.pickme.repositories;

import com.example.pickme.models.WaitingListEntrant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Handles interactions with the waiting list entrants subcollection
 * waitingListEntrant subcollection is a subcollection of the events collection
 */
public class WaitingListEntrantRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Get the reference to the waitingListEntrants subcollection for a specific event
    public CollectionReference getWaitingListEntrantsRef(String eventId) {
        return db.collection("events")
                .document(eventId)
                .collection("waitingList");
    }

    // Get the reference to a specific waitingListEntrant document
    public DocumentReference getWaitingListEntrantDocRef(String eventId, String entrantId) {
        return getWaitingListEntrantsRef(eventId).document(entrantId);
    }

    // Create a new waitingListEntrant
    public void addWaitingListEntrant(String eventId, WaitingListEntrant waitingListEntrant, OnCompleteListener<Object> onCompleteListener) {
        db.runTransaction(transaction -> {
                    DocumentReference newWaitingListEntrantRef = getWaitingListEntrantsRef(eventId).document();
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
    public void getWaitingListEntrantById(String eventId, String entrantId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        getWaitingListEntrantDocRef(eventId, entrantId).get().addOnCompleteListener(onCompleteListener);
    }

    // Update a waitingListEntrant
    public void updateWaitingListEntrant(String eventId, String entrantId, WaitingListEntrant waitingListEntrant, OnCompleteListener<Void> onCompleteListener) {
        getWaitingListEntrantDocRef(eventId, entrantId).set(waitingListEntrant).addOnCompleteListener(onCompleteListener);
    }

    // Delete a waitingListEntrant by ID
    public void deleteWaitingListEntrant(String eventId, String entrantId, OnCompleteListener<Void> onCompleteListener) {
        getWaitingListEntrantDocRef(eventId, entrantId).delete().addOnCompleteListener(onCompleteListener);
    }
}