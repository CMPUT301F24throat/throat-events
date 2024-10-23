package com.example.pickme.repositories;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Handles interactions with the waitingList subcollection
 * Responsibilities:
 * CRUD operations for waitingList data
 **/

public class WaitingListRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Get the reference to the waitingList subcollection for a specific event
    public CollectionReference getWaitingListRef(String eventId) {
        return db.collection("events").document(eventId).collection("waitingList");
    }

    // Get the reference to a specific waitingList document
    public DocumentReference getWaitingListDocRef(String eventId, String waitingListId) {
        return getWaitingListRef(eventId).document(waitingListId);
    }
}