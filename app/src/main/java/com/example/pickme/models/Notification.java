package com.example.pickme.models;

import com.example.pickme.models.Enums.NotificationType;
import com.example.pickme.utils.TimestampUtil;
import com.google.firebase.Timestamp;

import java.time.LocalDateTime;

/**
 * Represents notifications sent to users (entrants/organizers/admins)
 * Responsibilities:
 * Models a notification in the notifications collection
 * Captures notification messages in the notifications collection
 * and tracks whether a notification has been read
 **/

public class Notification {
    private String notificationId;
    private String receiverId;
    private String senderId;
    private NotificationType type;
    private Timestamp readAt;
    private final Timestamp createdAt;
    private Timestamp updatedAt;

    public Notification() {
        this.createdAt = Timestamp.now();
    }

    public Notification(String receiverId, String senderId, NotificationType type) {
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.type = type;
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
        this.updatedAt = Timestamp.now();
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
        this.updatedAt = Timestamp.now();
    }

    public String getSenderId() {
        return senderId;
    }

    public void setType(NotificationType type) {
        this.type = type;
        this.updatedAt = Timestamp.now();
    }

    public NotificationType getType() {
        return type;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = TimestampUtil.toTimestamp(readAt);
        this.updatedAt = Timestamp.now();
    }

    public Timestamp getReadAt() {
        return readAt;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
}
