package com.example.pickme.repositories;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Handles interactions with the notifications collection
 * @author sophiecabungcal, Omar-Kattan-1
 * @version 1.1
 * <b></b>
 * Responsibilities:
 * CRUD operations for notification data
 */
public class NotificationRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference notificationsRef = db.collection("notifications");

    private NotificationAdapter notificationAdapter;
    private NotifRecAdapter notifRecAdapter;
    static boolean listening = false;

    private static NotificationRepository instance;

    public static NotificationRepository getInstance(){
        if(instance == null)
            instance = new NotificationRepository();

        return instance;
    }

    /**
     * Constructor also initializes the Firebase db
     */
    private NotificationRepository() {
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

    // Read a notification by ID
    public void getNotificationById(String notificationId, OnSuccessListener<DocumentSnapshot> onSuccessListener) {
        notificationsRef.document(notificationId).get().addOnSuccessListener(onSuccessListener);
    }

    // Update a notification
    public void updateNotification(Notification notification) {
        notificationsRef.document(notification.getNotificationId()).set(notification);
    }

    // Delete a notification by ID
    public void deleteNotification(String notificationId) {
        notificationsRef.document(notificationId).delete();
    }

    // Read all notifications
    public void getAllNotifications(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        notificationsRef.get().addOnCompleteListener(onCompleteListener);
    }

    // Read notifications by receiver user ID
    public void getNotificationsByReceiverUserId(String userId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        notificationsRef.whereEqualTo("receiverId", userId).get().addOnCompleteListener(onCompleteListener);
    }

    // Read notifications by sender user ID
    public void getNotificationsBySenderUserId(String userId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        notificationsRef.whereEqualTo("senderId", userId).get().addOnCompleteListener(onCompleteListener);
    }

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
                listening = false;
                return;
            }

            if(query != null){
                for(DocumentChange change : query.getDocumentChanges()){
                    Log.i("NOTIF", "doc changed: " + change.getDocument().getId());
                    Notification notification = change.getDocument().toObject(Notification.class);

                    if(!notification.getSendTo().contains(user.getDeviceId()))
                        continue;

                    if(change.getType() == DocumentChange.Type.ADDED){
                        notificationList.add(notification);
                        user.addUserNotification(new UserNotification(notification.getNotificationId()));
                        EventRepository.getInstance().getEventById(notification.getEventID(), task ->{
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channelID")
                                    .setSmallIcon(android.R.drawable.ic_menu_info_details)
                                    .setContentTitle(task.getResult().getEventTitle())
                                    .setContentText(notification.getMessage());


                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                                    notificationManager.notify(notification.getNotificationId().hashCode(), builder.build());
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
