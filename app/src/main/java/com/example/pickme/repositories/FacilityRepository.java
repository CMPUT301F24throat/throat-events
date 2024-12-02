package com.example.pickme.repositories;

import com.example.pickme.models.Facility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Handles interactions with the facilities collection.
 * Responsibilities:
 * - CRUD operations for facility data.
 */
public class FacilityRepository {
    private final FirebaseFirestore db;
    private final CollectionReference facilitiesRef;
    private final CollectionReference usersRef;

    /**
     * Default constructor that initializes Firestore and collection references.
     */
    private FacilityRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.facilitiesRef = db.collection("facilities");
        this.usersRef = db.collection("users");
    }

    private static FacilityRepository instance;

    /**
     * Singleton pattern to get the instance of FacilityRepository.
     * @return the single instance of FacilityRepository.
     */
    public static FacilityRepository getInstance(){
        if(instance == null)
            instance = new FacilityRepository();

        return instance;
    }

    /**
     * Adds a new facility to the Firestore database.
     * Checks if the user exists before adding the facility.
     * @param facility the facility to be added.
     * @param onCompleteListener the listener to handle completion events.
     */
    public void addFacility(Facility facility, OnCompleteListener<Object> onCompleteListener) {
        usersRef.document(facility.getOwnerId()).get().addOnCompleteListener(userTask -> {
            if (userTask.isSuccessful() && userTask.getResult().exists()) {
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
            } else {
                System.err.println("User does not exist");
            }
        });
    }

    /**
     * Updates the name and location of an existing facility.
     * Checks if the facility and user exist before updating.
     * @param facilityId the ID of the facility to be updated.
     * @param facilityName the new name of the facility.
     * @param facilityLocation the new location of the facility.
     * @param onCompleteListener the listener to handle completion events.
     */
    public void updateFacility(String facilityId, String facilityName, String facilityLocation, OnCompleteListener<Void> onCompleteListener) {
        facilitiesRef.document(facilityId).get().addOnCompleteListener(facilityTask -> {
            if (facilityTask.isSuccessful() && facilityTask.getResult().exists()) {
                String ownerId = facilityTask.getResult().getString("ownerId");
                if (ownerId != null) {
                    usersRef.document(ownerId).get().addOnCompleteListener(userTask -> {
                        if (userTask.isSuccessful() && userTask.getResult().exists()) {
                            facilitiesRef.document(facilityId)
                                    .update("facilityName", facilityName, "facilityLocation", facilityLocation)
                                    .addOnCompleteListener(onCompleteListener)
                                    .addOnFailureListener(e -> {
                                        // Handle the error
                                        System.err.println("Update failed: " + e.getMessage());
                                    });
                        } else {
                            System.err.println("User does not exist");
                        }
                    });
                } else {
                    System.err.println("Owner ID not found");
                }
            } else {
                System.err.println("Facility does not exist");
            }
        });
    }

    /**
     * Retrieves facilities by the owner's device ID.
     * @param ownerDeviceId the device ID of the owner.
     * @param onCompleteListener the listener to handle completion events.
     */
    public void getFacilityByOwnerDeviceId(String ownerDeviceId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        facilitiesRef.whereEqualTo("ownerId", ownerDeviceId)
                .get()
                .addOnCompleteListener(onCompleteListener)
                .addOnFailureListener(e -> {
                    // Handle the error
                    System.err.println("Query failed: " + e.getMessage());
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

    // Delete a facility by its ID
    public void deleteFacility(String facilityId, OnCompleteListener<Void> onCompleteListener) {
        facilitiesRef.document(facilityId)
                .delete()
                .addOnCompleteListener(onCompleteListener)
                .addOnFailureListener(e -> {
                    // Handle the error
                    System.err.println("Delete failed: " + e.getMessage());
                });
    }
}