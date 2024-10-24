package com.example.pickme.models;

import java.time.LocalDateTime;
import java.util.ArrayList;

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
    private String notificationID;
    private String message;

    private boolean read;
    private LocalDateTime dateTime;

    private User sentFrom;
    private ArrayList<User> sendTo;
    private SendLevel level;

    public Notification(){
        this.read = false;
    }

    //---------- Message --------------------
    public String getMessage() {
        return message;
    }

    public boolean setMessage(String message) {
        if(message.length() > 300 || message.isEmpty()) return false;

        this.message = message;
        return true;
    }

    //---------- Read --------------------
    public boolean isRead() {
        return read;
    }

    public void markRead() {
        this.read = true;
    }

    public void markUnread(){
        this.read = false;
    }

    //---------- DateTime --------------------
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTimeNow() {
        this.dateTime = LocalDateTime.now();
    }

    //---------- SentFrom --------------------
    public User getSentFrom() {
        return sentFrom;
    }

    public void setSentFrom(User sentFrom) {
        this.sentFrom = sentFrom;
    }

    //---------- SendTo --------------------
    public ArrayList<User> getSendTo() {
        return sendTo;
    }

    public void setSendTo(ArrayList<User> sendTo){
        if(this.level != SendLevel.Specific) return;

        this.sendTo = sendTo;
    }

    //---------- Level --------------------
    public SendLevel getLevel() {
        return level;
    }

    public void setLevel(SendLevel level) {
        this.level = level;
    }

    public enum SendLevel{
        Specific,
        Entrants,
        Users,
        Organizers,
        Admins,
        All
    }
}
