package com.example.pickme.utils;

import com.example.pickme.models.Enums.EntrantStatus;
import com.example.pickme.models.Event;
import com.example.pickme.models.WaitingListEntrant;
import com.example.pickme.repositories.UserRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Utility class for managing waiting lists in the Firestore database.
 */
public class WaitingListUtils {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final UserRepository userRepository = UserRepository.getInstance();

    /**
     * Retrieves a specific entrant from the waiting list.
     *
     * @param eventId The ID of the event.
     * @param entrantId The ID of the entrant.
     * @param onCompleteListener The listener to handle the completion of the task.
     */
    public void getWaitingListEntrantByEntrantId(String eventId, String entrantId, OnCompleteListener<WaitingListEntrant> onCompleteListener) {
        checkUserAndEventExist(eventId, entrantId, (userExists, event) -> {
            if (userExists && event != null) {
                for (WaitingListEntrant entrant : event.getWaitingList()) {
                    if (entrant.getEntrantId().equals(entrantId)) {
                        onCompleteListener.onComplete(Tasks.forResult(entrant));
                        return;
                    }
                }
                onCompleteListener.onComplete(Tasks.forException(new Exception("Entrant not found")));
            } else {
                onCompleteListener.onComplete(Tasks.forException(new Exception("User or Event not found")));
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
        checkUserAndEventExist(eventId, waitingListEntrant.getEntrantId(), (userExists, event) -> {
            if (userExists && event != null) {
                List<WaitingListEntrant> waitingList = event.getWaitingList();
                waitingList.add(waitingListEntrant);
                event.setWaitingList(new ArrayList<>(waitingList));
                db.collection("events").document(eventId).set(event).addOnCompleteListener(onCompleteListener);
            } else {
                onCompleteListener.onComplete(Tasks.forException(new Exception("User or Event not found")));
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
        checkUserAndEventExist(eventId, entrantId, (userExists, event) -> {
            if (userExists && event != null) {
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
                onCompleteListener.onComplete(Tasks.forException(new Exception("User or Event not found")));
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
        checkUserAndEventExist(eventId, userId, (userExists, event) -> {
            if (userExists && event != null) {
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
                System.err.println("User or Event not found.");
            }
        });
    }

    /**
     * Retrieves the number of entrants in the waiting list with a specific status.
     *
     * @param eventId The ID of the event.
     * @param status The status of the entrants to be counted.
     * @param onCompleteListener The listener to handle the completion of the task.
     */
    public void getEntrantsCountByStatus(String eventId, EntrantStatus status, OnCompleteListener<Integer> onCompleteListener) {
        db.collection("events").document(eventId).get().addOnCompleteListener(eventTask -> {
            if (eventTask.isSuccessful() && eventTask.getResult() != null) {
                Event event = eventTask.getResult().toObject(Event.class);
                if (event != null) {
                    long count = event.getWaitingList().stream().filter(entrant -> entrant.getStatus() == status).count();
                    onCompleteListener.onComplete(Tasks.forResult((int) count));
                } else {
                    onCompleteListener.onComplete(Tasks.forException(new Exception("Event not found")));
                }
            } else {
                onCompleteListener.onComplete(Tasks.forException(new Exception("Failed to retrieve event")));
            }
        });
    }

    /**
     * Retrieves the entrants in the waiting list with a specific status.
     *
     * @param eventId The ID of the event.
     * @param status The status of the entrants to be retrieved.
     * @param onCompleteListener The listener to handle the completion of the task.
     */
    public void getWaitingListEntrantsByStatus(String eventId, EntrantStatus status, OnCompleteListener<List<WaitingListEntrant>> onCompleteListener) {
        db.collection("events").document(eventId).get().addOnCompleteListener(eventTask -> {
            if (eventTask.isSuccessful() && eventTask.getResult() != null) {
                Event event = eventTask.getResult().toObject(Event.class);
                if (event != null) {
                    List<WaitingListEntrant> filteredList = new ArrayList<>();
                    for (WaitingListEntrant entrant : event.getWaitingList()) {
                        if (entrant.getStatus() == status) {
                            filteredList.add(entrant);
                        }
                    }
                    onCompleteListener.onComplete(Tasks.forResult(filteredList));
                } else {
                    onCompleteListener.onComplete(Tasks.forException(new Exception("Event not found")));
                }
            } else {
                onCompleteListener.onComplete(Tasks.forException(new Exception("Failed to retrieve event")));
            }
        });
    }

    /**
     * Cancels all rejected invites for an event - sets their status to EntrantStatus.CANCELLED.
     *
     * @param eventId The ID of the event.
     */
    public void cancelRejectedInvites(String eventId) {
        db.collection("events").document(eventId).get().addOnCompleteListener(eventTask -> {
            if (eventTask.isSuccessful() && eventTask.getResult() != null) {
                Event event = eventTask.getResult().toObject(Event.class);
                if (event != null) {
                    for (WaitingListEntrant entrant : event.getWaitingList()) {
                        if (entrant.getStatus() == EntrantStatus.REJECTED) {
                            entrant.setStatus(EntrantStatus.CANCELLED);
                        }
                    }
                    db.collection("events").document(eventId).set(event).addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            System.out.println("Rejected invites cancelled successfully.");
                        } else {
                            System.err.println("Failed to cancel rejected invites: " + updateTask.getException().getMessage());
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
     * Cleans up the waiting list by removing any entrants that no longer exist in the database.
     *
     * @param eventId The ID of the event.
     */
    public void cleanupWaitingList(String eventId) {
        db.collection("events").document(eventId).get().addOnCompleteListener(eventTask -> {
            if (eventTask.isSuccessful() && eventTask.getResult() != null) {
                Event event = eventTask.getResult().toObject(Event.class);
                if (event != null) {
                    ArrayList<WaitingListEntrant> updatedList = new ArrayList<>();
                    for (WaitingListEntrant entrant : event.getWaitingList()) {
                        checkUserExists(entrant.getEntrantId(), userTask -> {
                            if (userTask.isSuccessful() && userTask.getResult()) {
                                updatedList.add(entrant);
                            }
                        });
                    }
                    event.setWaitingList(updatedList);
                    db.collection("events").document(eventId).set(event).addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            System.out.println("Waiting list cleaned up successfully.");
                        } else {
                            System.err.println("Failed to clean up waiting list: " + updateTask.getException().getMessage());
                        }
                    });
                } else {
                    System.err.println("Failed to retrieve event: " + eventTask.getException().getMessage());
                }
            }
        });
    }

    /**
     * Checks if a user and event exist in the database.
     *
     * @param eventId The ID of the event.
     * @param userId The ID of the user.
     * @param callback The callback to handle the result.
     */
    private void checkUserAndEventExist(String eventId, String userId, BiConsumer<Boolean, Event> callback) {
        userRepository.checkUserExists(userId, userTask -> {
            if (userTask.isSuccessful() && userTask.getResult()) {
                db.collection("events").document(eventId).get().addOnCompleteListener(eventTask -> {
                    if (eventTask.isSuccessful() && eventTask.getResult() != null) {
                        Event event = eventTask.getResult().toObject(Event.class);
                        callback.accept(true, event);
                    } else {
                        callback.accept(true, null);
                    }
                });
            } else {
                callback.accept(false, null);
            }
        });
    }

    /**
     * Checks if a user exists in the database.
     *
     * @param userId The ID of the user.
     * @param onCompleteListener The listener to handle the completion of the task.
     */
    private void checkUserExists(String userId, OnCompleteListener<Boolean> onCompleteListener) {
        db.collection("users").document(userId).get().addOnCompleteListener(userTask -> {
            if (userTask.isSuccessful() && userTask.getResult() != null) {
                onCompleteListener.onComplete(Tasks.forResult(true));
            } else {
                onCompleteListener.onComplete(Tasks.forResult(false));
            }
        });
    }
}