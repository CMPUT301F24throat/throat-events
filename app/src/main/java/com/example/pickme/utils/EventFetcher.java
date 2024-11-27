package com.example.pickme.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pickme.models.Event;
import com.example.pickme.repositories.EventRepository;
import com.example.pickme.repositories.QrRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * Utility class to fetch the Event details associated with a given QR ID.
 */
public class EventFetcher {

    private static final String TAG = "EventFetcher";
    private final EventRepository eventRepository;

    public EventFetcher(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * Fetches the Event object associated with the given QR ID.
     *
     * @param qrID     The QR ID to look up in the database
     * @param callback Callback to handle the result
     */
    public void getEventByEventId(String qrID, EventCallback callback) {
        if (qrID == null || qrID.isEmpty()) {
            callback.onError("Invalid QR ID");
            return;
        }

        Log.d(TAG, "Fetching QR document for QR ID: " + qrID);

        // Use the repository method to fetch the QR document by QR ID
        new QrRepository().readQRByID(qrID).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                DocumentSnapshot document = task.getResult().getDocuments().get(0);

                // Log the raw QR document data for debugging
                Log.d(TAG, "Raw QR Code Document: " + document.getData());

                // Extract the QR reference and remove "/events/"
                String qrReference = document.getString("qrAssociation");
                if (qrReference != null && qrReference.startsWith("/events/")) {
                    String eventID = qrReference.replace("/events/", "");
                    Log.d(TAG, "Extracted Event ID: " + eventID);

                    // Use the event ID to fetch the event details
                    fetchEventById(eventID, callback);
                } else {
                    Log.e(TAG, "Invalid or missing QR reference");
                    callback.onError("Invalid or missing QR reference");
                }
            } else {
                Log.e(TAG, "Error fetching QR document", task.getException());
                callback.onError("Error fetching QR document");
            }
        });
    }

    /**
     * Fetches the Event object associated with the given event ID.
     *
     * @param eventID  The event ID to look up in the database
     * @param callback Callback to handle the result
     */
    private void fetchEventById(String eventID, EventCallback callback) {
        Log.d(TAG, "Fetching event details for Event ID: " + eventID);

        eventRepository.getEventById(eventID, new OnCompleteListener<Event>() {
            @Override
            public void onComplete(@NonNull Task<Event> task) {
                if (task.isSuccessful() && task.getResult() != null) {
//                    DocumentSnapshot document = task.getResult();
//
//                    // Log the raw event document data for debugging
//                    Log.d(TAG, "Raw Event Document: " + document.getData());

                    // Convert Firestore document to Event object
                    Event event = task.getResult();
                    if (event != null) {
                        Log.d(TAG, "Event fetched: " + event.getEventTitle());
                        callback.onEventFetched(event);
                    } else {
                        Log.e(TAG, "Failed to convert Firestore document to Event object");
                        callback.onError("Event details not found");
                    }
                } else {
                    Log.e(TAG, "Error fetching event details", task.getException());
                    callback.onError("Error fetching event details");
                }
            }
        });
    }

    /**
     * Callback interface for fetching the Event object.
     */
    public interface EventCallback {
        void onEventFetched(Event event);

        void onError(String errorMessage);
    }
}
