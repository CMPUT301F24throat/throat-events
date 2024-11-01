package com.example.pickme.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pickme.models.Notification;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Manages local and push notifications
 * Responsibilities:
 * Create and send notifications to users
 * Handle notification preferences (opt-in/opt-out)
 * Handle Firebase Cloud Messaging (FCM) for push notifications
 **/

public class NotificationHelper extends FirebaseMessagingService{
    Notification notification;

    public NotificationHelper(){

    }

    public NotificationHelper(Notification notification){
        this.notification = notification;
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        if(message.getData().isEmpty())
            return;

        Log.i("TAG", message.getData().toString());
    }

    @Override
    public void onNewToken(@NonNull String token) {

    }
}
