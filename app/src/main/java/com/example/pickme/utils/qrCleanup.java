package com.example.pickme.utils;

import android.util.Log;

import com.example.pickme.models.Event;
import com.example.pickme.repositories.EventRepository;
import com.example.pickme.repositories.QrRepository;
import com.google.firebase.firestore.QuerySnapshot;

public class qrCleanup {

    /**
     * Checks all QR codes and removes those associated with deleted events.
     */
    public static void cleanUpQRCodes() {
        QrRepository qrRepository = new QrRepository();
        EventRepository eventRepository = EventRepository.getInstance();

        qrRepository.getAllQRs().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                QuerySnapshot qrDocuments = task.getResult();
                if (qrDocuments.isEmpty()) {
                    Log.d("qrCleanup", "No QR codes found in Firestore.");
                    return;
                }

                Log.d("qrCleanup", "Found " + qrDocuments.size() + " QR codes.");
                qrDocuments.getDocuments().forEach(qrDoc -> {
                    String qrAssociation = qrDoc.getString("qrAssociation");

                    if (qrAssociation != null && qrAssociation.startsWith("/events/")) {
                        String eventId = qrAssociation.substring("/events/".length());
                        Log.d("qrCleanup", "Extracted event ID: " + eventId);

                        eventRepository.getEventById(eventId, eventTask -> {
                            if (!eventTask.isSuccessful()) {
                                // Task failed, delete the QR code since we assume the event is invalid
                                Log.e("qrCleanup", "Failed to check event existence for event ID: " + eventId + ", deleting associated QR code.");
                                qrRepository.deleteQR(qrDoc.getId())
                                        .addOnSuccessListener(aVoid -> Log.d("qrCleanup", "Deleted QR code: " + qrDoc.getId()))
                                        .addOnFailureListener(e -> Log.e("qrCleanup", "Failed to delete QR code: " + qrDoc.getId(), e));
                                return;
                            }

                            Event event = eventTask.getResult();
                            if (event == null) {
                                // Event does not exist, delete the QR code
                                Log.d("qrCleanup", "Event not found for QR code: " + qrDoc.getId());
                                qrRepository.deleteQR(qrDoc.getId())
                                        .addOnSuccessListener(aVoid -> Log.d("qrCleanup", "Deleted QR code: " + qrDoc.getId()))
                                        .addOnFailureListener(e -> Log.e("qrCleanup", "Failed to delete QR code: " + qrDoc.getId(), e));
                            } else {
                                // Event exists, do nothing
                                Log.d("qrCleanup", "Event found for QR code: " + qrDoc.getId());
                            }
                        });

                    } else {
                        Log.d("qrCleanup", "Invalid or missing association for QR code: " + qrDoc.getId());
                    }
                });
            } else {
                Log.e("qrCleanup", "Failed to retrieve QR codes: ", task.getException());
            }
        });
    }



}
