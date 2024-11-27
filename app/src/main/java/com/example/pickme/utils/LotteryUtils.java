package com.example.pickme.utils;

import com.example.pickme.models.Enums.EntrantStatus;
import com.example.pickme.models.Event;
import com.example.pickme.models.User;
import com.example.pickme.models.WaitingListEntrant;
import com.example.pickme.repositories.EventRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
        User currentUser = User.getInstance();
        String currentUserDeviceId = currentUser.getDeviceId();

        db.collection("events").document(eventId).get().addOnCompleteListener(eventTask -> {
            if (eventTask.isSuccessful() && eventTask.getResult() != null) {
                Event event = eventTask.getResult().toObject(Event.class);

                if (event != null && event.getOrganizerId().equals(currentUserDeviceId) && !eventRepository.hasEventPassed(event)) {
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

                                                List<String> selectedUserDeviceIds = new ArrayList<>();
                                                for (int i = 0; i < Math.min(numToDraw, waitingEntrants.size()); i++) {
                                                    // Add the selected entrants to the list and update their status to selected
                                                    selectedUserDeviceIds.add(waitingEntrants.get(i).getEntrantId());
                                                    waitingListUtils.updateEntrantStatus(eventId, waitingEntrants.get(i).getEntrantId(), EntrantStatus.SELECTED);
                                                }

                                                onCompleteListener.onComplete(Tasks.forResult(selectedUserDeviceIds));
                                            } else {
                                                onCompleteListener.onComplete(Tasks.forException(waitingEntrantsTask.getException()));
                                            }
                                        });
                                    } else {
                                        onCompleteListener.onComplete(Tasks.forResult(new ArrayList<>()));
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
        waitingListUtils.getSelectedEntrants(eventId, selectedEntrantsTask -> {
            if (selectedEntrantsTask.isSuccessful() && selectedEntrantsTask.getResult() != null) {
                List<WaitingListEntrant> pendingEntrants = selectedEntrantsTask.getResult();
                boolean isMaxReached = event.getMaxEntrants() != null && pendingEntrants.size() >= event.getMaxEntrants();
                onCompleteListener.onComplete(Tasks.forResult(isMaxReached));
            } else {
                onCompleteListener.onComplete(Tasks.forException(selectedEntrantsTask.getException()));
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
    public void getNumToDraw(Event event, String eventId, OnCompleteListener<Integer> onCompleteListener) {
        waitingListUtils.getAcceptedEntrants(eventId, acceptedEntrantsTask -> {
            if (acceptedEntrantsTask.isSuccessful() && acceptedEntrantsTask.getResult() != null) {

                // Add the number of accepted entrants to the number of selected entrants to get the potential total number of winners
                List<WaitingListEntrant> acceptedEntrants = acceptedEntrantsTask.getResult();
                AtomicInteger numPotentialWinners = new AtomicInteger(acceptedEntrants.size());

                waitingListUtils.getSelectedEntrants(eventId, selectedEntrantsTask -> {
                    if (selectedEntrantsTask.isSuccessful() && selectedEntrantsTask.getResult() != null) {
                        List<WaitingListEntrant> selectedEntrants = selectedEntrantsTask.getResult();
                        numPotentialWinners.addAndGet(selectedEntrants.size());

                        int targetNumWinners = Integer.parseInt(event.getMaxWinners());

                        // Get the number of entrants to draw using helper method
                        waitingListUtils.getWaitingEntrants(eventId, waitingEntrantsTask -> {
                            if (waitingEntrantsTask.isSuccessful() && waitingEntrantsTask.getResult() != null) {
                                List<WaitingListEntrant> waitingEntrants = waitingEntrantsTask.getResult();
                                int numToDraw = determineNumToDraw(waitingEntrants, numPotentialWinners.get(), targetNumWinners);

                                onCompleteListener.onComplete(Tasks.forResult(numToDraw));
                            } else {
                                onCompleteListener.onComplete(Tasks.forException(waitingEntrantsTask.getException()));
                            }
                        });
                    } else {
                        onCompleteListener.onComplete(Tasks.forException(selectedEntrantsTask.getException()));
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
     * @param waitingEntrants     The list of entrants currently waiting.
     * @param numPotentialWinners The number of entrants that have already been accepted.
     * @param targetNumWinners    The maximum number of winners allowed.
     * @return The number of entrants to draw.
     */
    private static int determineNumToDraw(List<WaitingListEntrant> waitingEntrants, int numPotentialWinners, int targetNumWinners) {
        int numWaitingEntrants = waitingEntrants.size();
        int numToDraw;

        if (numPotentialWinners == targetNumWinners) {
            numToDraw = 0;  // If num of accepted entrants is equal to num of winners, no need to draw
        } else if (numPotentialWinners == 0 && numWaitingEntrants > targetNumWinners) {
            numToDraw = targetNumWinners;  // If no entrants have been accepted and there are more waiting entrants than winners, draw max num of winners
        } else if (numPotentialWinners != 0 && numWaitingEntrants > targetNumWinners) {
            numToDraw = targetNumWinners - numPotentialWinners;  // If there are already accepted entrants and there are more waiting entrants than winners, draw the difference
        } else {
            numToDraw = numWaitingEntrants;  // If there are less waiting entrants than winners, draw all waiting entrants
        }
        return numToDraw;
    }
}