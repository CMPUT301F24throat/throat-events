package com.example.pickme.utils;

import com.example.pickme.models.Enums.EntrantStatus;
import com.example.pickme.models.Event;
import com.example.pickme.models.WaitingListEntrant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
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
     * Retrieves an event's waiting list and creates a WaitingList object.
     *
     * @param eventId The ID of the event.
     * @param onCompleteListener The listener to handle the completion of the task.
     */
    public void getEventWaitingList(String eventId, OnCompleteListener<List<WaitingListEntrant>> onCompleteListener) {
        DocumentReference eventRef = eventsRef.document(eventId);

        eventRef.get().addOnCompleteListener(eventTask -> {
            if (eventTask.isSuccessful() && eventTask.getResult() != null) {
                Event event = eventTask.getResult().toObject(Event.class);

                if (event != null) {
                    List<WaitingListEntrant> waitingList = event.getWaitingList();
                    onCompleteListener.onComplete(Tasks.forResult(waitingList));
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
        DocumentReference eventRef = eventsRef.document(eventId);

        eventRef.get().addOnCompleteListener(eventTask -> {
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
        DocumentReference eventRef = eventsRef.document(eventId);

        eventRef.get().addOnCompleteListener(eventTask -> {
            if (eventTask.isSuccessful() && eventTask.getResult() != null) {
                Event event = eventTask.getResult().toObject(Event.class);
                if (event != null) {
                    List<WaitingListEntrant> waitingList = event.getWaitingList();
                    waitingList.add(waitingListEntrant);
                    event.setWaitingList(new ArrayList<>(waitingList));
                    eventRef.set(event).addOnCompleteListener(onCompleteListener);
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
        DocumentReference eventRef = eventsRef.document(eventId);

        eventRef.get().addOnCompleteListener(eventTask -> {
            if (eventTask.isSuccessful() && eventTask.getResult() != null) {
                Event event = eventTask.getResult().toObject(Event.class);
                if (event != null) {
                    List<WaitingListEntrant> waitingList = event.getWaitingList();
                    for (int i = 0; i < waitingList.size(); i++) {
                        if (waitingList.get(i).getEntrantId().equals(entrantId)) {
                            waitingList.set(i, waitingListEntrant);
                            event.setWaitingList(new ArrayList<>(waitingList));
                            eventRef.set(event).addOnCompleteListener(onCompleteListener);
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
     * Removes an entrant from the waiting list.
     *
     * @param eventId The ID of the event.
     * @param entrantId The ID of the entrant.
     * @param onCompleteListener The listener to handle the completion of the task.
     */
    public void deleteEntrantFromWaitingList(String eventId, String entrantId, OnCompleteListener<Void> onCompleteListener) {
        DocumentReference eventRef = eventsRef.document(eventId);

        eventRef.get().addOnCompleteListener(eventTask -> {
            if (eventTask.isSuccessful() && eventTask.getResult() != null) {
                Event event = eventTask.getResult().toObject(Event.class);
                if (event != null) {
                    List<WaitingListEntrant> waitingList = event.getWaitingList();
                    waitingList.removeIf(entrant -> entrant.getEntrantId().equals(entrantId));
                    event.setWaitingList(new ArrayList<>(waitingList));
                    eventRef.set(event).addOnCompleteListener(onCompleteListener);
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
        DocumentReference eventRef = eventsRef.document(eventId);

        eventRef.get().addOnCompleteListener(eventTask -> {
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
                    eventRef.set(event).addOnCompleteListener(updateTask -> {
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

    /**
     * Retrieves entrants from the waiting list based on their status.
     *
     * @param eventId The ID of the event.
     * @param status The status of the entrants to be retrieved.
     * @param onCompleteListener The listener to handle the completion of the task.
     */
    public void getEntrantsByStatus(String eventId, EntrantStatus status, OnCompleteListener<List<WaitingListEntrant>> onCompleteListener) {
        DocumentReference eventRef = eventsRef.document(eventId);

        eventRef.get().addOnCompleteListener(eventTask -> {
            if (eventTask.isSuccessful() && eventTask.getResult() != null) {
                Event event = eventTask.getResult().toObject(Event.class);
                if (event != null) {
                    List<WaitingListEntrant> entrants = new ArrayList<>();
                    for (WaitingListEntrant entrant : event.getWaitingList()) {
                        if (entrant.getStatus() == status) {
                            entrants.add(entrant);
                        }
                    }
                    onCompleteListener.onComplete(Tasks.forResult(entrants));
                } else {
                    onCompleteListener.onComplete(Tasks.forException(new Exception("Event not found")));
                }
            } else {
                onCompleteListener.onComplete(Tasks.forException(eventTask.getException()));
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
        getEntrantsByStatus(eventId, status, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                onCompleteListener.onComplete(Tasks.forResult(task.getResult().size()));
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
                DocumentReference eventRef = eventsRef.document(eventId);

                eventRef.get().addOnCompleteListener(eventTask -> {
                    if (eventTask.isSuccessful() && eventTask.getResult() != null) {
                        Event event = eventTask.getResult().toObject(Event.class);
                        if (event != null) {
                            List<WaitingListEntrant> waitingList = event.getWaitingList();
                            for (WaitingListEntrant entrant : pendingEntrants) {
                                for (WaitingListEntrant wlEntrant : waitingList) {
                                    if (wlEntrant.getEntrantId().equals(entrant.getEntrantId())) {
                                        wlEntrant.setStatus(EntrantStatus.CANCELLED);
                                        break;
                                    }
                                }
                            }
                            event.setWaitingList(new ArrayList<>(waitingList));
                            eventRef.set(event).addOnCompleteListener(onCompleteListener);
                        } else {
                            onCompleteListener.onComplete(Tasks.forException(new Exception("Event not found")));
                        }
                    } else {
                        onCompleteListener.onComplete(Tasks.forException(eventTask.getException()));
                    }
                });
            } else {
                onCompleteListener.onComplete(Tasks.forException(selectedEntrantsTask.getException()));
            }
        });
    }
}