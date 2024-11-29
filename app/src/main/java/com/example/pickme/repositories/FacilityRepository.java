package com.example.pickme.repositories;

import com.example.pickme.models.Facility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Handles interactions with the facilities collection.
 * <p>
 * Responsibilities:
 * - CRUD operations for facility data
 *
 * @version 1.0
 */
public class FacilityRepository {
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private final CollectionReference facilitiesRef;
    private static FacilityRepository instance;

    /**
     * Constructor initializes Firebase Firestore and Auth instances, and sets the facilities collection reference.
     */
    private FacilityRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
        this.facilitiesRef = db.collection("facilities");
    }

    /**
     * Singleton pattern to get the instance of FacilityRepository.
     *
     * @return the single instance of FacilityRepository
     */
    public static synchronized FacilityRepository getInstance(){
        if(instance == null)
            instance = new FacilityRepository();
        return instance;
    }

    /**
     * Adds a new facility to the Firestore database.
     *
     * @param facility the Facility object to be added
     * @param onCompleteListener the listener to handle completion of the transaction
     */
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

    /**
     * Updates the name and location of an existing facility.
     *
     * @param facilityId the ID of the facility to be updated
     * @param facilityName the new name of the facility
     * @param facilityLocation the new location of the facility
     * @param onCompleteListener the listener to handle completion of the update
     */
    public void updateFacility(String facilityId, String facilityName, String facilityLocation, OnCompleteListener<Void> onCompleteListener) {
        facilitiesRef.document(facilityId)
                .update("facilityName", facilityName, "facilityLocation", facilityLocation)
                .addOnCompleteListener(onCompleteListener)
                .addOnFailureListener(e -> {
                    // Handle the error
                    System.err.println("Update failed: " + e.getMessage());
                });
    }

    /**
     * Retrieves facilities by the owner's ID.
     *
     * @param ownerId the ID of the owner whose facilities are to be retrieved
     * @param eventListener the listener to handle real-time updates
     */
    public void getFacilityByOwnerId(String ownerId, EventListener<QuerySnapshot> eventListener) {
        facilitiesRef.whereEqualTo("ownerId", ownerId)
                .addSnapshotListener(eventListener);
    }
}