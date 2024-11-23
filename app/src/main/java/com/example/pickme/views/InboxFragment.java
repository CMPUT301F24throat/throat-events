package com.example.pickme.views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pickme.R;
import com.example.pickme.models.Notification;
import com.example.pickme.models.User;
import com.example.pickme.repositories.NotificationRepository;
import com.example.pickme.utils.UserNotification;
import com.example.pickme.views.adapters.NotificationAdapter;

import java.util.ArrayList;

public class InboxFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inbox, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        ArrayList<Notification> notifications = new ArrayList<Notification>();

        NotificationAdapter notificationAdapter = new NotificationAdapter(getContext(), notifications);
        ListView listView = view.findViewById(R.id.notificationsList);

        listView.setAdapter(notificationAdapter);

        User user = User.getInstance();

        NotificationRepository notificationRepository = new NotificationRepository();
        for(UserNotification userNotification : user.getUserNotifications()){
            Log.i("NOTIF", "NotifID: " + userNotification.getNotificationID());
            notificationRepository.getNotificationById(userNotification.getNotificationID(), documentSnapshot -> {
                Log.i("NOTIF", "DOCID: " + documentSnapshot.getId());

                Notification notification = documentSnapshot.toObject(Notification.class);
                if(userNotification.isRead())
                    notification.markRead();

                notifications.add(notification);

                notificationAdapter.notifyDataSetChanged();
            });
        }

//        Notification notif1 = new Notification();
//        notif1.setEventID("RFcDhogc3mHnI0uleHnt");
//        notif1.setMessage("Test notif 1");
//
//        Notification notif2 = new Notification();
//        notif2.setEventID("RFcDhogc3mHnI0uleHnt");
//        notif2.setMessage("a super duper duper duper duper duper duper duper duper duper duper long message just to see what happens");
//        notif2.markRead();
//
//        notifications.add(notif1);
//        notifications.add(notif2);



    }
}