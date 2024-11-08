package com.example.pickme.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pickme.controllers.EventViewModel;
import com.example.pickme.databinding.EventEventslistBinding;
import com.example.pickme.models.Event;
import com.example.pickme.views.adapters.EventAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment displaying a list of events with navigation to event creation.
 *
 * @version 2.0
 * @author Ayub Ali
 * Responsibilities:
 * - Display a list of events from Firestore
 * - Provide navigation to event creation or editing
 */

public class EventListFragment extends Fragment implements EventAdapter.OnEventClickListener {

    private EventEventslistBinding binding;
    private EventAdapter eventAdapter;
    private List<Event> eventList = new ArrayList<>();
    private EventViewModel eventViewModel = new EventViewModel();

    // Inflate the layout for the fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = EventEventslistBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    // Set up RecyclerView adapter and fetch data after the view is created
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventAdapter = new EventAdapter(eventList, requireActivity(), this);
        binding.recyclerView.setAdapter(eventAdapter);

        fetchEventsFromFirestore();
    }

    // Fetches event data from Firestore
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

    // Handle event item click and navigate to event creation/edit screen
    @Override
    public void onEventClick(Event event) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("selectedEvent", event);

        //Navigation.findNavController(requireView()).navigate(R.id.action_eventListFragment_to_eventCreationFragment, bundle);
    }
}

/**
 * Code Sources
 *
 * Stack Overflow:
 * - RecyclerView setup and data binding in Android
 *
 * Firebase Documentation:
 * - Firestore: Fetching and displaying collection data
 *
 * Android Developers:
 * - Navigation components for data transfer between fragments]- Navigating and passing data between fragments with the Navigation component.
 */
