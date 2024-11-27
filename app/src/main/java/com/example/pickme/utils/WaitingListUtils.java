package com.example.pickme.utils;

import com.example.pickme.models.Enums.EntrantStatus;
import com.example.pickme.models.Event;
import com.example.pickme.models.WaitingList;
import com.example.pickme.models.WaitingListEntrant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for managing waiting lists in the Firestore database.
 */
public class WaitingListUtils {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference eventsRef = db.collection("events");

    /**
     * Retrieves an event along with its waiting list.
     *
     * @param eventId The ID of the event.
     * @param onCompleteListener The listener to handle the completion of the task.
     */
    public void getEventWithWaitingList(String eventId, OnCompleteListener<WaitingList> onCompleteListener) {
        DocumentReference eventRef = eventsRef.document(eventId);
        CollectionReference waitingListRef = eventRef.collection("waitingList");

        eventRef.get().addOnCompleteListener(eventTask -> {
            if (eventTask.isSuccessful() && eventTask.getResult() != null) {
                Event event = eventTask.getResult().toObject(Event.class);

                if (event != null) {
                    waitingListRef.get().addOnCompleteListener(waitingListTask -> {
                        if (waitingListTask.isSuccessful() && waitingListTask.getResult() != null) {
                            int numEntrants = waitingListTask.getResult().size() - 1; // Exclude the placeholder document

                            WaitingList waitingList = new WaitingList();
                            waitingList.setEventId(event.getEventId());
                            waitingList.setMaxEntrants(event.getMaxEntrants());
                            waitingList.setMaxWinners(Integer.parseInt(event.getMaxWinners()));
                            waitingList.setNumEntrants(numEntrants);

                            onCompleteListener.onComplete(Tasks.forResult(waitingList));
                        } else {
                            onCompleteListener.onComplete(Tasks.forException(waitingListTask.getException()));
                        }
                    });
                } else {
                    onCompleteListener.onComplete(Tasks.forException(eventTask.getException()));
                }
            } else {
                onCompleteListener.onComplete(Tasks.forException(eventTask.getException()));
            }
        });
    }

