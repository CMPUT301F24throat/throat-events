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

import java.util.List;

public class AdminEventAdapter extends RecyclerView.Adapter<AdminEventAdapter.EventViewHolder> {

    private final List<Event> eventList;
    private final Context context;
    private final OnItemClickListener onItemClickListener;

    public AdminEventAdapter(Context context, List<Event> eventList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.eventList = eventList;
        this.onItemClickListener = onItemClickListener;
    }

    // Define an interface for click listener
    public interface OnItemClickListener {
        void onItemClick(Event event, int position);
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout
        View view = LayoutInflater.from(context).inflate(R.layout.admin_event_archive_item_view, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        // Bind data to the view
        Event event = eventList.get(position);
        holder.eventTitleTextView.setText(event.getEventTitle());
        holder.eventCountTextView.setText(position+1);
        // Set the click listener for the item
        holder.viewDetails.setOnClickListener(v -> onItemClickListener.onItemClick(event, position));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    // ViewHolder class
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventTitleTextView;
        TextView eventCountTextView;
        ImageView viewDetails;
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            // Bind the TextView
            eventTitleTextView = itemView.findViewById(R.id.eventTitle);
            eventCountTextView = itemView.findViewById(R.id.idTitle);
            viewDetails = itemView.findViewById(R.id.viewDetails);
        }
    }
}