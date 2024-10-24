package com.example.pickme.repositories;

import com.example.pickme.models.WaitingList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Handles interactions with the waiting list subcollection
 * waitingList subcollection is a subcollection of the events collection
 * @author sophiecabungcal
 * @version 1.0
 * Responsibilities:
 * CRUD operations for event data
 */
public class WaitingListRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Get the reference to the waitingList subcollection for a specific event
    public CollectionReference getWaitingListRef(String eventId) {
        return db.collection("events").document(eventId).collection("waitingList");
    }

    // Create a new waitingList with auto-generated ID
    public void addWaitingList(String eventId, WaitingList waitingList, OnCompleteListener<Object> onCompleteListener) {
        db.runTransaction(transaction -> {
                    DocumentReference newWaitingListRef = getWaitingListRef(eventId).document();
                    waitingList.setWaitingListId(newWaitingListRef.getId());
                    transaction.set(newWaitingListRef, waitingList);
                    return null;
                }).addOnCompleteListener(onCompleteListener)
                .addOnFailureListener(e -> {
                    // Handle the error
                    System.err.println("Transaction failed: " + e.getMessage());
                });
    }

    // Get the reference to a specific waitingList document
    public DocumentReference getWaitingListDocRef(String eventId, String waitingListId) {
        return getWaitingListRef(eventId).document(waitingListId);
    }

    // Read a waitingList by ID
    public void getWaitingListById(String eventId, String waitingListId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        getWaitingListDocRef(eventId, waitingListId).get()
                .addOnCompleteListener(onCompleteListener);
    }

    // Update a waitingList
    public void updateWaitingList(String eventId, String waitingListId, WaitingList waitingList, OnCompleteListener<Void> onCompleteListener) {
        getWaitingListDocRef(eventId, waitingListId).set(waitingList)
                .addOnCompleteListener(onCompleteListener);
    }

    // Delete a waitingList by ID
    public void deleteWaitingList(String eventId, String waitingListId, OnCompleteListener<Void> onCompleteListener) {
        getWaitingListDocRef(eventId, waitingListId).delete()
                .addOnCompleteListener(onCompleteListener);
    }

    // Read a waitingList by event ID
    public void getWaitingListByEventId(String eventId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        getWaitingListRef(eventId).get()
                .addOnCompleteListener(onCompleteListener);
    }
}