package com.example.pickme.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.pickme.R;
import com.example.pickme.models.Facility;
import com.example.pickme.models.User;
import com.example.pickme.repositories.FacilityRepository;

/**
 * A Fragment that handles the creation of a new Facility.
 */
public class FacilityCreationFragment extends Fragment {

    private FacilityRepository facilityRepository;
    private EditText facilityNameEditText;
    private EditText facilityLocationEditText;
    private Button createFacilityButton;

    /**
     * Inflates the layout for this fragment.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The View for the fragment's UI.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.facility_create, container, false);
    }

    /**
     * Called immediately after onCreateView has returned, but before any saved state has been restored in to the view.
     *
     * @param view The View returned by onCreateView.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        facilityRepository = FacilityRepository.getInstance();
        facilityNameEditText = view.findViewById(R.id.facilityNameEditText);
        facilityLocationEditText = view.findViewById(R.id.facilityLocationEditText);
        createFacilityButton = view.findViewById(R.id.createFacilityButton);

        createFacilityButton.setOnClickListener(v -> createFacility());
    }

    /**
     * Creates a new Facility using the input from the user.
     * If the facility is successfully created, navigates to the MyEventsFragment.
     * If the creation fails, shows a Toast message.
     */
    private void createFacility() {
        String facilityName = facilityNameEditText.getText().toString().trim();
        String facilityLocation = facilityLocationEditText.getText().toString().trim();
        User user = User.getInstance();

        if (user != null && !facilityName.isEmpty()) {
            Facility facility = new Facility(user.getDeviceId(), facilityName, facilityLocation);
            facilityRepository.addFacility(facility, task -> {
                if (task.isSuccessful()) {
                    navigateToMyEventsFragment();
                } else {
                    Toast.makeText(requireActivity(), "Failed creating facility", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Navigates to the MyEventsFragment.
     */
    private void navigateToMyEventsFragment() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_global_myEventsFragment);
    }
}