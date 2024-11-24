package com.example.pickme.utils;

import com.example.pickme.models.Notification;

import java.util.ArrayList;

public class NotificationList extends ArrayList<Notification> {
    private static NotificationList instance;

    private NotificationList() {

    }

    public static NotificationList getInstance() {
        if(instance == null)
            instance = new NotificationList();

        return instance;
    }

    public static void setInstance(NotificationList instance) {
        NotificationList.instance = instance;
    }

    public static Notification getByID(String id){
        for(Notification notification : instance){
            if(notification.getNotificationId().equals(id))
                return notification;
        }

        return null;
    }

    @Override
    public boolean add(Notification notification){
        boolean res = super.add(notification);

        instance.sort((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()));

        return res;
    }

    @Override
    public boolean remove(Object o){
        boolean res = super.remove(o);

        instance.sort((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()));

        return res;
    }
}
