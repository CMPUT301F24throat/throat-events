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

import com.example.pickme.R;
import com.example.pickme.databinding.EventsMyEventsBinding;
import com.example.pickme.models.Event;
import com.example.pickme.models.User;
import com.example.pickme.repositories.EventRepository;
import com.example.pickme.repositories.FacilityRepository;
import com.example.pickme.views.adapters.EventAdapter;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment representing the user's events screen.
 */
public class MyEventsFragment extends Fragment implements EventAdapter.OnEventClickListener {

    private FacilityRepository facilityRepository;
    private EventsMyEventsBinding binding;
    private EventRepository eventRepository;
    private EventAdapter eventAdapter;
    private ArrayList<Event> eventList = new ArrayList<>();

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = EventsMyEventsBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    /**
     * Called immediately after onCreateView has returned, but before any saved state has been restored in to the view.
     *
     * @param view               The View returned by onCreateView.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        facilityRepository = FacilityRepository.getInstance();
        eventRepository = EventRepository.getInstance();
        User user = User.getInstance();

        eventAdapter = new EventAdapter(eventList, requireActivity(), this);
        binding.recyclerView.setAdapter(eventAdapter);

        if (user != null) {
            // Check if the user has a facility
            checkUserFacility(user.getDeviceId());
        }

        binding.fabAddEvent.setOnClickListener(v -> {
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

    /**
     * Checks if the user has a facility and loads user events if a facility exists.
     *
     * @param userDeviceId The device ID of the user.
     */
    private void checkUserFacility(String userDeviceId) {
        facilityRepository.getFacilityByOwnerDeviceId(userDeviceId, task -> {
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

    /**
     * Loads the user events into the RecyclerView with an option to include past events.
     *
     * @param userDeviceId      The device ID of the user.
     */
    private void loadUserEvents(String userDeviceId) {
        eventRepository.getEventsByOrganizerId(userDeviceId, false, getEventsTask -> {
            if (getEventsTask.isSuccessful()) {
                List<Event> events = getEventsTask.getResult();
                if (events != null) {
                    eventList.clear();
                    eventList.addAll(events);
                    eventAdapter.notifyDataSetChanged();

                    eventRepository.attachList(eventList, () -> {
                        eventList.removeIf(event -> event.getEventId() == null);
                        eventAdapter.notifyDataSetChanged();
                    });

                    // Show or hide the no events text based on the event list size
                    binding.noEventsText.setVisibility(eventList.isEmpty() ? View.VISIBLE : View.GONE);
                }
            }
        });
    }

    /**
     * Navigates to the facility creation screen.
     */
    private void navigateToFacilityCreationFragment() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_myEventsFragment_to_facilityCreationFragment);
    }

    /**
     * Handles the event click and navigates to the event details screen.
     *
     * @param event The event that was clicked.
     */
    @Override
    public void onEventClick(Event event) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("selectedEvent", event);
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_myEventsFragment_to_eventDetailsFragment, bundle);
    }
}

/*
   Coding Sources
   <p>
   Stack Overflow
   - https://stackoverflow.com/questions/60848166/cannot-resolve-method-getparentfragmentmanager
   - https://stackoverflow.com/questions/77237163/android-how-to-share-result-from-one-fragment-to-another-with-fragment-result-a
  */