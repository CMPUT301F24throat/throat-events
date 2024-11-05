// EventAdapter.java
package com.example.pickme.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pickme.databinding.EventDiscoverItemViewBinding;
import com.example.pickme.models.Event;
import com.example.pickme.models.Image;
import com.example.pickme.utils.ImageQuery;

import java.util.List;

public class EventDiscoverdapter extends RecyclerView.Adapter<EventDiscoverdapter.EventViewHolder> {
    private List<Event> eventList;
    private List<Event> fullEventList;
    private Context context;
    private OnEventClickListener listener;

    public EventDiscoverdapter(List<Event> eventList, Context context, OnEventClickListener listener) {
        this.eventList = eventList;
        this.context = context;
        this.listener = listener;
        this.fullEventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        EventDiscoverItemViewBinding binding = EventDiscoverItemViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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
        private final EventDiscoverItemViewBinding binding;

        public EventViewHolder(@NonNull EventDiscoverItemViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Event event, OnEventClickListener listener) {
            String[] dateParts = event.getEventDate().split(",", 2);

            if (dateParts.length > 1) {
                binding.date.setText(dateParts[0].trim());
                binding.time.setText(dateParts[1].trim());
            } else {
                binding.time.setText(event.getEventDate());
            }

            Image image = new Image("1234567890", "123456789");
            image.download(new ImageQuery() {
                @Override
                public void onSuccess(Image image) {
                    Glide.with(binding.getRoot()).load(image.getImageUrl()).into(binding.eventFlyer);
                }

                @Override
                public void onEmpty() {

                }
            });

            binding.title.setText(event.getEventTitle());
            binding.address.setText(event.getEventLocation());
            binding.description.setText(event.getEventDescription());

            // Set the click listener to notify the fragment
            binding.parent.setOnClickListener(v -> listener.onEventClick(event));
        }
    }

    public void filterEvents(String query) {
        eventList.clear();

        if (query.isEmpty()) {
            eventList.addAll(fullEventList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Event event : fullEventList) {
                if (event.getEventTitle().toLowerCase().contains(lowerCaseQuery) ||
                        event.getEventLocation().toLowerCase().contains(lowerCaseQuery) ||
                        event.getEventDescription().toLowerCase().contains(lowerCaseQuery)) {
                    eventList.add(event);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateEvents(List<Event> newEvents){
        fullEventList.clear();
        fullEventList.addAll(newEvents);
        eventList.clear();
        eventList.addAll(newEvents);
        notifyDataSetChanged();
    }

    // Define an interface for click events
    public interface OnEventClickListener {
        void onEventClick(Event event);
    }
}