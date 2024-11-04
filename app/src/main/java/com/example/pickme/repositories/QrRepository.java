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
     * Creates a new QR document in the collection.
     *
     * @param qrID       Unique identifier for the QR code
     * @param qrType     Type of the QR code (e.g., "event_join" or "event_info")
     * @param qrReference Reference path for the QR (e.g., "/events/mli7Z3XcZsBMCHocKmr6")
     * @param uploaderId ID of the user who uploaded the QR code
     * @param dateCreated Date when the QR code was created
     * @return Task for tracking success/failure
     */
    public Task<Void> createQR(String qrID, String qrType, String qrReference, String uploaderId, String dateCreated) {
        Map<String, Object> qrData = new HashMap<>();
        qrData.put("qrID", qrID);
        qrData.put("qrType", qrType);
        qrData.put("qrReference", qrReference);
        qrData.put("uploaderId", uploaderId);
        qrData.put("dateCreated", dateCreated);

        return qrRef.document(qrID).set(qrData);
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
     * Reads a QR document by its reference and type.
     *
     * @param qrReference Reference path for the QR document
     * @param qrType Type of the QR code (e.g., "event_join")
     * @return Task containing the QR document if found
     */
    public Task<QuerySnapshot> readQRByReferenceAndType(String qrReference, String qrType) {
        return qrRef.whereEqualTo("qrReference", qrReference)
                .whereEqualTo("qrType", qrType)
                .get();
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
