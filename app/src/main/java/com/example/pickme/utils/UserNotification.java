package com.example.pickme.utils;

public class UserNotification {
    private String notificationID;
    private boolean read;

    public UserNotification(){
        notificationID = null;
        read = false;
    }

    public UserNotification(String notificationID){
        this.notificationID = notificationID;
        read = false;
    }

    public UserNotification(String notificationID, boolean read){
        this.notificationID = notificationID;
        this.read = read;
    }

    public String getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

}
