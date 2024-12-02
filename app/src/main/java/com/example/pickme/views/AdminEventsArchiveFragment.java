package com.example.pickme.views;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.pickme.R;
import com.example.pickme.databinding.FragmentAdminEventsBinding;
import com.example.pickme.models.Event;
import com.example.pickme.repositories.EventRepository;
import com.example.pickme.views.adapters.AdminEventAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment displaying all the apps events for administrative purposes.
 * Provides functionality to refresh the event list, deleting events, and search/filter events.
 */
public class AdminEventsArchiveFragment extends Fragment {

    private FragmentAdminEventsBinding binding;
    private EventRepository eventRepository;
    private final List<Event> eventList = new ArrayList<>();
    private AdminEventAdapter eventAdapter;

    /**
     * Called to inflate the fragment's view.
     *
     * @param inflater The LayoutInflater object to inflate the view.
     * @param container The container this fragment is attached to.
     * @param savedInstanceState The saved instance state if the fragment is being re-created.
     * @return The root view of the fragment.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminEventsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Called after the fragment's view has been created. Initializes repositories,
     * sets up the event adapter, and handles UI interactions like refreshing the list and searching.
     *
     * @param view The root view of the fragment.
     * @param savedInstanceState The saved instance state from the last session.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventRepository = EventRepository.getInstance();

        // Refresh button logic
        ImageView refreshButton = view.findViewById(R.id.imageCatalogRefresh);
        refreshButton.setOnClickListener(v ->  {
            eventList.clear();
            eventAdapter.updateList(eventList);
            binding.noEventsText.setVisibility(View.GONE);
            loadEvents();
        });

        // Back button logic
        binding.imageCatalogBack.setOnClickListener(listener -> Navigation.findNavController(requireView()).navigateUp());

        // Setting up the RecyclerView
        eventAdapter = new AdminEventAdapter(requireActivity(), eventList, (event, position) -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("selectedEvent", event);
            Navigation.findNavController(requireView()).navigate(R.id.action_eventsArchiveFragment_to_eventDetailsFragment, bundle);
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.recyclerView.setAdapter(eventAdapter);

        // Search bar functionality
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
     * Loads events from the event repository and updates the event list.
     * This method fetches data from eventRepository.
     */
    private void loadEvents() {
        eventList.clear();
        binding.noEventsText.setVisibility(View.GONE);

        eventRepository.getAllEvents(task -> {
            if (task.isSuccessful()) {
                List<Event> events = task.getResult();
                if (events != null) {
                    eventList.clear();
                    eventList.addAll(events);
                    eventAdapter.updateList(eventList);
                    binding.noEventsText.setVisibility(eventList.isEmpty() ? View.VISIBLE : View.GONE);
                } else{
                    binding.noEventsText.setVisibility(View.VISIBLE);
                }
            } else {
                binding.noEventsText.setVisibility(View.VISIBLE);
            }
        });
    }
}
