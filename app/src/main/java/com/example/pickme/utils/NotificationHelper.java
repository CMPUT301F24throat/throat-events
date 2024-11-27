package com.example.pickme.utils;

import android.util.Log;

import com.example.pickme.models.Notification;
import com.example.pickme.models.User;
import com.example.pickme.repositories.EventRepository;
import com.example.pickme.repositories.NotificationRepository;
import com.example.pickme.repositories.UserRepository;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Manages local and push notifications
 * @author Omar-Kattan-1
 * <p>
 * Responsibilities:
 * Create and send notifications to users
 * cleans notifications (if deleted from repo and such)
 **/
public class NotificationHelper extends FirebaseMessagingService{

    public NotificationHelper(){

    }

    public void sendNotification(Notification notification){

        UserNotification userNotification = new UserNotification(notification.getNotificationId());
        UserRepository userRepository = new UserRepository();

        for(String userID : notification.getSendTo()){

            userRepository.getUserByDeviceId(userID, documentSnapshotTask -> {
                if(!documentSnapshotTask.isSuccessful() || documentSnapshotTask.getResult() == null){
                    Log.i("NOTIF", "Failed to find user to send notif; userID: " + userID);
                    return;
                }

                User user = documentSnapshotTask.getResult().toObject(User.class);

                if(user == null || !user.isNotificationEnabled())
                    return;

                user.addUserNotification(userNotification);

                userRepository.updateUser(user, task -> {
                    Log.i("NOTIF", "Sent notif to user: " + userID);
                });

            });
        }
    }

    public void cleanNotifications(Runnable toRun) {
        User user = User.getInstance();
        List<UserNotification> notificationsToRemove = new ArrayList<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (UserNotification userNotification : user.getUserNotifications()) {
            if (userNotification.getNotificationID() == null) {
                Log.i("NOTIF", "notif id was null");
                notificationsToRemove.add(userNotification);
                continue;
            }

            Log.i("NOTIF", "parsing ID: " + userNotification.getNotificationID());

            CompletableFuture<Void> future = new CompletableFuture<>();
            futures.add(future);

            NotificationRepository.getInstance().getNotificationById(userNotification.getNotificationID(), documentSnapshot -> {
                if (documentSnapshot == null) {
                    notificationsToRemove.add(userNotification);
                    new UserRepository().updateUser(user, task -> {
                        Log.i("NOTIF", "Cleaned Notifs; doc was null");
                        future.complete(null);
                    });
                    return;
                }

                Notification notification = documentSnapshot.toObject(Notification.class);
                if (notification == null || notification.getEventID() == null) {
                    notificationsToRemove.add(userNotification);
                    new UserRepository().updateUser(user, task -> {
                        Log.i("NOTIF", "Cleaned Notifs; notif was null");
                        future.complete(null);
                    });
                    return;
                }

                new EventRepository().getEventById(notification.getEventID(), documentSnapshot1 -> {
                    if (documentSnapshot1.getResult() == null) {
                        notificationsToRemove.add(userNotification);
                        new UserRepository().updateUser(user, task -> {
                            Log.i("NOTIF", "Cleaned Notifs; no event with that ID");
                            future.complete(null);
                        });
                    } else {
                        future.complete(null);
                    }
                });
            });
        }

        // Wait for all futures to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenRun(() -> {
            user.getUserNotifications().removeAll(notificationsToRemove);
            new UserRepository().updateUser(user, task -> {
                Log.i("NOTIF", "Cleaned Notifs");
                toRun.run();
            });

        });
    }
}

/*
  Sources:
  ChatGPT: how do i make it wait for all my async stuff to finish
 */
