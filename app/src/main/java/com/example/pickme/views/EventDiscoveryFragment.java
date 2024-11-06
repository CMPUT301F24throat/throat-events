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
import com.example.pickme.controllers.EventViewModel;
import com.example.pickme.databinding.EventEventdiscoveryBinding;
import com.example.pickme.models.Event;
import com.example.pickme.views.adapters.EventDiscoverdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for discovering and searching events.
 * Displays a list of available events with search functionality for filtering.
 * Fetches event data from Firestore and handles user interactions for viewing details.
 *
 * @version 2.0
 * @author Ayub Ali
 * Responsibilities:
 * - Display a list of events
 * - Fetch event data from Firestore and update the list.
 */

public class EventDiscoveryFragment extends Fragment implements EventDiscoverdapter.OnEventClickListener {

    private EventEventdiscoveryBinding binding;
    private EventDiscoverdapter eventAdapter;
    private List<Event> eventList = new ArrayList<>();
    private EventViewModel eventViewModel = new EventViewModel();

    // Inflates the layout for the event discovery view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = EventEventdiscoveryBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    // Sets up RecyclerView adapter and search functionality after the view is created
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventAdapter = new EventDiscoverdapter(eventList, requireActivity(), this);
        binding.recyclerView.setAdapter(eventAdapter);

        binding.searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                eventAdapter.filterEvents(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        fetchEventsFromFirestore();
    }

    // Fetches events from Firestore and updates adapter
    private void fetchEventsFromFirestore() {
        eventViewModel.fetchEvents(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Event> fetchedEvents = eventViewModel.getEvents();
                    eventAdapter.updateEvents(fetchedEvents);
                }
            }
        });
    }

    // Handles event click and navigates to details view
    @Override
    public void onEventClick(Event event) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("selectedEvent", event);
        Navigation.findNavController(requireView()).navigate(R.id.action_eventDiscoveryFragment_to_eventDetailsFragment, bundle);
    }
}

/**
 * Code Sources
 *
 * Stack Overflow:
 * - "Setting up RecyclerView with search filtering" - https://stackoverflow.com/questions/30398247/how-to-filter-a-recyclerview-with-a-searchview
 * - "Passing data between fragments with Navigation Component" - https://stackoverflow.com/questions/51075486/passing-data-between-fragments-using-android-architecture-components
 *
 * Firebase Documentation:
 * - [Firestore: Data fetching and filtering](https://firebase.google.com/docs/firestore/query-data/get-data) - Methods for querying and retrieving data.
 *
 * Android Developers:
 * - [Implementing RecyclerView](https://developer.android.com/guide/topics/ui/layout/recyclerview) - Guidelines for displaying a list with RecyclerView.
 * - [Fragment navigation with arguments](https://developer.android.com/guide/navigation/navigation-pass-data) - Navigating and passing data between fragments with the Navigation component.
 */
