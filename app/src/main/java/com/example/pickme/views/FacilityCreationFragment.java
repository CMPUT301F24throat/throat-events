package com.example.pickme.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.pickme.R;
import com.example.pickme.models.Facility;
import com.example.pickme.models.User;
import com.example.pickme.repositories.FacilityRepository;

public class FacilityCreationFragment extends Fragment {

    private FacilityRepository facilityRepository;
    private EditText facilityNameEditText;
    private EditText facilityLocationEditText;
    private Button createFacilityButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_facility, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        facilityRepository = new FacilityRepository();
        facilityNameEditText = view.findViewById(R.id.facilityNameEditText);
        facilityLocationEditText = view.findViewById(R.id.facilityLocationEditText);
        createFacilityButton = view.findViewById(R.id.createFacilityButton);

        createFacilityButton.setOnClickListener(v -> createFacility());
    }

    private void createFacility() {
        String facilityName = facilityNameEditText.getText().toString().trim();
        String facilityLocation = facilityLocationEditText.getText().toString().trim();
        User user = User.getInstance();

        if (user != null && !facilityName.isEmpty()) {
            Facility facility = new Facility(user.getUserId(), facilityName, facilityLocation);
            facilityRepository.addFacility(facility, task -> {
                if (task.isSuccessful()) {
                    navigateToMyEventsFragment();
                } else {


                }
            });
        }
    }

    private void navigateToMyEventsFragment() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_global_myEventsFragment);
    }
}