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

/**
 * Utility class for running a lottery on an event's waiting list.
 */
public class LotteryUtils {

    private final WaitingListUtils waitingListUtils = new WaitingListUtils();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final EventRepository eventRepository = EventRepository.getInstance();

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

                if (isValidEvent(event, currentUserDeviceId)) {
                    int numToDraw = determineNumToDraw(event);

                    if (numToDraw > 0) {
                        drawLottery(eventId, event, numToDraw, onCompleteListener);
                    } else {
                        onCompleteListener.onComplete(Tasks.forResult(new ArrayList<>()));
                    }
                } else {
                    onCompleteListener.onComplete(Tasks.forException(new Exception("User is not the organizer of the event or event has passed")));
                }
            } else {
                onCompleteListener.onComplete(Tasks.forException(eventTask.getException()));
            }
        });
    }

    /**
     * Replaces the rejected entrants with new entrants from the waiting list.
     *
     * @param eventId The ID of the event for which the lottery is to be run.
     * @param onCompleteListener The listener to be called upon completion of the lottery process.
     */
    public void replaceRejectedEntrants(String eventId, OnCompleteListener<List<String>> onCompleteListener) {
        waitingListUtils.getWaitingListEntrantsByStatus(eventId, EntrantStatus.REJECTED, task -> {
            if (task.isSuccessful()) {
                List<WaitingListEntrant> rejectedEntrants = task.getResult();
                if (!rejectedEntrants.isEmpty()) {
                    // Cancel all rejected entrants
                    for (WaitingListEntrant entrant : rejectedEntrants) {
                        waitingListUtils.updateEntrantStatus(eventId, entrant.getEntrantId(), EntrantStatus.CANCELLED);
                    }

                    // Draw new entrants to replace the rejected ones
                    db.collection("events").document(eventId).get().addOnCompleteListener(eventTask -> {
                        if (eventTask.isSuccessful() && eventTask.getResult() != null) {
                            Event event = eventTask.getResult().toObject(Event.class);
                            if (isValidEvent(event, User.getInstance().getDeviceId())) {
                                int numToDraw = rejectedEntrants.size();
                                drawLottery(eventId, event, numToDraw, onCompleteListener);
                            } else {
                                onCompleteListener.onComplete(Tasks.forException(new Exception("User is not the organizer of the event or event has passed")));
                            }
                        } else {
                            onCompleteListener.onComplete(Tasks.forException(eventTask.getException()));
                        }
                    });
                } else {
                    onCompleteListener.onComplete(Tasks.forResult(new ArrayList<>()));
                }
            } else {
                onCompleteListener.onComplete(Tasks.forException(task.getException()));
            }
        });
    }

    /**
     * Validates if the event is valid for running the lottery.
     *
     * @param event The event to be validated.
     * @param currentUserDeviceId The device ID of the current user.
     * @return True if the event is valid, false otherwise.
     */
    private boolean isValidEvent(Event event, String currentUserDeviceId) {
        return event != null && event.getOrganizerId().equals(currentUserDeviceId) && !eventRepository.hasEventPassed(event);
    }

    /**
     * Draws the lottery for the event.
     *
     * @param eventId The ID of the event.
     * @param event The event object.
     * @param numToDraw The number of entrants to draw.
     * @param onCompleteListener The listener to be called upon completion of the lottery process.
     */
    private void drawLottery(String eventId, Event event, int numToDraw, OnCompleteListener<List<String>> onCompleteListener) {
        waitingListUtils.getWaitingListEntrantsByStatus(eventId, EntrantStatus.WAITING, task -> {
            if (task.isSuccessful()) {
                List<WaitingListEntrant> waitingEntrants = task.getResult();
                Collections.shuffle(waitingEntrants);

                List<String> selectedUserDeviceIds = new ArrayList<>();
                for (int i = 0; i < Math.min(numToDraw, waitingEntrants.size()); i++) {
                    selectedUserDeviceIds.add(waitingEntrants.get(i).getEntrantId());
                    waitingEntrants.get(i).setStatus(EntrantStatus.SELECTED);   // Update entrant status to selected
                }

                event.setWaitingList(new ArrayList<>(waitingEntrants)); // Update event's waiting list to reflect entrantStatus changes
                event.setHasLotteryExecuted(true);

                // Update new event data in Firestore
                eventRepository.updateEvent(event, null, updateTask -> {
                    if (updateTask.isSuccessful()) {
                        onCompleteListener.onComplete(Tasks.forResult(selectedUserDeviceIds));
                    } else {
                        onCompleteListener.onComplete(Tasks.forException(updateTask.getException()));
                    }
                });
            } else {
                onCompleteListener.onComplete(Tasks.forException(task.getException()));
            }
        });
    }

    /**
     * Determines the number of entrants to draw in the lottery.
     *
     * @param event The event for which the lottery is being run.
     * @return The number of entrants to draw.
     */
    private int determineNumToDraw(Event event) {
        int currentNumInvited = 0;
        int targetNumWinners = event.getMaxWinners();

        for (WaitingListEntrant entrant : event.getWaitingList()) {
            if (entrant.getStatus() == EntrantStatus.ACCEPTED || entrant.getStatus() == EntrantStatus.SELECTED || entrant.getStatus() == EntrantStatus.REJECTED) {
                currentNumInvited++;
            }
        }

        int numWaitingEntrants = (int) event.getWaitingList().stream().filter(e -> e.getStatus() == EntrantStatus.WAITING).count();
        return Math.min(targetNumWinners - currentNumInvited, numWaitingEntrants);
    }
}