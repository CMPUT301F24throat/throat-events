package com.example.pickme.models;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Represents a notification alert created by a user
 * @author Omar-Kattan-1
 * @version 1.0
 */
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

    public void setNotificationId(String notificationID) {
        this.notificationID = notificationID;
    }

    public String getNotificationId() {
        return notificationID;
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
