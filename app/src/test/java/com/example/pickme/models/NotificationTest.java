package com.example.pickme.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.firebase.Timestamp;

import org.junit.Test;

import java.util.ArrayList;

public class NotificationTest {
    private Notification mockNotification(){return new Notification();}

    @Test
    public void testMessage(){
        Notification notification = mockNotification();

        assertFalse(notification.setMessage(""));

        String s = "";
        for(int i = 0; i < 40; i++){
            s.concat("0123456789");
        }
        assertFalse(notification.setMessage(s)); //messages longer than 300 chars are not allowed

        assertTrue(notification.setMessage("test :)"));

        assertEquals(notification.getMessage(), "test :)");
    }

    @Test
    public void testSendIDs(){
        Notification notification = mockNotification();

        notification.setSentFrom("user1");

        assertEquals(notification.getSentFrom(), "user1");

        ArrayList<String> sendTo = new ArrayList<>();

        for(Integer i = 0; i < 5; i++){
            sendTo.add("user" + i.toString());
        }

        notification.setSendTo(sendTo);

        for(Integer i = 0; i < 5; i++){
            assertEquals(notification.getSendTo().get(i), "user" + i.toString());
        }
    }

    @Test
    public void testDateTimeNow(){
        Notification notification = mockNotification();

        notification.setDateTimeNow();

        assertTrue(Math.abs(Timestamp.now().getSeconds() - notification.getTimestamp().getSeconds()) < 5);
    }
}
