package com.example.pickme.repositories;

import com.example.pickme.models.QR;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Handles interactions with the QRs collection
 * CRUD operations for QR data
 */
public class QrRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference qrRef = db.collection("QRs");

    /**
     * Creates a new QR document using a QR object.
     *
     * @param qr The QR object containing the association data
     * @return Task for tracking success/failure
     */
    public Task<DocumentReference> createQR(QR qr) {
        // Add the QR object to Firestore and let Firestore generate the document ID
        return qrRef.add(qr).continueWithTask(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentReference docRef = task.getResult();
                String generatedQrId = docRef.getId();  // Retrieve the auto-generated document ID

                // Set the qrId in the QR object and update Firestore with this value
                qr.setQrId(generatedQrId);
                return docRef.update("qrId", generatedQrId).continueWith(updateTask -> docRef);  // Return DocumentReference after update
            } else {
                throw task.getException();  // Propagate the exception if the add operation failed
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
        return qrRef.whereEqualTo("qrId", qrID).get();
    }

    /**
     * Retrieves all QR documents in Firestore.
     *
     * @return Task containing all QR documents.
     */
    public Task<QuerySnapshot> getAllQRs() {
        return qrRef.get();
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

/*
  Code Sources
  <p>
  Firebase Documentation:
  - Firestore Documentation
  <p>
  Java Documentation:
  - Java error handling with exceptions
 */
