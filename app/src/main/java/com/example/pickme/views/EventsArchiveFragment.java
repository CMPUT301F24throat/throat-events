package com.example.pickme.views;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.pickme.R;
import com.example.pickme.databinding.EventsEventsArchiveBinding;
import com.example.pickme.models.Event;
import com.example.pickme.repositories.EventRepository;
import com.example.pickme.views.adapters.AdminEventAdapter;

import java.util.ArrayList;
import java.util.List;

public class EventsArchiveFragment extends Fragment {
    private EventsEventsArchiveBinding binding;
    private EventRepository eventRepository;
    private final List<Event> eventList = new ArrayList<>();
    private AdminEventAdapter eventAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = EventsEventsArchiveBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventRepository = EventRepository.getInstance();

        eventAdapter = new AdminEventAdapter(requireActivity(), eventList, (event, position) -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("selectedEvent", event);
            Navigation.findNavController(requireView()).navigate(R.id.action_eventsArchiveFragment_to_eventDetailsFragment, bundle);
        });
        binding.recyclerView.setAdapter(eventAdapter);
        binding.searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                eventAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        loadEvents();
    }

    /**
     * Loads events from the repository and updates the UI.
     */
    private void loadEvents() {
        eventRepository.getAllEvents(task -> {
            if (task.isSuccessful()) {
                List<Event> events = task.getResult();
                if (events != null) {
                    eventList.clear();
                    eventList.addAll(events);
                    eventAdapter.notifyDataSetChanged();
                    binding.noEventsText.setVisibility(eventList.isEmpty() ? View.VISIBLE : View.GONE);
                }
            } else {
                binding.noEventsText.setVisibility(View.VISIBLE);
            }
        });
    }
}
