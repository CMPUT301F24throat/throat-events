package com.example.pickme.repositories;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles interactions with the QRs collection
 * CRUD operations for QR data
 */
public class QrRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference qrRef = db.collection("QRs");

    /**
     * Creates a new QR document in the collection using only the QR association.
     *
     * @param qrAssociation Associated entity reference for the QR code (e.g., "/events/mli7Z3XcZsBMCHocKmr6")
     * @return Task for tracking success/failure
     */
    public Task<Void> createQR(String qrAssociation) {
        Map<String, Object> qrData = new HashMap<>();
        qrData.put("qrAssociation", qrAssociation);

        // Add the document to Firestore with an auto-generated ID
        return qrRef.add(qrData).continueWithTask(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                // Get the auto-generated document ID
                String qrID = task.getResult().getId();

                // Update the document with its own ID as the qrID field
                return task.getResult().update("qrID", qrID);
            } else {
                throw task.getException();  // Propagate any exception if creation failed
            }
        });
    }

    /**
     * Reads a QR document by its ID.
     *
     * @param qrID ID of the QR document to read
     * @return Task containing the QR document if found
     */
    public Task<QuerySnapshot> readQRByID(String qrID) {
        return qrRef.whereEqualTo("qrID", qrID).get();
    }

    /**
     * Reads a QR document by its association.
     *
     * @param qrAssociation Associated entity reference for the QR document
     * @return Task containing the QR document if found
     */
    public Task<QuerySnapshot> readQRByAssociation(String qrAssociation) {
        return qrRef.whereEqualTo("qrAssociation", qrAssociation).get();
    }

    /**
     * Deletes a QR document by its ID.
     *
     * @param qrID ID of the QR document to delete
     * @return Task for tracking success/failure
     */
    public Task<Void> deleteQR(String qrID) {
        return qrRef.document(qrID).delete();
    }
}
