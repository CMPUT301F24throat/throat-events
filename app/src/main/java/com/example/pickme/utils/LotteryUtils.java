package com.example.pickme.utils;

import com.example.pickme.models.Enums.EntrantStatus;
import com.example.pickme.models.Event;
import com.example.pickme.models.User;
import com.example.pickme.models.WaitingListEntrant;
import com.example.pickme.repositories.EventRepository;
import com.example.pickme.repositories.UserRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
     * @param event The event for which the lottery is to be run.
     * @param onCompleteListener The listener to be called upon completion of the lottery process.
     */
    public void runLottery(Event event, OnCompleteListener<List<String>> onCompleteListener) {
        User currentUser = User.getInstance();
        String currentUserDeviceId = currentUser.getDeviceId();

        if (isValidEvent(event, currentUserDeviceId)) {
            int numToDraw = determineNumToDraw(event);

            if (numToDraw > 0) {
                drawLottery(event, numToDraw, onCompleteListener);
            } else {
                onCompleteListener.onComplete(Tasks.forResult(new ArrayList<>()));
            }
        } else {
            onCompleteListener.onComplete(Tasks.forException(new Exception("User is not the organizer of the event or event has passed")));
        }
    }

    /**
     * Replaces the rejected entrants with new entrants from the waiting list.
     *
     * @param event The event for which the lottery is to be run.
     * @param onCompleteListener The listener to be called upon completion of the lottery process.
     */
    public void replaceRejectedEntrants(Event event, OnCompleteListener<List<String>> onCompleteListener) {
        List<WaitingListEntrant> rejectedEntrants = event.getWaitingList().stream()
                .filter(e -> e.getStatus() == EntrantStatus.REJECTED)
                .collect(Collectors.toList());

        for (WaitingListEntrant entrant : rejectedEntrants) {
            entrant.setStatus(EntrantStatus.CANCELLED);
        }

        // Create a new list with updated statuses
        ArrayList<WaitingListEntrant> updatedWaitingList = new ArrayList<>(event.getWaitingList());
        for (WaitingListEntrant entrant : rejectedEntrants) {
            int index = updatedWaitingList.indexOf(entrant);
            if (index != -1) {
                updatedWaitingList.set(index, entrant);
            }
        }

        // Update the event's waiting list with the modified entrants before drawing the lottery
        event.setWaitingList(updatedWaitingList);

        int rejectedCount = rejectedEntrants.size();
        drawLottery(event, rejectedCount, onCompleteListener);
    }

    /**
     * Validates if the event is valid for running the lottery.
     *
     * @param event The event to be validated.
     * @param currentUserDeviceId The device ID of the current user.
     * @return True if the event is valid, false otherwise.
     */
    private boolean isValidEvent(Event event, String currentUserDeviceId) {
        if (event == null) {
            return false;
        }
        return event.getOrganizerId().equals(currentUserDeviceId) && !eventRepository.hasEventPassed(event);
    }

    /**
     * Draws the lottery for the event.
     *
     * @param event The event object.
     * @param numToDraw The number of entrants to draw.
     * @param onCompleteListener The listener to be called upon completion of the lottery process.
     */
    private void drawLottery(Event event, int numToDraw, OnCompleteListener<List<String>> onCompleteListener) {
        List<WaitingListEntrant> checkedEntrants = new ArrayList<>();

        // first check that all entrants still exist
        event.getWaitingList()
                .forEach(entrant -> UserRepository.getInstance().checkUserExists(entrant.getEntrantId(), task -> {
                    if (task.isSuccessful() && task.getResult()) {
                        checkedEntrants.add(entrant);
                    } else {
                        // User no longer exists, remove from waiting list
                        event.getWaitingList().remove(entrant);
                    }
                }));

        List<WaitingListEntrant> waitingEntrants = new ArrayList<>(checkedEntrants.stream().filter(e -> e.getStatus() == EntrantStatus.WAITING).collect(Collectors.toList()));
        Collections.shuffle(waitingEntrants);

        List<String> selectedUserDeviceIds = new ArrayList<>();
        for (int i = 0; i < Math.min(numToDraw, waitingEntrants.size()); i++) {
            selectedUserDeviceIds.add(waitingEntrants.get(i).getEntrantId());
            checkedEntrants.get(checkedEntrants.indexOf(waitingEntrants.get(i))).setStatus(EntrantStatus.SELECTED);     // Update entrant status to selected in checked entrants
        }

        event.setWaitingList(new ArrayList<>(checkedEntrants)); // Update event's waiting list to reflect entrantStatus changes
        event.setHasLotteryExecuted(true);

        // don't need to update poster so keep null but we need to update waiting list w/ new entrant statys
        EventRepository.getInstance().updateEvent(event, null, task -> {
            if (task.isSuccessful()) {
                onCompleteListener.onComplete(Tasks.forResult(selectedUserDeviceIds));
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
    public int determineNumToDraw(Event event) {
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