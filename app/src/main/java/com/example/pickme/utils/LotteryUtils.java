package com.example.pickme.utils;

import com.example.pickme.models.Event;
import com.example.pickme.models.WaitingListEntrant;
import com.example.pickme.repositories.EventRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for running a lottery on an event's waiting list.
 */
public class LotteryUtils {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final WaitingListUtils waitingListUtils = new WaitingListUtils();
    private final EventRepository eventRepository = new EventRepository();

    /**
     * Runs the lottery for the specified event.
     *
     * @param eventId The ID of the event for which the lottery is to be run.
     * @param onCompleteListener The listener to be called upon completion of the lottery process.
     */
    public void runLottery(String eventId, OnCompleteListener<List<String>> onCompleteListener) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("events").document(eventId).get().addOnCompleteListener(eventTask -> {
            if (eventTask.isSuccessful() && eventTask.getResult() != null) {
                Event event = eventTask.getResult().toObject(Event.class);

                if (event != null && event.getOrganizerId().equals(currentUserId) && !eventRepository.hasEventPassed(event)) {
                    isMaxEntrantsReached(event, eventId, maxEntrantsTask -> {
                        if (maxEntrantsTask.isSuccessful() && maxEntrantsTask.getResult() != null && !maxEntrantsTask.getResult()) {
                            getNumToDraw(event, eventId, numToDrawTask -> {
                                if (numToDrawTask.isSuccessful() && numToDrawTask.getResult() != null) {
                                    int numToDraw = numToDrawTask.getResult();

                                    if (numToDraw > 0) {
                                        waitingListUtils.getWaitingEntrants(eventId, waitingEntrantsTask -> {
                                            if (waitingEntrantsTask.isSuccessful() && waitingEntrantsTask.getResult() != null) {
                                                List<WaitingListEntrant> waitingEntrants = waitingEntrantsTask.getResult();
                                                Collections.shuffle(waitingEntrants);

                                                List<String> selectedUserIds = new ArrayList<>();
                                                for (int i = 0; i < Math.min(numToDraw, waitingEntrants.size()); i++) {
                                                    selectedUserIds.add(waitingEntrants.get(i).getEntrantId());
                                                }

                                                onCompleteListener.onComplete(Tasks.forResult(selectedUserIds));
                                            } else {
                                                onCompleteListener.onComplete(Tasks.forException(waitingEntrantsTask.getException()));
                                            }
                                        });
                                    } else {
                                        db.collection("events").document(eventId).set(event).addOnCompleteListener(statusUpdateTask -> {
                                            if (statusUpdateTask.isSuccessful()) {
                                                onCompleteListener.onComplete(Tasks.forResult(new ArrayList<>()));
                                            } else {
                                                onCompleteListener.onComplete(Tasks.forException(statusUpdateTask.getException()));
                                            }
                                        });
                                    }
                                } else {
                                    onCompleteListener.onComplete(Tasks.forException(numToDrawTask.getException()));
                                }
                            });
                        } else {
                            onCompleteListener.onComplete(Tasks.forException(new Exception("Maximum number of entrants reached or error occurred")));
                        }
                    });
                } else {
                    onCompleteListener.onComplete(Tasks.forException(new Exception("User is not the organizer of the event or event has passed")));
                }
            } else {
                onCompleteListener.onComplete(Tasks.forException(eventTask.getException()));
            }
        });
    }

    /**
     * Checks if the maximum number of entrants has been reached.
     *
     * @param event The event to check.
     * @param eventId The ID of the event.
     * @param onCompleteListener The listener to handle the completion of the task.
     */
    private void isMaxEntrantsReached(Event event, String eventId, OnCompleteListener<Boolean> onCompleteListener) {
        waitingListUtils.getPendingEntrants(eventId, pendingEntrantsTask -> {
            if (pendingEntrantsTask.isSuccessful() && pendingEntrantsTask.getResult() != null) {
                List<WaitingListEntrant> pendingEntrants = pendingEntrantsTask.getResult();
                boolean isMaxReached = event.getMaxEntrants() != null && pendingEntrants.size() >= event.getMaxEntrants();
                onCompleteListener.onComplete(Tasks.forResult(isMaxReached));
            } else {
                onCompleteListener.onComplete(Tasks.forException(pendingEntrantsTask.getException()));
            }
        });
    }

    /**
     * Calculates the number of entrants to draw in the lottery.
     *
     * @param event The event for which the lottery is being run.
     * @param eventId The ID of the event.
     * @param onCompleteListener The listener to handle the completion of the task.
     */
    private void getNumToDraw(Event event, String eventId, OnCompleteListener<Integer> onCompleteListener) {
        waitingListUtils.getAcceptedEntrants(eventId, acceptedEntrantsTask -> {
            if (acceptedEntrantsTask.isSuccessful() && acceptedEntrantsTask.getResult() != null) {
                List<WaitingListEntrant> acceptedEntrants = acceptedEntrantsTask.getResult();
                int numAcceptedEntrants = acceptedEntrants.size();
                int numWinners = Integer.parseInt(event.getMaxWinners());

                waitingListUtils.getWaitingEntrants(eventId, waitingEntrantsTask -> {
                    if (waitingEntrantsTask.isSuccessful() && waitingEntrantsTask.getResult() != null) {
                        List<WaitingListEntrant> waitingEntrants = waitingEntrantsTask.getResult();
                        int numToDraw = determineNumToDraw(waitingEntrants, numAcceptedEntrants, numWinners);

                        onCompleteListener.onComplete(Tasks.forResult(numToDraw));
                    } else {
                        onCompleteListener.onComplete(Tasks.forException(waitingEntrantsTask.getException()));
                    }
                });
            } else {
                onCompleteListener.onComplete(Tasks.forException(acceptedEntrantsTask.getException()));
            }
        });
    }

    /**
     * Determines the number of entrants to draw in the lottery.
     *
     * @param waitingEntrants The list of entrants currently waiting.
     * @param numAcceptedEntrants The number of entrants that have already been accepted.
     * @param numWinners The maximum number of winners allowed.
     * @return The number of entrants to draw.
     */
    private static int determineNumToDraw(List<WaitingListEntrant> waitingEntrants, int numAcceptedEntrants, int numWinners) {
        int numWaitingEntrants = waitingEntrants.size();
        int numToDraw;

        if (numAcceptedEntrants == numWinners) {
            numToDraw = 0;  // If num of accepted entrants is equal to num of winners, no need to draw
        } else if (numAcceptedEntrants == 0 && numWaitingEntrants > numWinners) {
            numToDraw = numWinners;  // If no entrants have been accepted and there are more waiting entrants than winners, draw max num of winners
        } else if (numAcceptedEntrants != 0 && numWaitingEntrants > numWinners) {
            numToDraw = numWinners - numAcceptedEntrants;  // If there are already accepted entrants and there are more waiting entrants than winners, draw the difference
        } else {
            numToDraw = numWaitingEntrants;  // If there are less waiting entrants than winners, draw all waiting entrants
        }
        return numToDraw;
    }
}