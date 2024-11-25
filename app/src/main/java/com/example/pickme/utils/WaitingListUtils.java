package com.example.pickme.utils;

import com.example.pickme.models.Event;
import com.example.pickme.models.WaitingList;
import com.example.pickme.models.WaitingListEntrant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class WaitingListUtils {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference eventsRef = db.collection("events");

    // Method to get an event's waiting list
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

    // Method to get an entrant from the waiting list
    public void getEntrantFromWaitingList(String eventId, String entrantId, OnCompleteListener<WaitingListEntrant> onCompleteListener) {
        DocumentReference entrantRef = eventsRef.document(eventId).collection("waitingList").document(entrantId);
        entrantRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                WaitingListEntrant entrant = task.getResult().toObject(WaitingListEntrant.class);
                onCompleteListener.onComplete(Tasks.forResult(entrant));
            } else {
                onCompleteListener.onComplete(Tasks.forException(task.getException()));
            }
        });
    }

    // Method to get all entrants from the waiting list
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

    // Add a new entrant to the waiting list
    public void addEntrantToWaitingList(String eventId, WaitingListEntrant waitingListEntrant, OnCompleteListener<Void> onCompleteListener) {
        DocumentReference newEntrantRef = eventsRef.document(eventId).collection("waitingList").document();
        waitingListEntrant.setWaitListEntrantId(newEntrantRef.getId());
        newEntrantRef.set(waitingListEntrant)
                .addOnCompleteListener(onCompleteListener)
                .addOnFailureListener(e -> {
                    // Handle the error
                    System.err.println("Addition failed: " + e.getMessage());
                });
    }

    // Edit an entrant in the waiting list
    public void editEntrantInWaitingList(String eventId, String entrantId, WaitingListEntrant waitingListEntrant, OnCompleteListener<Void> onCompleteListener) {
        DocumentReference entrantRef = eventsRef.document(eventId).collection("waitingList").document(entrantId);
        entrantRef.set(waitingListEntrant)
                .addOnCompleteListener(onCompleteListener)
                .addOnFailureListener(e -> {
                    // Handle the error
                    System.err.println("Update failed: " + e.getMessage());
                });
    }

    // Remove an entrant from the waiting list
    public void deleteEntrantFromWaitingList(String eventId, String entrantId, OnCompleteListener<Void> onCompleteListener) {
        DocumentReference entrantRef = eventsRef.document(eventId).collection("waitingList").document(entrantId);
        entrantRef.delete()
                .addOnCompleteListener(onCompleteListener)
                .addOnFailureListener(e -> {
                    // Handle the error
                    System.err.println("Deletion failed: " + e.getMessage());
                });
    }

}