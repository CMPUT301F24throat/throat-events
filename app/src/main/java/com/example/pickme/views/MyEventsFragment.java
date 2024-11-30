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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyEventsFragment extends Fragment implements EventAdapter.OnEventClickListener {

    private FacilityRepository facilityRepository;
    private EventRepository eventRepository;
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private ArrayList<Event> eventList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        facilityRepository = new FacilityRepository();
        eventRepository = EventRepository.getInstance();
        User user = User.getInstance();

        recyclerView = view.findViewById(R.id.recyclerView);
        eventAdapter = new EventAdapter(eventList, requireActivity(), this);
        recyclerView.setAdapter(eventAdapter);

        if (user != null) {
            // Check if the user has a facility
            checkUserFacility(user.getDeviceId());
        }

        FloatingActionButton addEventBtn = view.findViewById(R.id.fab_add_event);
        addEventBtn.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_myEventsFragment_to_eventCreationFragment);
        });

        // Observe the result from EventCreationFragment
        getParentFragmentManager().setFragmentResultListener("eventCreated", this, (requestKey, result) -> {
            if (result.getBoolean("eventCreated")) {
                Toast.makeText(requireActivity(), "Event successfully created", Toast.LENGTH_SHORT).show();
                if (user != null) {
                    loadUserEvents(user.getDeviceId());
                }
            }
        });
    }

    private void checkUserFacility(String userDeviceId) {
        facilityRepository.getFacilityByOwnerId(userDeviceId, task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    // Facility exists, proceed to show MyEventsFragment view
                    loadUserEvents(userDeviceId);
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

    // Method without includePastEvents parameter, defaulting to false
    private void loadUserEvents(String userDeviceId) {
        loadUserEvents(userDeviceId, false);
    }

    // Method with includePastEvents parameter
    private void loadUserEvents(String userDeviceId, boolean includePastEvents) {
        eventRepository.getEventsByOrganizerId(userDeviceId, includePastEvents, getEventsTask -> {
            if (getEventsTask.isSuccessful()) {
                List<Event> events = getEventsTask.getResult();
                if (events != null) {
                    eventList.clear();
                    eventList.addAll(events);
                    eventAdapter.notifyDataSetChanged();

                    // Show or hide the no events text based on the event list size
                    View noEventsText = requireView().findViewById(R.id.noEventsText);
                    noEventsText.setVisibility(eventList.isEmpty() ? View.VISIBLE : View.GONE);
                }
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