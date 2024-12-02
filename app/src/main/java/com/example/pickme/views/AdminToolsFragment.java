package com.example.pickme.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.pickme.R;

/**
 * Fragment for admin tools.
 */
public class AdminToolsFragment extends Fragment {

    private Button adminGalleryButton, userProfilesButton, eventArchiveButton;

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_tools, container, false);
    }

    /**
     * Called immediately after onCreateView has returned, but before any saved state has been restored in to the view.
     *
     * @param view The View returned by onCreateView.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        // Initialize the admin gallery button
        adminGalleryButton = view.findViewById(R.id.adminGalleryButton);
        userProfilesButton = view.findViewById(R.id.userProfilesButton);
        eventArchiveButton = view.findViewById(R.id.eventArchiveButton);

        // Set click listener to navigate to the admin gallery fragment
        eventArchiveButton.setOnClickListener(v -> Navigation
                .findNavController(view)
                .navigate(R.id.action_adminToolsFragment_to_eventsArchiveFragment));
        userProfilesButton.setOnClickListener(v -> Navigation
                .findNavController(view)
                .navigate(R.id.action_adminToolsFragment_to_adminUserProfilesFragment));
        adminGalleryButton.setOnClickListener(v -> Navigation
                .findNavController(view)
                .navigate(R.id.action_adminToolsFragment_to_adminGalleryFragment));
    }
}