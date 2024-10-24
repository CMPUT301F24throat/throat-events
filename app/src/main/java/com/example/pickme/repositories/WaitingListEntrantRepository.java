package com.example.pickme.repositories;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Handles interactions with the waitingListEntrants subcollection
 * Responsibilities:
 * CRUD operations for waitingListEntrants data
 **/

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
}