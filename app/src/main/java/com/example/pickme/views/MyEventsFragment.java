package com.example.pickme.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickme.R;
import com.example.pickme.models.Event;
import com.example.pickme.models.User;
import com.example.pickme.repositories.EventRepository;
import com.example.pickme.repositories.FacilityRepository;
import com.example.pickme.views.adapters.EventAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyEventsFragment extends Fragment implements EventAdapter.OnEventClickListener {

    private FacilityRepository facilityRepository;
    private EventRepository eventRepository;
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        facilityRepository = new FacilityRepository();
        eventRepository = new EventRepository();
        User user = User.getInstance();

        recyclerView = view.findViewById(R.id.recyclerView);
        eventAdapter = new EventAdapter(eventList, requireActivity(), this);
        recyclerView.setAdapter(eventAdapter);

        if (user != null) {
            // Check if the user has a facility
            checkUserFacility(user.getUserId());
        }

        FloatingActionButton fabAddEvent = view.findViewById(R.id.fab_add_event);
        fabAddEvent.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_myEventsFragment_to_eventCreationFragment);
        });

        // Observe the result from EventCreationFragment
        getParentFragmentManager().setFragmentResultListener("eventCreated", this, (requestKey, result) -> {
            if (result.getBoolean("eventCreated")) {
                Toast.makeText(requireActivity(), "Event successfully created", Toast.LENGTH_SHORT).show();
                if (user != null) {
                    fetchEvents(user.getUserId());
                }
            }
        });
    }

    private void checkUserFacility(String userId) {
        facilityRepository.getFacilityByOwnerId(userId, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                boolean facilityExists = false;
                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                    if (document.exists()) {
                        facilityExists = true;
                        break;
                    }
                }
                if (facilityExists) {
                    // Facility exists, proceed to show MyEventsFragment view
                    fetchEvents(userId);
                } else {
                    // Facility does not exist, navigate to FacilityCreationFragment
                    navigateToFacilityCreationFragment();
                }
            } else {
                // Handle Firestore access errors
                navigateToFacilityCreationFragment();
            }
        });
    }

    private void fetchEvents(String userId) {
        eventRepository.getEventsByOrganizerId(userId, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                eventList.clear();
                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                    Event event = document.toObject(Event.class);
                    eventList.add(event);
                }
                eventAdapter.notifyDataSetChanged();
                // Show or hide the no events text based on the event list size
                View noEventsText = requireView().findViewById(R.id.noEventsText);
                noEventsText.setVisibility(eventList.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void navigateToFacilityCreationFragment() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_myEventsFragment_to_facilityCreationFragment);
    }

    @Override
    public void onEventClick(Event event) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("selectedEvent", event);
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_myEventsFragment_to_eventDetailsFragment, bundle);
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