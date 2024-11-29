package com.example.pickme.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pickme.databinding.EventsEventsArchiveBinding;
import com.example.pickme.models.Event;
import com.example.pickme.repositories.EventRepository;
import com.example.pickme.views.adapters.AdminEventAdapter;
import com.example.pickme.views.adapters.EventAdapter;

import java.util.ArrayList;
import java.util.List;

public class EventsArchiveFragment extends Fragment {
    private EventsEventsArchiveBinding binding;

    private EventRepository eventRepository;
    private List<Event> eventList = new ArrayList<>();
    private AdminEventAdapter eventAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = EventsEventsArchiveBinding.inflate(getLayoutInflater(),container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eventRepository = new EventRepository();

        eventAdapter = new AdminEventAdapter(requireActivity(), eventList);
        binding.recyclerView.setAdapter(eventAdapter);
        loadEvents();
    }

    private void loadEvents() {
=        eventRepository.getAllEvents(task -> {
            if (task.isSuccessful()) {
                List<Event> events = task.getResult();
                if (events != null) {
                    eventList.clear();
                    eventList.addAll(events);
                    eventAdapter.notifyDataSetChanged();
                    // Show or hide the no events text based on the event list size
                    binding.noEventsText.setVisibility(eventList.isEmpty() ? View.VISIBLE : View.GONE);
                }
            }
        });
    }
}