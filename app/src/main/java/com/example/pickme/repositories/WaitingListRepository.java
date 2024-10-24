package com.example.pickme.repositories;

import com.example.pickme.models.WaitingList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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

    // Create a new waitingList
    // Create a new waitingList
    public void addWaitingList(String eventId, String waitingListId, WaitingList waitingList, OnCompleteListener<Void> onCompleteListener, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        getWaitingListRef(eventId).document(waitingListId).set(waitingList)
                .addOnCompleteListener(onCompleteListener)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    // Read a waitingList by ID
    public void getWaitingListById(String eventId, String waitingListId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        getWaitingListDocRef(eventId, waitingListId).get().addOnCompleteListener(onCompleteListener);
    }

    // Update a waitingList
    public void updateWaitingList(String eventId, String waitingListId, WaitingList waitingList, OnCompleteListener<Void> onCompleteListener, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        getWaitingListDocRef(eventId, waitingListId).set(waitingList)
                .addOnCompleteListener(onCompleteListener)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    // Delete a waitingList by ID
    public void deleteWaitingList(String eventId, String waitingListId, OnCompleteListener<Void> onCompleteListener, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        getWaitingListDocRef(eventId, waitingListId).delete()
                .addOnCompleteListener(onCompleteListener)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    // Read a waitingList by event ID
    public void getWaitingListByEventId(String eventId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        getWaitingListRef(eventId).get().addOnCompleteListener(onCompleteListener);
    }


}