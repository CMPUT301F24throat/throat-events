package com.example.pickme.utils;

import android.util.Log;

import com.example.pickme.models.Enums.EntrantStatus;
import com.example.pickme.models.Event;
import com.example.pickme.models.User;
import com.example.pickme.models.WaitingListEntrant;
import com.example.pickme.repositories.EventRepository;
import com.example.pickme.repositories.UserRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
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

            Log.i("LOTTERY", "num to draw: " + String.valueOf(numToDraw));

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
     * Replaces the entrants who declined their invitation with new entrants from the waiting list.
     *
     * @param event The event for which the lottery is to be run.
     * @param onCompleteListener The listener to be called upon completion of the lottery process.
     */
    public void replaceCancelledEntrants(Event event, OnCompleteListener<List<String>> onCompleteListener) {
        List<WaitingListEntrant> cancelledEntrants = event.getWaitingList().stream()
                .filter(e -> e.getStatus() == EntrantStatus.CANCELLED)
                .collect(Collectors.toList());

        ArrayList<WaitingListEntrant> updatedWaitingList = new ArrayList<>(event.getWaitingList());
        updatedWaitingList.removeAll(cancelledEntrants);  // Get rid of the cancelled entrants

        // Update the event's waiting list to not include the cancelled entrants
        event.setWaitingList(updatedWaitingList);

        // Remove eventId from each cancelled entrant's eventIDs property and update the user in Firestore
        for (WaitingListEntrant entrant : cancelledEntrants) {
            UserRepository userRepository = UserRepository.getInstance();
            userRepository.getUserByDeviceId(entrant.getEntrantId(), userTask -> {
                if (userTask.isSuccessful() && userTask.getResult() != null) {
                    User user = userTask.getResult();
                    if (user != null) {
                        user.getEventIDs().remove(event.getEventId());
                        userRepository.updateUser(user, null);
                    }
                }
            });
        }

        // Draw the lottery for the event with the number of cancelled entrants
        int rejectedCount = cancelledEntrants.size();
        drawLottery(event, rejectedCount, onCompleteListener);
    }

    /**
     * Sets pending entrants (selected entrants who haven't accepted/declined) as cancelled.
     *
     * @param event The event for which the lottery is to be run.
     * @param onCompleteListener The listener to be called upon completion of the lottery process.
     */
    public void cancelPendingEntrants(Event event, OnCompleteListener<Void> onCompleteListener) {
        List<WaitingListEntrant> pendingEntrants = event.getWaitingList().stream()
                .filter(e -> e.getStatus() == EntrantStatus.SELECTED)
                .collect(Collectors.toList());

        ArrayList<WaitingListEntrant> updatedWaitingList = new ArrayList<>(event.getWaitingList());
        // Update status of selected entrants to cancelled
        for (WaitingListEntrant entrant : pendingEntrants) {
            entrant.setStatus(EntrantStatus.CANCELLED);
            updatedWaitingList.set(updatedWaitingList.indexOf(entrant), entrant);
        }

        // Update the event's waiting list to not include the pending entrants
        event.setWaitingList(updatedWaitingList);

        // Update the event in the database with the new waiting list
        EventRepository.getInstance().updateEvent(event, null, task -> {
            if (task.isSuccessful()) {
                onCompleteListener.onComplete(Tasks.forResult(null));
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

        UserRepository.getInstance().getAllUsers(query -> {
            if(!query.isSuccessful() || query.getResult() == null)
                return;

            for(WaitingListEntrant entrant : event.getWaitingList()){

                boolean exists = false;
                for(DocumentSnapshot doc : query.getResult().getDocuments()){
                    if(doc.getId().equals(entrant.getEntrantId())){
                        checkedEntrants.add(entrant);
                        exists = true;
                        break;
                    }
                }

                if(!exists)
                    event.getWaitingList().remove(entrant);
            }

            List<WaitingListEntrant> waitingEntrants = new ArrayList<>(checkedEntrants.stream().filter(e -> e.getStatus() == EntrantStatus.WAITING).collect(Collectors.toList()));
            Collections.shuffle(waitingEntrants);

            List<String> selectedUserDeviceIds = new ArrayList<>();
            for (int i = 0; i < Math.min(numToDraw, waitingEntrants.size()); i++) {
                selectedUserDeviceIds.add(waitingEntrants.get(i).getEntrantId());
                checkedEntrants.get(checkedEntrants.indexOf(waitingEntrants.get(i))).setStatus(EntrantStatus.SELECTED);     // Update entrant status to selected in checked entrants
            }

            for(int i = Math.min(numToDraw, waitingEntrants.size()); i < waitingEntrants.size(); i++){
                checkedEntrants.get(checkedEntrants.indexOf(waitingEntrants.get(i))).setStatus(EntrantStatus.REJECTED);
            }

            event.setWaitingList(new ArrayList<>(checkedEntrants)); // Update event's waiting list to reflect entrantStatus changes
            event.setHasLotteryExecuted(true);

            // don't need to update poster so keep null but we need to update waiting list w/ new entrant status
            EventRepository.getInstance().updateEvent(event, null, task -> {
                if (task.isSuccessful()) {
                    onCompleteListener.onComplete(Tasks.forResult(selectedUserDeviceIds));
                } else {
                    onCompleteListener.onComplete(Tasks.forException(task.getException()));
                }
            });
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
            if (entrant.getStatus() == EntrantStatus.ACCEPTED || entrant.getStatus() == EntrantStatus.SELECTED) {
                currentNumInvited++;
            }
        }

        int numWaitingEntrants = (int) event.getWaitingList().stream().filter(e -> e.getStatus() == EntrantStatus.WAITING).count();
        return Math.min(targetNumWinners - currentNumInvited, numWaitingEntrants);
    }
}