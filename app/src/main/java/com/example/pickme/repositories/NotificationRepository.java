package com.example.pickme.repositories;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.pickme.models.Event;
import com.example.pickme.models.Notification;
import com.example.pickme.models.User;
import com.example.pickme.utils.NotificationList;
import com.example.pickme.utils.UserNotification;
import com.example.pickme.views.adapters.NotifRecAdapter;
import com.example.pickme.views.adapters.NotificationAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Handles interactions with the notifications collection
 * @version 1.1
 * Responsibilities:
 * CRUD operations for notification data
 */
public class NotificationRepository {
    private final FirebaseFirestore db;
    private final CollectionReference notificationsRef;
    private NotificationAdapter notificationAdapter;
    private NotifRecAdapter notifRecAdapter;
    static boolean listening = false;
    private static NotificationRepository instance;

    /**
     * Default constructor that initializes Firebase Firestore and FirebaseAuth instances.
     */
    private NotificationRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.notificationsRef = db.collection("notifications");

        // temporary anonymous auth
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.d("AUTH", "AUTH: Successful authentication");
            }
        });
    }

    /**
     * Singleton pattern to ensure only one instance of the repository is created.
     *
     * @return The instance of the NotificationRepository.
     */
    public static synchronized NotificationRepository getInstance(){
        if (instance == null) {
            instance = new NotificationRepository();
        }

        return instance;
    }

    /**
     * Adds a notification to the db
     * @see Notification
     *
     * @param notification          the notification object to add
     * @param onCompleteListener    task to be completed once this is done
     */
    public void addNotification(Notification notification, OnCompleteListener<Object> onCompleteListener) {
        db.runTransaction(transaction -> {
                    DocumentReference newNotificationRef = notificationsRef.document();
                    notification.setNotificationId(newNotificationRef.getId());
                    transaction.set(newNotificationRef, notification);
                    return null;
                }).addOnCompleteListener(onCompleteListener)
                .addOnFailureListener(e -> {
                    // Handle the error
                    System.err.println("Transaction failed: " + e.getMessage());
                });
    }

    /**
     * Reads a notification by ID
     * @param notificationId The ID of the notification to be read
     * @param eventListener Listener to handle real-time updates
     */
    public void getNotificationById(String notificationId, EventListener<DocumentSnapshot> eventListener) {
        notificationsRef.document(notificationId).addSnapshotListener(eventListener);
    }

    /**
     * Updates a notification
     * @param notification The notification object containing updated information
     */
    public void updateNotification(Notification notification) {
        notificationsRef.document(notification.getNotificationId()).set(notification);
    }

    /**
     * Deletes a notification by ID
     * @param notificationId The ID of the notification to be deleted
     */
    public void deleteNotification(String notificationId) {
        notificationsRef.document(notificationId).delete();
    }

    /**
     * Reads all notifications
     * @param eventListener Listener to handle real-time updates
     */
    public void getAllNotifications(EventListener<QuerySnapshot> eventListener) {
        notificationsRef.addSnapshotListener(eventListener);
    }

    /**
     * Reads notifications by receiver user ID
     * @param userId The ID of the receiver user
     * @param eventListener Listener to handle real-time updates
     */
    public void getNotificationsByReceiverUserId(String userId, EventListener<QuerySnapshot> eventListener) {
        notificationsRef.whereEqualTo("receiverId", userId).addSnapshotListener(eventListener);
    }

    /**
     * Reads notifications by sender user ID
     * @param userId The ID of the sender user
     * @param eventListener Listener to handle real-time updates
     */
    public void getNotificationsBySenderUserId(String userId, EventListener<QuerySnapshot> eventListener) {
        notificationsRef.whereEqualTo("senderId", userId).addSnapshotListener(eventListener);
    }

    /**
     * Adds a snapshot listener for real-time updates
     * @param context The context in which the listener is added
     */
    public void addSnapshotListener(Context context){
        if(listening)
            return;

        NotificationList notificationList = NotificationList.getInstance();
        User user = User.getInstance();

        Log.i("NOTIF", "adding listener");

        notificationsRef.addSnapshotListener((query, error) -> {

            if(!listening){
                listening = true;
                return;
            }

            if(error != null){
                Log.e("NOTIF", "Listen failed: ", error);
                return;
            }

            if(query != null){
                for(DocumentChange change : query.getDocumentChanges()){
                    Notification notification = change.getDocument().toObject(Notification.class);

                    if(!notification.getSendTo().contains(user.getDeviceId()))
                        continue;

                    if(change.getType() == DocumentChange.Type.ADDED){
                        notificationList.add(notification);
                        user.addUserNotification(new UserNotification(notification.getNotificationId()));
                        EventRepository eventRepository = EventRepository.getInstance();
                        eventRepository.getEventById(notification.getEventID(), (documentSnapshot, eventError) -> {
                            if (eventError != null || documentSnapshot == null) {
                                Log.e("NOTIF", "Failed to fetch event for notification: " + notification.getNotificationId(), eventError);
                                return;
                            }

                            Event event = documentSnapshot.toObject(Event.class);
                            if (event != null) {
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channelID")
                                        .setSmallIcon(android.R.drawable.ic_menu_info_details)
                                        .setContentTitle(event.getEventTitle())
                                        .setContentText(notification.getMessage());

                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                                    notificationManager.notify(notification.getNotificationId().hashCode(), builder.build());
                                }
                            }
                        });
                    }
                    else if(change.getType() == DocumentChange.Type.REMOVED)
                        notificationList.remove(notification);
                }

                if(notificationAdapter != null)
                    this.notificationAdapter.notifyDataSetChanged();

                if(notifRecAdapter != null)
                    this.notifRecAdapter.notifyDataSetChanged();
            }
        });
    }

    public void attachAdapter(NotificationAdapter notificationAdapter){
        this.notificationAdapter = notificationAdapter;
    }

    public void attachRecAdapter(NotifRecAdapter notifRecAdapter){
        this.notifRecAdapter = notifRecAdapter;
    }
}

/*
Sources:

Firebase docs: https://firebase.google.com/docs/cloud-messaging/android/client?_gl=1*1c1ztk2*_up*MQ..*_ga*MTExNzcwMDM4LjE3MzA5OTYyMDc.*_ga_CW55HF8NVT*MTczMDk5NjIwNi4xLjAuMTczMDk5NjIwNi4wLjAuMA..
 */
