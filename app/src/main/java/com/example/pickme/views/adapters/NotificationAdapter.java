package com.example.pickme.views.adapters;

import static androidx.core.content.ContextCompat.getColor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.pickme.R;
import com.example.pickme.models.Notification;
import com.example.pickme.repositories.EventRepository;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

/**
 * this class is an adapter for Notifications for ListView used on the Home screen
 *
 * @author Omar-Kattan-1
 */
public class NotificationAdapter extends ArrayAdapter<Notification> {
    private Context context;
    private ArrayList<Notification> notifications;

    public NotificationAdapter(Context context, ArrayList<Notification> notifications) {
        super(context, 0, notifications);
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Reuse the view if possible
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.notif_list_view, parent, false);
        }

        // Get the notification for this position
        Notification notification = notifications.get(position);

        // Bind data to the views
        TextView eventName = convertView.findViewById(R.id.eventName);
        TextView message = convertView.findViewById(R.id.notifMessage);

        EventRepository.getInstance().getEventById(notification.getEventID(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    eventName.setText(documentSnapshot.getString("eventTitle"));
                }
            }
        });

        message.setText(notification.getMessage());

        convertView.setBackgroundColor(notification.isRead() ?
                getColor(getContext(), R.color.readNotif) : getColor(getContext(), R.color.background3));

        return convertView;
    }
}