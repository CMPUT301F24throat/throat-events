// EventAdapter.java
package com.example.pickme.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickme.databinding.EventsItemViewBinding;
import com.example.pickme.models.Event;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> eventList;
    private Context context;
    private OnEventClickListener listener;

    public EventAdapter(List<Event> eventList, Context context, OnEventClickListener listener) {
        this.eventList = eventList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        EventsItemViewBinding binding = EventsItemViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new EventViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.bind(event, listener);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        private final EventsItemViewBinding binding;

        public EventViewHolder(@NonNull EventsItemViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Event event, OnEventClickListener listener) {
            String[] dateParts = event.getEventDate().split(",", 2);

            if (dateParts.length > 1) {
                binding.time.setText(dateParts[1].trim()); // Set time part
            } else {
                binding.time.setText(event.getEventDate());
            }

            binding.title.setText(event.getEventTitle());
            binding.address.setText(event.getEventLocation());

            // Set the click listener to notify the fragment
            binding.parent.setOnClickListener(v -> listener.onEventClick(event));
        }
    }

    public void updateEvents(List<Event> newEvents){
        eventList.clear();
        eventList = newEvents;
        notifyDataSetChanged();
    }

    // Define an interface for click events
    public interface OnEventClickListener {
        void onEventClick(Event event);
    }
}