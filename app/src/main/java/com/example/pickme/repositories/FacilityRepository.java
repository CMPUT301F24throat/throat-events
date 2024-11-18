package com.example.pickme.repositories;

import com.example.pickme.models.Facility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Handles interactions with the facilities collection
 * @author sophiecabungcal
 * @version 1.0
 * Responsibilities:
 * CRUD operations for facility data
 */
public class FacilityRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference facilitiesRef = db.collection("facilities");

    // Create a new facility
    public void addFacility(Facility facility, OnCompleteListener<Object> onCompleteListener) {
        db.runTransaction(transaction -> {
                    DocumentReference newFacilityRef = facilitiesRef.document();
                    facility.setFacilityId(newFacilityRef.getId());
                    transaction.set(newFacilityRef, facility);
                    return null;
                }).addOnCompleteListener(onCompleteListener)
                .addOnFailureListener(e -> {
                    // Handle the error
                    System.err.println("Transaction failed: " + e.getMessage());
                });
    }

    // Update facility name and location
    public void updateFacility(String facilityId, String facilityName, String facilityLocation, OnCompleteListener<Void> onCompleteListener) {
        facilitiesRef.document(facilityId)
                .update("facilityName", facilityName, "facilityLocation", facilityLocation)
                .addOnCompleteListener(onCompleteListener)
                .addOnFailureListener(e -> {
                    // Handle the error
                    System.err.println("Update failed: " + e.getMessage());
                });
    }


    // Get facility by ownerId
    public void getFacilityByOwnerId(String ownerId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        facilitiesRef.whereEqualTo("ownerId", ownerId)
                .get()
                .addOnCompleteListener(onCompleteListener)
                .addOnFailureListener(e -> {
                    // Handle the error
                    System.err.println("Query failed: " + e.getMessage());
                });
    }
}

