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
        UserRepository userRepository = UserRepository.getInstance();

        // Make sure user actually exists first
        userRepository.checkUserExists(entrantId, userTask -> {
            if (userTask.isSuccessful() && userTask.getResult()) {

                // If user exists, check that event also exists
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
            } else {
                onCompleteListener.onComplete(Tasks.forException(new Exception("User not found")));
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
        UserRepository userRepository = UserRepository.getInstance();

        // Make sure user actually exists first
        userRepository.checkUserExists(waitingListEntrant.getEntrantId(), userTask -> {
            if (userTask.isSuccessful() && userTask.getResult()) {

                // If user exists, check that event also exists
                db.collection("events").document(eventId).get().addOnCompleteListener(eventTask -> {
                    if (eventTask.isSuccessful() && eventTask.getResult() != null) {
                        Event event = eventTask.getResult().toObject(Event.class);
                        if (event != null && event.getEventId() != null) {
                            List<WaitingListEntrant> waitingList = event.getWaitingList();
                            waitingList.add(waitingListEntrant);
                            event.setWaitingList(new ArrayList<>(waitingList));
                            db.collection("events").document(eventId).set(event).addOnCompleteListener(onCompleteListener);
                        } else {
                            onCompleteListener.onComplete(Tasks.forException(new Exception("Event not found or has been deleted")));
                        }
                    } else {
                        onCompleteListener.onComplete(Tasks.forException(eventTask.getException()));
                    }
                });
            } else {
                onCompleteListener.onComplete(Tasks.forException(new Exception("User not found")));
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
        UserRepository userRepository = UserRepository.getInstance();

        // Make sure user actually exists first
        userRepository.checkUserExists(entrantId, userTask -> {
            if (userTask.isSuccessful() && userTask.getResult()) {

                // If user exists, check that event also exists
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
            } else {
                onCompleteListener.onComplete(Tasks.forException(new Exception("User not found")));
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
        UserRepository userRepository = UserRepository.getInstance();

        // Make sure user actually exists first
        userRepository.checkUserExists(userId, userTask -> {
            if (userTask.isSuccessful() && userTask.getResult()) {

                // If user exists, check that event also exists
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
            } else {
                System.err.println("User not found.");
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
                    UserRepository userRepository = UserRepository.getInstance();
                    List<WaitingListEntrant> waitingList = event.getWaitingList();
                    List<WaitingListEntrant> validEntrants = new ArrayList<>();

                    for (WaitingListEntrant entrant : waitingList) {
                        userRepository.checkUserExists(entrant.getEntrantId(), userTask -> {
                            if (userTask.isSuccessful() && userTask.getResult()) {
                                validEntrants.add(entrant);
                            }
                        });
                    }

                    int entrantsCount = (int) validEntrants.stream().filter(entrant -> entrant.getStatus() == status).count();
                    onCompleteListener.onComplete(Tasks.forResult(entrantsCount));
                } else {
                    onCompleteListener.onComplete(Tasks.forException(new Exception("Event not found in Firestore")));
                }
            } else {
                onCompleteListener.onComplete(Tasks.forException(new Exception("Failed to retrieve event from Firestore")));
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
                    List<WaitingListEntrant> waitingEntrants = new ArrayList<>();
                    for (WaitingListEntrant entrant : event.getWaitingList()) {
                        checkUserExists(entrant.getEntrantId(), userTask -> {
                            if (userTask.isSuccessful() && userTask.getResult()) {
                                if (entrant.getStatus() == status) {
                                    waitingEntrants.add(entrant);
                                }
                            }
                        });
                    }
                    onCompleteListener.onComplete(Tasks.forResult(waitingEntrants));
                } else {
                    onCompleteListener.onComplete(Tasks.forException(new Exception("Event not found")));
                }
            } else {
                onCompleteListener.onComplete(Tasks.forException(eventTask.getException()));
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
                    List<WaitingListEntrant> waitingList = event.getWaitingList();
                    for (WaitingListEntrant entrant : waitingList) {
                        checkUserExists(entrant.getEntrantId(), userTask -> {
                            if (userTask.isSuccessful() && userTask.getResult()) {
                                if (entrant.getStatus() == EntrantStatus.REJECTED) {
                                    entrant.setStatus(EntrantStatus.CANCELLED);
                                }
                            }
                        });
                    }
                    event.setWaitingList(new ArrayList<>(waitingList));
                    db.collection("events").document(eventId).set(event).addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            System.out.println("Rejected invites cancelled successfully.");
                        } else {
                            System.err.println("Failed to cancel rejected invites: " + updateTask.getException().getMessage());
                        }
                    });
                } else {
                    System.err.println("Failed to retrieve event: " + eventTask.getException().getMessage());
                }
            }
        });
    }

    public void cleanupWaitingList(String eventId) {
        db.collection("events").document(eventId).get().addOnCompleteListener(eventTask -> {
            if (eventTask.isSuccessful() && eventTask.getResult() != null) {
                Event event = eventTask.getResult().toObject(Event.class);
                if (event != null) {
                    List<WaitingListEntrant> waitingList = event.getWaitingList();
                    ArrayList<WaitingListEntrant> updatedList = new ArrayList<>();
                    for (WaitingListEntrant entrant : waitingList) {
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