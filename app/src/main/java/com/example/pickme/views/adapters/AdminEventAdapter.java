package com.example.pickme.views.adapters;

import android.content.Context;
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
 * Adapter class for displaying events in a RecyclerView for admin users.
 */
public class AdminEventAdapter extends RecyclerView.Adapter<AdminEventAdapter.EventViewHolder> {

    private final List<Event> originalEventList;
    private List<Event> filteredEventList;
    private final Context context;
    private final OnItemClickListener onItemClickListener;

    /**
     * Constructor for AdminEventAdapter.
     *
     * @param context The context in which the adapter is used.
     * @param eventList The list of events to display.
     * @param onItemClickListener The listener for item click events.
     */
    public AdminEventAdapter(Context context, List<Event> eventList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.originalEventList = new ArrayList<>(eventList); // Copy the original list
        this.filteredEventList = new ArrayList<>(eventList); // Initialize filtered list
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * Interface for handling item click events.
     */
    public interface OnItemClickListener {
        void onItemClick(Event event, int position);
    }

    /**
     * Creates and returns a new EventViewHolder.
     *
     * @param parent The parent view group.
     * @param viewType The view type of the new view.
     * @return A new EventViewHolder.
     */
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_event_archive_item_view, parent, false);
        return new EventViewHolder(view);
    }

    /**
     * Binds the data to the ViewHolder.
     *
     * @param holder The ViewHolder to bind data to.
     * @param position The position of the item in the data set.
     */
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = filteredEventList.get(position);
        holder.eventTitleTextView.setText(event.getEventTitle());
        holder.eventCountTextView.setText(String.valueOf(position + 1));
        holder.viewDetails.setOnClickListener(v -> onItemClickListener.onItemClick(event, position));
    }

    /**
     * Returns the total number of items in the data set.
     *
     * @return The total number of items.
     */
    @Override
    public int getItemCount() {
        return filteredEventList.size();
    }

    /**
     * ViewHolder class for event items.
     */
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventTitleTextView;
        TextView eventCountTextView;
        ImageView viewDetails;

        /**
         * Constructor for EventViewHolder.
         *
         * @param itemView The view of the item.
         */
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTitleTextView = itemView.findViewById(R.id.eventTitle);
            eventCountTextView = itemView.findViewById(R.id.idTitle);
            viewDetails = itemView.findViewById(R.id.viewDetails);
        }
    }

    /**
     * Updates the list of events and notifies the adapter.
     *
     * @param arrayList The new list of events.
     */
    public void updateList(List<Event> arrayList) {
        filteredEventList.clear();
        filteredEventList.addAll(arrayList);
        notifyDataSetChanged();
    }

    /**
     * Filters the list of events based on a query.
     *
     * @param query The query to filter events by.
     */
    public void filter(String query) {
        query = query.toLowerCase().trim();
        filteredEventList = new ArrayList<>();
        if (query.isEmpty()) {
            filteredEventList.addAll(originalEventList);
        } else {
            for (Event event : originalEventList) {
                if (event.getEventTitle().toLowerCase().contains(query)) {
                    filteredEventList.add(event);
                }
            }
        }
        notifyDataSetChanged();
    }
}