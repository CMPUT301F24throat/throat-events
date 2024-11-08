package com.example.pickme.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.pickme.R;
import com.example.pickme.models.User;
import com.example.pickme.repositories.FacilityRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;

public class MyEventsFragment extends Fragment {

    private FacilityRepository facilityRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        facilityRepository = new FacilityRepository();
        User user = User.getInstance();

        if (user != null) {
            checkUserFacility(user.getUserId());
        }

        FloatingActionButton fabAddEvent = view.findViewById(R.id.fab_add_event);
        fabAddEvent.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_myEventsFragment_to_eventCreationFragment);
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
                    showMyEventsView();
                } else {
                    // Facility does not exist, navigate to FacilityFragment
                    navigateToFacilityFragment();
                }
            } else {
                // Handle Firestore access errors
                navigateToFacilityFragment();
            }
        });
    }

    private void showMyEventsView() {
        // Show MyEventsFragment view
        // This method can be used to update the UI if needed
    }

    private void navigateToFacilityFragment() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_myEventsFragment_to_facilityFragment);
    }
}