package com.example.pickme.repositories;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

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
}
