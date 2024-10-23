package com.example.pickme.models;

import com.example.pickme.models.Enums.NotificationType;

import java.util.Date;

/**
 * Represents notifications sent to users (entrants/organizers/admins)
 * Responsibilities:
 * Captures notification messages in the notifications collection
 * and tracks whether a notification has been read
 **/

public class Notification {
    private String notificationId;
    private String receiverId;
    private String senderId;
    private NotificationType type;
    private Date readAt;
    private final Date createdAt;
    private Date updatedAt;

    public Notification() {
        this.createdAt = new Date();
    }

    public Notification(String receiverId, String senderId, NotificationType type) {
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.type = type;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
        this.updatedAt = new Date();
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
        this.updatedAt = new Date();
    }

    public void setType(NotificationType type) {
        this.type = type;
        this.updatedAt = new Date();
    }

    public void setReadAt(Date readAt) {
        this.readAt = readAt;
        this.updatedAt = new Date();
    }
}
