package com.example.pickme.repositories;

import static com.google.firebase.appcheck.internal.util.Logger.TAG;

import android.util.Log;

import com.example.pickme.models.QR;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Handles interactions with the QRs collection
 * CRUD operations for QR data
 */
public class QrRepository {
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private final CollectionReference qrRef;
    private static QrRepository instance;

    /**
     * Default constructor that initializes Firebase Firestore and FirebaseAuth instances.
     */
    private QrRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
        this.qrRef = db.collection("QRs");
    }

    /**
     * Singleton pattern to ensure only one instance of the repository is created.
     *
     * @return The instance of the QrRepository.
     */
    public static synchronized QrRepository getInstance() {
        if (instance == null) {
            instance = new QrRepository();
        }
        return instance;
    }

    /**
     * Creates a new QR document using a QR object.
     *
     * @param qr The QR object containing the association data
     * @return Task for tracking success/failure
     */
    public Task<DocumentReference> createQR(QR qr) {
        Log.d(TAG, "Creating QR: " + qr);  // debug
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
     * @param eventListener The listener to notify upon completion
     */
    public void readQRByID(String qrID, EventListener<QuerySnapshot> eventListener) {
        qrRef.whereEqualTo("qrId", qrID).addSnapshotListener(eventListener);
    }

    /**
     * Retrieves all QR documents in Firestore.
     *
     * @param eventListener The listener to notify upon completion
     */
    public void getAllQRs(EventListener<QuerySnapshot> eventListener) {
        qrRef.addSnapshotListener(eventListener);
    }

    /**
     * Reads a QR document by its association.
     *
     * @param qrAssociation Associated entity reference for the QR document
     * @param eventListener The listener to notify upon completion
     */
    public void readQRByAssociation(String qrAssociation, EventListener<QuerySnapshot> eventListener) {
        qrRef.whereEqualTo("qrAssociation", qrAssociation).addSnapshotListener(eventListener);
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