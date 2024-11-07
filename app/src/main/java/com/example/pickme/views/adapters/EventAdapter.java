// EventAdapter.java
package com.example.pickme.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickme.R;
import com.example.pickme.databinding.EventsItemViewBinding;
import com.example.pickme.models.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
            String eventDateTime = event.getEventDate(); // e.g., "October 5 2024, 7:00 PM - 8:00 PM"
            String[] parts = eventDateTime.split(", ");

            // Extract date and times from the string
            if (parts.length == 2) {
                String datePart = parts[0].trim();
                String timePart = parts[1].trim();

                // Set the time view with the parsed time part
                binding.time.setText(timePart);
                binding.title.setText(event.getEventTitle());
                binding.address.setText(event.getEventLocation());

                // Parse the date and end time
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d yyyy, h:mm a", Locale.getDefault());
                SimpleDateFormat endTimeFormat = new SimpleDateFormat("MMMM d yyyy, h:mm a", Locale.getDefault());

                try {
                    Date startDate = dateFormat.parse(datePart + ", " + timePart.split(" - ")[0]);
                    Date endDate = endTimeFormat.parse(datePart + ", " + timePart.split(" - ")[1]);

                    // Get the current date and time
                    Date currentDate = new Date();

                    if (endDate != null && endDate.before(currentDate)) {
                        // If the event has ended, set the title text color to gray
                        binding.title.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text3));
                        binding.time.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text3));
                        binding.address.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text3));
                    } else {
                        // If the event is still upcoming or ongoing, use the default color
                        binding.title.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.white));
                        binding.time.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.white));
                        binding.address.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.white));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    binding.title.setTextColor(Color.BLACK); // Default color in case of parse failure
                }

                // Set the click listener to notify the fragment
                binding.parent.setOnClickListener(v -> listener.onEventClick(event));
            }
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