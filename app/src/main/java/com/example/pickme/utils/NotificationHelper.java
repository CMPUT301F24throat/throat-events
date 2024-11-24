package com.example.pickme.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pickme.models.Notification;
import com.example.pickme.models.User;
import com.example.pickme.repositories.EventRepository;
import com.example.pickme.repositories.NotificationRepository;
import com.example.pickme.repositories.UserRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Manages local and push notifications
 * @author Omar-Kattan-1
 *
 * Responsibilities:
 * Create and send notifications to users
 * Handle notification preferences (opt-in/opt-out)
 * Handle Firebase Cloud Messaging (FCM) for push notifications
 **/
public class NotificationHelper extends FirebaseMessagingService{
    private Notification notification;

    public NotificationHelper(){
    }

    public NotificationHelper(Notification notification){
        this.notification = notification;
    }

    /**
     * Handles the event that a message has been received
     *
     * @param message Remote message that has been received.
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        if(message.getData().isEmpty())
            return;

        Log.i("TAG", message.getData().toString());
    }

    /**
     * Updates the User's FCM token in the db when it changes locally
     * @param token The token used for sending messages to this application instance. This token is
     *     the same as the one retrieved by {@link FirebaseMessaging#getToken()}.
     */
    @Override
    public void onNewToken(@NonNull String token) {
//        TODO: sendRegistrationToServer(token);
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

    public void cleanNotifications(){
        User user = User.getInstance();

        for(UserNotification userNotification : user.getUserNotifications()){
            if(userNotification.getNotificationID() == null){
                user.getUserNotifications().remove(userNotification);
                continue;
            }

            new NotificationRepository().getNotificationById(userNotification.getNotificationID(), documentSnapshot -> {
                if(documentSnapshot == null)
                    user.getUserNotifications().remove(userNotification);

                Notification notification = documentSnapshot.toObject(Notification.class);

                if(notification == null || notification.getEventID() == null) {
                    user.getUserNotifications().remove(userNotification);
                }

                new UserRepository().updateUser(user, task -> { Log.i("NOTIF", "Cleaned Notifs"); });

                new EventRepository().getEventById(notification.getEventID(), documentSnapshot1 -> {
                    if(documentSnapshot1.getResult() == null) {
                        user.getUserNotifications().remove(userNotification);
                        new UserRepository().updateUser(user, task -> {
                            Log.i("NOTIF", "Cleaned Notifs");
                        });
                    }
                });
            });


        }

//        user.getUserNotifications().removeIf(userNotification -> userNotification.getNotificationID() == null);
//
//        new UserRepository().updateUser(user, task -> { Log.i("NOTIF", "Cleaned Notifs"); });
    }


}

/**
 * Sources:
 * FirebaseMessaging: {@link https://firebase.google.com/docs/cloud-messaging/android/client?_gl=1*1c1ztk2*_up*MQ..*_ga*MTExNzcwMDM4LjE3MzA5OTYyMDc.*_ga_CW55HF8NVT*MTczMDk5NjIwNi4xLjAuMTczMDk5NjIwNi4wLjAuMA..}
 */
