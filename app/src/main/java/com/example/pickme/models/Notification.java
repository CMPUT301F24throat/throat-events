package com.example.pickme.models;

import com.google.firebase.Timestamp;
import java.util.ArrayList;

/**
 * Represents a notification alert created by a user
 * <br>
 * This class holds the following:
 * <ul>
 *     <li>Notification Message</li>
 *     <li>Who it was sent from and when</li>
 *     <li>Who it's going to</li>
 * </ul>
 * @author Omar-Kattan-1
 * @version 1.1
 */
public class Notification {
    private String notificationID;
    private String message;

    private boolean read;
    private Timestamp timestamp;

    private String sentFrom;
    private ArrayList<String> sendTo = new ArrayList<String>();
    private SendLevel level;

    private String eventID;

    public Notification(){
        this.read = false;
    }

    //--------- Notification ID -------------
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

    public void markRead(boolean read) {
        this.read = read;
    }

    public void markUnread(){
        this.read = false;
    }

    //---------- DateTime --------------------
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setDateTimeNow() {
        this.timestamp = Timestamp.now();
    }

    //---------- SentFrom --------------------
    public String getSentFrom() {
        return sentFrom;
    }

    public void setSentFrom(String sentFrom) {
        this.sentFrom = sentFrom;
    }

    //---------- SendTo --------------------
    public ArrayList<String> getSendTo() {
        return sendTo;
    }

    public void setSendTo(ArrayList<String> sendTo){
        this.sendTo = sendTo;
    }

    //---------- Level --------------------
    public SendLevel getLevel() {
        return level;
    }

    public void setLevel(SendLevel level) {
        this.level = level;
    }

    //---------- eventID ------------------
    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    /**
     * represents the group of people a notification will be sent to
     */
    public enum SendLevel{
        All,
        Selected,
        Cancelled,
        Rejected
    }
}
