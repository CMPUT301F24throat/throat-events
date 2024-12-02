package com.example.pickme.views.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickme.R;
import com.example.pickme.models.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter class for displaying a list of events in a RecyclerView for administrative purposes.
 * Provides functionality for displaying event details and handling click events.
 */
public class AdminEventAdapter extends RecyclerView.Adapter<AdminEventAdapter.EventViewHolder> {

    private final List<Event> originalEventList;
    private List<Event> filteredEventList;
    private final Context context;
    private final OnItemClickListener onItemClickListener;

    /**
     * Constructor to initialize the adapter with a list of events and click listener.
     *
     * @param context The context for inflating the views.
     * @param eventList The list of events to display.
     * @param onItemClickListener Listener for item click events.
     */
    public AdminEventAdapter(Context context, List<Event> eventList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.originalEventList = new ArrayList<>(eventList); // Copy the original list
        this.filteredEventList = new ArrayList<>(eventList); // Initialize filtered list
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * Interface for handling click events on event items.
     */
    public interface OnItemClickListener {
        void onItemClick(Event event, int position);
    }

    /**
     * Creates and returns a new ViewHolder for event items.
     *
     * @param parent The parent view group where the view holder will be added.
     * @param viewType The type of view to be created.
     * @return A new EventViewHolder instance.
     */
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_event_archive_item_view, parent, false);
        return new EventViewHolder(view);
    }

    /**
     * Binds the event data to the views in the ViewHolder.
     *
     * @param holder The ViewHolder where the data should be set.
     * @param position The position of the item within the RecyclerView.
     */
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = filteredEventList.get(position);
        holder.eventTitleTextView.setText(event.getEventTitle());
        holder.eventCountTextView.setText(String.valueOf(position + 1));
        holder.viewDetails.setOnClickListener(v -> onItemClickListener.onItemClick(event, position));
    }

    /**
     * Returns the total number of items in the filtered list.
     *
     * @return The size of the filtered event list.
     */
    @Override
    public int getItemCount() {
        return filteredEventList.size();
    }

    /**
     * ViewHolder class for holding the views for each event item.
     */
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventTitleTextView;
        TextView eventCountTextView;
        ImageView viewDetails;

        /**
         * Constructor to initialize the views in the ViewHolder.
         *
         * @param itemView The item view that holds the event views.
         */
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTitleTextView = itemView.findViewById(R.id.eventTitle);
            eventCountTextView = itemView.findViewById(R.id.idTitle);
            viewDetails = itemView.findViewById(R.id.viewDetails);
        }
    }

    /**
     * Updates the list of events in the adapter and refreshes the displayed data.
     *
     * @param eventList The new list of events to be displayed in the RecyclerView.
     */
    public void updateList(List<Event> eventList) {
        filteredEventList.clear();
        filteredEventList.addAll(eventList);
        notifyDataSetChanged();
    }

    /**
     * Filters the list of users based on a search query and updates the displayed list.
     *
     * @param query The search query to filter the users by.
     */
    public void filter(String query) {
        query = query.toLowerCase().trim();

        if (TextUtils.isEmpty(query)) {
            filteredEventList = new ArrayList<>(originalEventList);
        } else {
            filteredEventList.clear();

            for (Event event : originalEventList) {
                String eventTitle = event.getEventTitle().toLowerCase();
                String eventDescription = event.getEventDescription().toLowerCase(); // Assuming this exists

                // Check if the event title or description contains the query
                if (eventTitle.contains(query) || eventDescription.contains(query)) {
                    filteredEventList.add(event);
                }
            }
        }
        notifyDataSetChanged();  // Notify the adapter that the data has changed
    }
}
