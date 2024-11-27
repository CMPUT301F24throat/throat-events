package com.example.pickme.utils;

import com.example.pickme.models.Notification;

import java.util.ArrayList;

/**
 * This class acts as an ArrayList of Notification objects, with slight modifications:
 * - Singleton pattern since the user only ever has a single list of notifications
 * - automatically sorts the list after every add/remove from newest to oldest
 *
 * @author Omar-Kattan-1
 */
public class NotificationList extends ArrayList<Notification> {
    private static NotificationList instance;

    /**
     * empty constructor
     */
    private NotificationList() {

    }

    /**
     * returns the current instance of this class, if there isn't one, create one and return it
     * @return the instance of this class
     */
    public static NotificationList getInstance() {
        if(instance == null)
            instance = new NotificationList();

        return instance;
    }

    /**
     * finds a notification object in the list based on its ID
     * @param id the ID of the notification to find
     * @return the notification object that corresponds to the ID, or null if there isn't one
     */
    public static Notification getByID(String id){
        for(Notification notification : instance){
            if(notification.getNotificationId().equals(id))
                return notification;
        }

        return null;
    }

    /**
     * adds a Notification to the list, and sorts it so it remains in order from newest to oldest
     * @param notification the notification to add
     * @return as specified by ArrayList
     */
    @Override
    public boolean add(Notification notification){
        boolean res = super.add(notification);

        instance.sort((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()));

        return res;
    }

    /**
     * removes a Notification from the list, and sorts it so it remains in order from newest to oldest
     * @param o the object to remove
     * @return as specified by ArrayList
     */
    @Override
    public boolean remove(Object o){
        boolean res = super.remove(o);

        instance.sort((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()));

        return res;
    }
}
