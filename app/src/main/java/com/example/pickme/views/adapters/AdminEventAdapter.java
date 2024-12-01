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

public class AdminEventAdapter extends RecyclerView.Adapter<AdminEventAdapter.EventViewHolder> {

    private final List<Event> originalEventList;
    private List<Event> filteredEventList;
    private final Context context;
    private final OnItemClickListener onItemClickListener;

    public AdminEventAdapter(Context context, List<Event> eventList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.originalEventList = new ArrayList<>(eventList); // Copy the original list
        this.filteredEventList = new ArrayList<>(eventList); // Initialize filtered list
        this.onItemClickListener = onItemClickListener;
    }

    // Define an interface for click listener
    public interface OnItemClickListener {
        void onItemClick(Event event, int position);
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_event_archive_item_view, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = filteredEventList.get(position);
        holder.eventTitleTextView.setText(event.getEventTitle());
        holder.eventCountTextView.setText(String.valueOf(position + 1));
        holder.viewDetails.setOnClickListener(v -> onItemClickListener.onItemClick(event, position));
    }

    @Override
    public int getItemCount() {
        return filteredEventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventTitleTextView;
        TextView eventCountTextView;
        ImageView viewDetails;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTitleTextView = itemView.findViewById(R.id.eventTitle);
            eventCountTextView = itemView.findViewById(R.id.idTitle);
            viewDetails = itemView.findViewById(R.id.viewDetails);
        }
    }

    // Filter method
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
