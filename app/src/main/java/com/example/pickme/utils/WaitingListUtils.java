package com.example.pickme.utils;

import com.example.pickme.models.Enums.EntrantStatus;
import com.example.pickme.models.Event;
import com.example.pickme.models.WaitingListEntrant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for managing waiting lists in the Firestore database.
 */
public class WaitingListUtils {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Retrieves a specific entrant from the waiting list.
     *
     * @param eventId The ID of the event.
     * @param entrantId The ID of the entrant.
     * @param onCompleteListener The listener to handle the completion of the task.
     */
    public void getWaitingListEntrantByEntrantId(String eventId, String entrantId, OnCompleteListener<WaitingListEntrant> onCompleteListener) {
        db.collection("events").document(eventId).get().addOnCompleteListener(eventTask -> {
            if (eventTask.isSuccessful() && eventTask.getResult() != null) {
                Event event = eventTask.getResult().toObject(Event.class);
                if (event != null) {
                    for (WaitingListEntrant entrant : event.getWaitingList()) {
                        if (entrant.getEntrantId().equals(entrantId)) {
                            onCompleteListener.onComplete(Tasks.forResult(entrant));
                            return;
                        }
                    }
                    onCompleteListener.onComplete(Tasks.forException(new Exception("Entrant not found")));
                } else {
                    onCompleteListener.onComplete(Tasks.forException(new Exception("Event not found")));
                }
            } else {
                onCompleteListener.onComplete(Tasks.forException(eventTask.getException()));
            }
        });
    }

    /**
     * Adds a new entrant to the waiting list.
     *
     * @param eventId The ID of the event.
     * @param waitingListEntrant The entrant to be added.
     * @param onCompleteListener The listener to handle the completion of the task.
     */
    public void addEntrantToWaitingList(String eventId, WaitingListEntrant waitingListEntrant, OnCompleteListener<Void> onCompleteListener) {
        db.collection("events").document(eventId).get().addOnCompleteListener(eventTask -> {
            if (eventTask.isSuccessful() && eventTask.getResult() != null) {
                Event event = eventTask.getResult().toObject(Event.class);
                if (event != null) {
                    List<WaitingListEntrant> waitingList = event.getWaitingList();
                    waitingList.add(waitingListEntrant);
                    event.setWaitingList(new ArrayList<>(waitingList));
                    db.collection("events").document(eventId).set(event).addOnCompleteListener(onCompleteListener);
                } else {
                    onCompleteListener.onComplete(Tasks.forException(new Exception("Event not found")));
                }
            } else {
                onCompleteListener.onComplete(Tasks.forException(eventTask.getException()));
            }
        });
    }

    /**
     * Edits an existing entrant in the waiting list.
     *
     * @param eventId The ID of the event.
     * @param entrantId The ID of the entrant.
     * @param waitingListEntrant The updated entrant information.
     * @param onCompleteListener The listener to handle the completion of the task.
     */
    public void editEntrantInWaitingList(String eventId, String entrantId, WaitingListEntrant waitingListEntrant, OnCompleteListener<Void> onCompleteListener) {
        db.collection("events").document(eventId).get().addOnCompleteListener(eventTask -> {
            if (eventTask.isSuccessful() && eventTask.getResult() != null) {
                Event event = eventTask.getResult().toObject(Event.class);
                if (event != null) {
                    List<WaitingListEntrant> waitingList = event.getWaitingList();
                    for (int i = 0; i < waitingList.size(); i++) {
                        if (waitingList.get(i).getEntrantId().equals(entrantId)) {
                            waitingList.set(i, waitingListEntrant);
                            event.setWaitingList(new ArrayList<>(waitingList));
                            db.collection("events").document(eventId).set(event).addOnCompleteListener(onCompleteListener);
                            return;
                        }
                    }
                    onCompleteListener.onComplete(Tasks.forException(new Exception("Entrant not found")));
                } else {
                    onCompleteListener.onComplete(Tasks.forException(new Exception("Event not found")));
                }
            } else {
                onCompleteListener.onComplete(Tasks.forException(eventTask.getException()));
            }
        });
    }

    /**
     * Updates the entrant status of a waiting list entrant.
     *
     * @param eventId The ID of the event.
     * @param userId The ID of the user.
     * @param newStatus The new status to be set for the entrant.
     */
    public void updateEntrantStatus(String eventId, String userId, EntrantStatus newStatus) {
        db.collection("events").document(eventId).get().addOnCompleteListener(eventTask -> {
            if (eventTask.isSuccessful() && eventTask.getResult() != null) {
                Event event = eventTask.getResult().toObject(Event.class);
                if (event != null) {
                    List<WaitingListEntrant> waitingList = event.getWaitingList();
                    for (WaitingListEntrant entrant : waitingList) {
                        if (entrant.getEntrantId().equals(userId)) {
                            entrant.setStatus(newStatus);
                            break;
                        }
                    }
                    event.setWaitingList(new ArrayList<>(waitingList));
                    db.collection("events").document(eventId).set(event).addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            System.out.println("Entrant status updated successfully.");
                        } else {
                            System.err.println("Failed to update entrant status: " + updateTask.getException().getMessage());
                        }
                    });
                } else {
                    System.err.println("Event not found.");
                }
            } else {
                System.err.println("Failed to retrieve event: " + eventTask.getException().getMessage());
            }
        });
    }
}