package com.example.pickme.utils;

import android.util.Log;

import com.example.pickme.models.Event;
import com.example.pickme.repositories.EventRepository;
import com.example.pickme.repositories.QrRepository;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Utility class for cleaning up orphaned QR codes in the Firestore database.
 * Identifies QR codes associated with deleted or non-existent events and removes them
 * to maintain database integrity.
 */
public class qrCleanup {

    /**
     * Cleans up QR codes by removing those associated with non-existent or deleted events.
     * <p>
     * Steps involved:
     * <ul>
     *   <li>Retrieve all QR codes from Firestore.</li>
     *   <li>Extract the associated event ID from each QR code's "qrAssociation" field.</li>
     *   <li>Check if the event exists in the database.</li>
     *   <li>Delete QR codes linked to missing or invalid events.</li>
     * </ul>
     */
    public static void cleanUpQRCodes() {
        QrRepository qrRepository = QrRepository.getInstance();
        EventRepository eventRepository = EventRepository.getInstance();

        qrRepository.getAllQRs().addOnCompleteListener(task -> {
            if (!task.isSuccessful() || task.getResult() == null) {
                Log.e("qrCleanup", "Failed to retrieve QR codes: ", task.getException());
                return;
            }

            QuerySnapshot qrDocuments = task.getResult();
            if (qrDocuments.isEmpty()) {
                Log.d("qrCleanup", "No QR codes found in Firestore.");
                return;
            }

            Log.d("qrCleanup", "Found " + qrDocuments.size() + " QR codes.");
            qrDocuments.getDocuments().forEach(qrDoc -> processQRCode(qrDoc.getId(), qrDoc.getString("qrAssociation"), eventRepository, qrRepository));
        });
    }

    /**
     * Processes a single QR code by checking the associated event and deleting the QR code
     * if the event is invalid or does not exist.
     *
     * @param qrId          The ID of the QR code document.
     * @param qrAssociation The "qrAssociation" field of the QR code document.
     * @param eventRepo     Instance of EventRepository for event lookups.
     * @param qrRepo        Instance of QrRepository for QR code management.
     */
    private static void processQRCode(String qrId, String qrAssociation, EventRepository eventRepo, QrRepository qrRepo) {
        if (qrAssociation == null || !qrAssociation.startsWith("/events/")) {
            Log.d("qrCleanup", "Invalid or missing association for QR code: " + qrId);
            return;
        }

        String eventId = qrAssociation.substring("/events/".length());
        Log.d("qrCleanup", "Extracted event ID: " + eventId);

        eventRepo.getEventById(eventId, task -> {
            if (!task.isSuccessful()) {
                Log.e("qrCleanup", "Failed to check event existence for event ID: " + eventId + ". Deleting associated QR code.");
                deleteQRCode(qrId, qrRepo);
                return;
            }

            Event event = task.getResult();
            if (event == null) {
                Log.d("qrCleanup", "Event not found for QR code: " + qrId);
                deleteQRCode(qrId, qrRepo);
            } else {
                Log.d("qrCleanup", "Event found for QR code: " + qrId);
            }
        });
    }

    /**
     * Deletes a QR code document from Firestore and logs the result.
     *
     * @param qrId   The ID of the QR code document to delete.
     * @param qrRepo Instance of QrRepository for QR code deletion.
     */
    private static void deleteQRCode(String qrId, QrRepository qrRepo) {
        qrRepo.deleteQR(qrId)
                .addOnSuccessListener(aVoid -> Log.d("qrCleanup", "Deleted QR code: " + qrId))
                .addOnFailureListener(e -> Log.e("qrCleanup", "Failed to delete QR code: " + qrId, e));
    }
}
