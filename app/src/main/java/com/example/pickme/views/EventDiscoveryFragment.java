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

public class EventDiscoveryFragment extends Fragment implements EventDiscoverdapter.OnEventClickListener{

    private EventEventdiscoveryBinding binding;
    private EventDiscoverdapter eventAdapter;
    private List<Event> eventList = new ArrayList<>();
    private EventViewModel eventViewModel = new EventViewModel();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = EventEventdiscoveryBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventAdapter = new EventDiscoverdapter(eventList, requireActivity(), this);
        binding.recyclerView.setAdapter(eventAdapter);

        binding.searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                eventAdapter.filterEvents(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed here
            }
        });
        fetchEventsFromFirestore();
    }

    private void fetchEventsFromFirestore() {
        eventViewModel.fetchEvents(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Handle successful fetch
                    List<Event> fetchedEvents = eventViewModel.getEvents();
                    eventAdapter.updateEvents(fetchedEvents);
                }
            }
        });
    }

    @Override
    public void onEventClick(Event event) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("selectedEvent", event);

        Navigation.findNavController(requireView()).navigate(R.id.action_eventListFragment_to_eventDetailsFragment, bundle);
    }
}