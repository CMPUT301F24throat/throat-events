package com.example.pickme.views.adapters;

import android.content.Context;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private ArrayList<Event> eventList;
    private Context context;
    private OnEventClickListener listener;

    public EventAdapter(ArrayList<Event> eventList, Context context, OnEventClickListener listener) {
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
            String eventDateTime = event.getEventDate();
            String[] parts = eventDateTime.split(", ");

            if (parts.length == 2) {
                String datePart = parts[0].trim();
                String timePart = parts[1].trim();

                binding.time.setText(timePart);
                binding.title.setText(event.getEventTitle());
                binding.address.setText(event.getEventLocation());

                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d yyyy, h:mm a", Locale.getDefault());
                SimpleDateFormat endTimeFormat = new SimpleDateFormat("MMMM d yyyy, h:mm a", Locale.getDefault());

                try {
                    Date startDate = dateFormat.parse(datePart + ", " + timePart.split(" - ")[0]);
                    Date endDate = endTimeFormat.parse(datePart + ", " + timePart.split(" - ")[1]);

                    Date currentDate = new Date();

                    if (endDate != null && endDate.before(currentDate)) {
                        binding.title.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text3));
                        binding.time.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text3));
                        binding.address.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text3));
                    } else {
                        binding.title.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.white));
                        binding.time.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.white));
                        binding.address.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.white));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    binding.title.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.black));
                }

                binding.parent.setOnClickListener(v -> listener.onEventClick(event));
            }
        }
    }

    public void updateEvents(List<Event> newEvents){
        eventList.clear();
        eventList.addAll(newEvents);
        notifyDataSetChanged();
    }

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }
}