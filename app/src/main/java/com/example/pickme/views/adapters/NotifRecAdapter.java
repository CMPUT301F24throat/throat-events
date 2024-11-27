package com.example.pickme.views.adapters;

import static androidx.core.content.ContextCompat.getColor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pickme.R;
import com.example.pickme.models.Notification;
import com.example.pickme.repositories.EventRepository;

import java.util.ArrayList;

/**
 * this class is an adapter for Notifications for a RecyclerView used on the Inbox screen
 *
 * @author Omar-Kattan-1
 */
public class NotifRecAdapter extends RecyclerView.Adapter<NotifRecAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Notification> notifications;

    public NotifRecAdapter(Context context, ArrayList<Notification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    // ViewHolder class to hold references to the views
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView eventName;
        public TextView message;

        public ViewHolder(View view) {
            super(view);
            eventName = view.findViewById(R.id.eventName);
            message = view.findViewById(R.id.notifMessage);
        }
    }

    // Called when RecyclerView needs a new ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the item layout
        View itemView = LayoutInflater.from(context).inflate(R.layout.notif_list_view, parent, false);
        return new ViewHolder(itemView);
    }

    // Called to bind data to the ViewHolder
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Notification notification = notifications.get(position);

        // Fetch event details asynchronously
        new EventRepository().getEventById(notification.getEventID(), task -> {
            String eventTitle = task.getResult().getEventTitle();
            holder.eventName.setText(eventTitle);
        });

        // Set the notification message
        holder.message.setText(notification.getMessage());

        // Set the background color based on whether the notification is read
        holder.itemView.setBackgroundColor(notification.isRead() ?
                getColor(context, R.color.readNotif) : getColor(context, R.color.background3));
    }

    // Returns the total number of items in the list
    @Override
    public int getItemCount() {
        return notifications.size();
    }

}
