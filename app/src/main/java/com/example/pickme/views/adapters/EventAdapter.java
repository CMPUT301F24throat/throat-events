// EventAdapter.java
package com.example.pickme.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickme.databinding.EventsItemViewBinding;
import com.example.pickme.models.Event;
import com.example.pickme.views.EventDetailsActivity;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> eventList;
    private Context context;

    public EventAdapter(List<Event> eventList, Context context) {
        this.eventList = eventList;
        this.context = context;
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
        holder.bind(event, context);
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

        public void bind(Event event, Context context) {
            String[] dateParts = event.getEventDate().split(",", 2);

            if (dateParts.length > 1) {
                binding.time.setText(dateParts[1].trim()); // Set time part
            } else {
                // If there's no comma, just display the full string in dateTextView
                binding.time.setText(event.getEventDate());
            }

            binding.title.setText(event.getEventTitle());
            binding.address.setText(event.getEventLocation());

            binding.parent.setOnClickListener(v -> {
                Intent intent = new Intent(context, EventDetailsActivity.class);
                intent.putExtra("selectedEvent", event); // Pass the Event object
                context.startActivity(intent);
            });
        }

    }
}