    /**
     * Retrieves a specific entrant from the waiting list.
     *
     * @param eventId The ID of the event.
     * @param entrantId The ID of the entrant.
     * @param onCompleteListener The listener to handle the completion of the task.
     */
    public void getWaitingListEntrantByEntrantId(String eventId, String entrantId, OnCompleteListener<WaitingListEntrant> onCompleteListener) {
        eventsRef.document(eventId).collection("waitingList")
                .whereEqualTo("entrantId", entrantId)
                .get()
                .addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                WaitingListEntrant entrant = task.getResult().toObjects(WaitingListEntrant.class).get(0);
                onCompleteListener.onComplete(Tasks.forResult(entrant));
            } else {
                onCompleteListener.onComplete(Tasks.forException(task.getException()));
            }
        });
    }

    /**
     * Retrieves all entrants from the waiting list.
     *
     * @param eventId The ID of the event.
     * @param onCompleteListener The listener to handle the completion of the task.
     */
    public void getAllEntrantsFromWaitingList(String eventId, OnCompleteListener<List<WaitingListEntrant>> onCompleteListener) {
        eventsRef.document(eventId).collection("waitingList")
                .whereNotEqualTo(FieldPath.documentId(), "placeholder") // Exclude the placeholder document
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<WaitingListEntrant> entrants = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            WaitingListEntrant entrant = document.toObject(WaitingListEntrant.class);
                            if (entrant != null) {
                                entrants.add(entrant);
                            }
                        }
                        onCompleteListener.onComplete(Tasks.forResult(entrants));
                    } else {
                        onCompleteListener.onComplete(Tasks.forException(task.getException()));
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
        CollectionReference waitingListRef = eventsRef.document(eventId).collection("waitingList");

        waitingListRef.whereEqualTo("entrantId", waitingListEntrant.getEntrantId()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {

                // Entrant already exists, update the status to WAITING (they rejoin)
                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                DocumentReference entrantRef = waitingListRef.document(document.getId());
                entrantRef.update("entrantStatus", EntrantStatus.WAITING).addOnCompleteListener(onCompleteListener)
                        .addOnFailureListener(e -> System.err.println("Status update failed: " + e.getMessage()));
            } else if (task.isSuccessful() && (task.getResult() == null || task.getResult().isEmpty())) {

                // Entrant does not exist, add new document
                DocumentReference newEntrantRef = waitingListRef.document();
                waitingListEntrant.setWaitListEntrantId(newEntrantRef.getId());
                newEntrantRef.set(waitingListEntrant).addOnCompleteListener(onCompleteListener)
                        .addOnFailureListener(e -> System.err.println("Addition failed: " + e.getMessage()));
            } else {
                onCompleteListener.onComplete(Tasks.forException(task.getException()));
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
        eventsRef.document(eventId).collection("waitingList")
                .whereEqualTo("entrantId", entrantId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        DocumentReference entrantRef = eventsRef.document(eventId).collection("waitingList").document(document.getId());
                        entrantRef.set(waitingListEntrant)
                                .addOnCompleteListener(onCompleteListener)
                                .addOnFailureListener(e -> System.err.println("Update failed: " + e.getMessage()));
                    } else {
                        onCompleteListener.onComplete(Tasks.forException(task.getException()));
                    }
                });
    }

    /**
     * Removes an entrant from the waiting list.
     *
     * @param eventId The ID of the event.
     * @param entrantId The ID of the entrant.
     * @param onCompleteListener The listener to handle the completion of the task.
     */
    public void deleteEntrantFromWaitingList(String eventId, String entrantId, OnCompleteListener<Void> onCompleteListener) {
        eventsRef.document(eventId).collection("waitingList")
                .whereEqualTo("entrantId", entrantId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        DocumentReference entrantRef = eventsRef.document(eventId).collection("waitingList").document(document.getId());
                        entrantRef.delete()
                                .addOnCompleteListener(onCompleteListener)
                                .addOnFailureListener(e -> System.err.println("Deletion failed: " + e.getMessage()));
                    } else {
                        onCompleteListener.onComplete(Tasks.forException(task.getException()));
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
        CollectionReference waitingListRef = eventsRef.document(eventId).collection("waitingList");

        waitingListRef.whereEqualTo("entrantId", userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                DocumentReference entrantRef = waitingListRef.document(document.getId());

                entrantRef.update("entrantStatus", newStatus).addOnCompleteListener(updateTask -> {
                    if (updateTask.isSuccessful()) {
                        System.out.println("Entrant status updated successfully.");
                    } else {
                        System.err.println("Failed to update entrant status: " + updateTask.getException().getMessage());
                    }
                });
            } else {
                System.err.println("User is not an entrant in the waiting list.");
            }
        });
    }

    /**
     * Retrieves entrants from the waiting list based on their status.
     *
     * @param eventId The ID of the event.
     * @param status The status of the entrants to be retrieved.
     * @param onCompleteListener The listener to handle the completion of the task.
     */
    public void getEntrantsByStatus(String eventId, EntrantStatus status, OnCompleteListener<List<WaitingListEntrant>> onCompleteListener) {
        eventsRef.document(eventId).collection("waitingList")
                .whereNotEqualTo(FieldPath.documentId(), "placeholder") // Exclude the placeholder document
                .whereEqualTo("entrantStatus", status)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<WaitingListEntrant> entrants = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            WaitingListEntrant entrant = document.toObject(WaitingListEntrant.class);
                            if (entrant != null) {
                                entrants.add(entrant);
                            }
                        }
                        onCompleteListener.onComplete(Tasks.forResult(entrants));
                    } else {
                        onCompleteListener.onComplete(Tasks.forException(task.getException()));
                    }
                });
    }

    /**
     * Retrieves the number of entrants from the waiting list based on their status.
     *
     * @param eventId The ID of the event.
     * @param status The status of the entrants to be counted.
     * @param onCompleteListener The listener to handle the completion of the task.
     */
    public void getEntrantNumbersByStatus(String eventId, EntrantStatus status, OnCompleteListener<Integer> onCompleteListener) {
        eventsRef.document(eventId).collection("waitingList")
                .whereNotEqualTo(FieldPath.documentId(), "placeholder") // Exclude the placeholder document
                .whereEqualTo("entrantStatus", status)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        int count = task.getResult().size();
                        onCompleteListener.onComplete(Tasks.forResult(count));
                    } else {
                        onCompleteListener.onComplete(Tasks.forException(task.getException()));
                    }
                });
    }

    /**
     * Retrieves entrants from the waiting list who accepted their
     * invite (ie. status is ACCEPTED).
     *
     * @param eventId The ID of the event.
     * @param onCompleteListener The listener to handle the completion of the task.
     */
    public void getAcceptedEntrants(String eventId, OnCompleteListener<List<WaitingListEntrant>> onCompleteListener) {
        getEntrantsByStatus(eventId, EntrantStatus.ACCEPTED, onCompleteListener);
    }

    /**
     * Retrieves selected entrants from the waiting list who have not yet accepted or declined
     * their invite (ie. status is SELECTED).
     *
     * @param eventId The ID of the event.
     * @param onCompleteListener The listener to handle the completion of the task.
     */
    public void getSelectedEntrants(String eventId, OnCompleteListener<List<WaitingListEntrant>> onCompleteListener) {
        getEntrantsByStatus(eventId, EntrantStatus.SELECTED, onCompleteListener);
    }

    /**
     * Retrieves entrants from the waiting list who rejected their
     * invite (ie. status is REJECTED).
     *
     * @param eventId The ID of the event.
     * @param onCompleteListener The listener to handle the completion of the task.
     */
    public void getRejectedEntrants(String eventId, OnCompleteListener<List<WaitingListEntrant>> onCompleteListener) {
        getEntrantsByStatus(eventId, EntrantStatus.REJECTED, onCompleteListener);
    }

    /**
     * Retrieves entrants from the waiting list who cancelled their spot
     * (ie. status is CANCELLED).
     *
     * @param eventId The ID of the event.
     * @param onCompleteListener The listener to handle the completion of the task.
     */
    public void getCancelledEntrants(String eventId, OnCompleteListener<List<WaitingListEntrant>> onCompleteListener) {
        getEntrantsByStatus(eventId, EntrantStatus.CANCELLED, onCompleteListener);
    }

    /**
     * Retrieves entrants from the waiting list who are still waiting to be selected
     * (ie. status is WAITING).
     *
     * @param eventId The ID of the event.
     * @param onCompleteListener The listener to handle the completion of the task.
     */
    public void getWaitingEntrants(String eventId, OnCompleteListener<List<WaitingListEntrant>> onCompleteListener) {
        getEntrantsByStatus(eventId, EntrantStatus.WAITING, onCompleteListener);
    }

    /**
     * Retrieves the selected entrants (with EntrantStatus.SELECTED) and changes their status to EntrantStatus.CANCELLED.
     *
     * @param eventId The ID of the event.
     * @param onCompleteListener The listener to handle the completion of the task.
     */
    public void cancelPendingEntrants(String eventId, OnCompleteListener<Void> onCompleteListener) {
        getSelectedEntrants(eventId, selectedEntrantsTask -> {
            if (selectedEntrantsTask.isSuccessful() && selectedEntrantsTask.getResult() != null) {
                List<WaitingListEntrant> pendingEntrants = selectedEntrantsTask.getResult();
                List<Task<Void>> updateTasks = new ArrayList<>();

                for (WaitingListEntrant entrant : pendingEntrants) {
                    DocumentReference entrantRef = eventsRef.document(eventId).collection("waitingList").document(entrant.getWaitListEntrantId());
                    Task<Void> updateTask = entrantRef.update("entrantStatus", EntrantStatus.CANCELLED);  // Update the entrant's status to CANCELLED
                    updateTasks.add(updateTask);
                }

                Tasks.whenAll(updateTasks).addOnCompleteListener(onCompleteListener);
            } else {
                onCompleteListener.onComplete(Tasks.forException(selectedEntrantsTask.getException()));
            }
        });
    }
}