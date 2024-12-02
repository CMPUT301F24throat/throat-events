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
 * Utility class to fetch the {@link Event} details associated with a given QR ID.
 * This class interacts with the repositories to retrieve QR and Event information
 * and provides callbacks for the results.
 */
public class EventFetcher {

    private static final String TAG = "EventFetcher";
    private final EventRepository eventRepository;

    /**
     * Constructs an instance of EventFetcher with the specified {@link EventRepository}.
     *
     * @param eventRepository The repository to fetch event details.
     */
    public EventFetcher(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * Fetches the {@link Event} object associated with the given QR ID.
     * The method first retrieves the QR document to extract the associated event ID
     * and then fetches the event details using the extracted event ID.
     *
     * @param qrID     The QR ID to look up in the database. Must not be null or empty.
     * @param callback Callback interface to handle the result or error.
     */
    public void getEventByEventId(String qrID, EventCallback callback) {
        if (qrID == null || qrID.isEmpty()) {
            callback.onError("Invalid QR ID");
            return;
        }

        Log.d(TAG, "Fetching QR document for QR ID: " + qrID);

        // Use the repository method to fetch the QR document by QR ID
        QrRepository.getInstance().readQRByID(qrID).addOnCompleteListener(task -> {
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
     * Fetches the {@link Event} object associated with the given event ID.
     *
     * @param eventID  The event ID to look up in the database. Must not be null or empty.
     * @param callback Callback interface to handle the result or error.
     */
    private void fetchEventById(String eventID, EventCallback callback) {
        Log.d(TAG, "Fetching event details for Event ID: " + eventID);

        eventRepository.getEventById(eventID, new OnCompleteListener<Event>() {
            @Override
            public void onComplete(@NonNull Task<Event> task) {
                if (task.isSuccessful() && task.getResult() != null) {
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
     * Callback interface to handle the result or error while fetching the {@link Event}.
     */
    public interface EventCallback {

        /**
         * Called when the {@link Event} object is successfully fetched.
         *
         * @param event The {@link Event} object containing the event details.
         */
        void onEventFetched(Event event);

        /**
         * Called when an error occurs during the fetch operation.
         *
         * @param errorMessage A message describing the error.
         */
        void onError(String errorMessage);
    }
}
