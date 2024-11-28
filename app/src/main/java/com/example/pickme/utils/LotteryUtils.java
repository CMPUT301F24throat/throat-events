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

                if (event != null && event.getOrganizerId().equals(currentUserDeviceId) && !eventRepository.hasEventPassed(event)) {
                    if (!event.hasLotteryExecuted()) {
                        int numToDraw = determineNumToDraw(event);

                        if (numToDraw > 0) {
                            List<WaitingListEntrant> waitingEntrants = new ArrayList<>();
                            for (WaitingListEntrant entrant : event.getWaitingList()) {
                                if (entrant.getStatus() == EntrantStatus.WAITING) {
                                    waitingEntrants.add(entrant);
                                }
                            }
                            Collections.shuffle(waitingEntrants);

                            List<String> selectedUserDeviceIds = new ArrayList<>();
                            for (int i = 0; i < Math.min(numToDraw, waitingEntrants.size()); i++) {
                                selectedUserDeviceIds.add(waitingEntrants.get(i).getEntrantId());
                                waitingEntrants.get(i).setStatus(EntrantStatus.SELECTED);
                            }

                            event.setWaitingList(new ArrayList<>(waitingEntrants));
                            event.setHasLotteryExecuted(true);
                            eventRepository.updateEvent(event, updateTask -> {
                                if (updateTask.isSuccessful()) {
                                    onCompleteListener.onComplete(Tasks.forResult(selectedUserDeviceIds));
                                } else {
                                    onCompleteListener.onComplete(Tasks.forException(updateTask.getException()));
                                }
                            });
                        } else {
                            onCompleteListener.onComplete(Tasks.forResult(new ArrayList<>()));
                        }
                    } else {
                        onCompleteListener.onComplete(Tasks.forException(new Exception("Lottery has already been executed for this event")));
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
     * Determines the number of entrants to draw in the lottery.
     *
     * @param event The event for which the lottery is being run.
     * @return The number of entrants to draw.
     */
    private int determineNumToDraw(Event event) {
        int numPotentialWinners = 0;
        int targetNumWinners = event.getMaxWinners();

        for (WaitingListEntrant entrant : event.getWaitingList()) {
            if (entrant.getStatus() == EntrantStatus.ACCEPTED || entrant.getStatus() == EntrantStatus.SELECTED) {
                numPotentialWinners++;
            }
        }

        int numWaitingEntrants = (int) event.getWaitingList().stream().filter(e -> e.getStatus() == EntrantStatus.WAITING).count();
        return Math.min(targetNumWinners - numPotentialWinners, numWaitingEntrants);
    }
}