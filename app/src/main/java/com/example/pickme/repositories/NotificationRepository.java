package com.example.pickme.repositories;

import android.util.Log;

import com.example.pickme.models.Notification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Handles interactions with the notifications collection
 * @author sophiecabungcal
 * @version 1.0
 * Responsibilities:
 * CRUD operations for notification data
 */
public class NotificationRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference notificationsRef = db.collection("notifications");

    public NotificationRepository() {
        // temporary anonymous auth
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.d("AUTH", "AUTH: Successful authentication");
            }
        });
    }

    // Create a new notification
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
    public void getNotificationById(String notificationId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        notificationsRef.document(notificationId).get().addOnCompleteListener(onCompleteListener);
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

}
