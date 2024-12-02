package com.example.pickme.utils;

/**
 * The UserNotification class represents a user notification with an ID and read status.
 */
public class UserNotification {
    private String notificationID;
    private boolean read;

    /**
     * Default constructor that initializes the notification with null ID and unread status.
     */
    public UserNotification(){
        notificationID = null;
        read = false;
    }

    /**
     * Constructor that initializes the notification with a specified ID and unread status.
     *
     * @param notificationID the ID of the notification
     */
    public UserNotification(String notificationID){
        this.notificationID = notificationID;
        read = false;
    }

    /**
     * Constructor that initializes the notification with a specified ID and read status.
     *
     * @param notificationID the ID of the notification
     * @param read the read status of the notification
     */
    public UserNotification(String notificationID, boolean read){
        this.notificationID = notificationID;
        this.read = read;
    }

    /**
     * Gets the ID of the notification.
     *
     * @return the notification ID
     */
    public String getNotificationID() {
        return notificationID;
    }

    /**
     * Sets the ID of the notification.
     *
     * @param notificationID the new notification ID
     */
    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    /**
     * Checks if the notification is read.
     *
     * @return true if the notification is read, false otherwise
     */
    public boolean isRead() {
        return read;
    }

    /**
     * Sets the read status of the notification.
     *
     * @param read the new read status
     */
    public void setRead(boolean read) {
        this.read = read;
    }
}