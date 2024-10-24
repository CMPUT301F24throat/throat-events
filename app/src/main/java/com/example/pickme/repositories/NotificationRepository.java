package com.example.pickme.repositories;

import com.example.pickme.models.Notification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Handles interactions with the notifications collection
 * Responsibilities:
 * CRUD operations for notification data
 * Send and retrieve notifications
 * Update notification read status
 * Track which users have been notified
 **/

public class NotificationRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference notificationsRef = db.collection("notifications");

    // Create a new notification
    public void addNotification(Notification notification, OnCompleteListener<DocumentReference> onCompleteListener) {
        notificationsRef.add(notification).addOnCompleteListener(onCompleteListener);
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
