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
    private Date createdAt;
    private Date readAt;
}
