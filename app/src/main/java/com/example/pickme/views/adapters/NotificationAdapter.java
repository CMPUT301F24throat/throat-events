package com.example.pickme.views.adapters;

import static androidx.core.content.ContextCompat.getColor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.pickme.R;
import com.example.pickme.models.Notification;
import com.example.pickme.repositories.EventRepository;

import java.util.ArrayList;

/**
 * Adapter class for displaying notifications in a ListView on the Home screen.
 * This adapter binds notification data to the views in the list item layout.
 */
public class NotificationAdapter extends ArrayAdapter<Notification> {
    private Context context;
    private ArrayList<Notification> notifications;

    /**
     * Constructor for NotificationAdapter.
     *
     * @param context The context in which the adapter is used.
     * @param notifications The list of notifications to display.
     */
    public NotificationAdapter(Context context, ArrayList<Notification> notifications) {
        super(context, 0, notifications);
        this.context = context;
        this.notifications = notifications;
    }

    /**
     * Provides a view for an adapter view (ListView, GridView, etc.).
     *
     * @param position The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Reuse the view if possible
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.notif_list_view, parent, false);
        }

        // Get the notification for this position
        Notification notification = notifications.get(position);

        // Bind data to the views
        TextView eventName = convertView.findViewById(R.id.eventName);
        TextView message = convertView.findViewById(R.id.notifMessage);

        // Fetch the event title and set it to the eventName TextView
        EventRepository.getInstance().getEventById(notification.getEventID(), task -> {
            eventName.setText(task.getResult().getEventTitle());
        });

        // Set the notification message
        message.setText(notification.getMessage());

        // Set the background color based on the read status of the notification
        convertView.setBackgroundColor(notification.isRead() ?
                getColor(getContext(), R.color.readNotif) : getColor(getContext(), R.color.background3));

        return convertView;
    }
}