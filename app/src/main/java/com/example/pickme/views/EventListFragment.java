package com.example.pickme.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.pickme.R;
import com.example.pickme.controllers.EventViewModel;
import com.example.pickme.databinding.EventEventslistBinding;
import com.example.pickme.models.Event;
import com.example.pickme.views.adapters.EventAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class EventListFragment extends Fragment implements EventAdapter.OnEventClickListener {

    private EventEventslistBinding binding;
    private EventAdapter eventAdapter;
    private List<Event> eventList = new ArrayList<>();
    private EventViewModel eventViewModel = new EventViewModel();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = EventEventslistBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventAdapter = new EventAdapter(eventList, requireActivity(), this);
        binding.recyclerView.setAdapter(eventAdapter);

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
                } else {
                    // Handle fetch failure
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