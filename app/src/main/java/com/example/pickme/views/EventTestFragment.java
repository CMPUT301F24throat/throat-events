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
import com.example.pickme.databinding.EventTestBinding;
import com.google.firebase.FirebaseApp;

/**
 * Test Fragment for navigating between event-related screens.
 *
 * @version 2.0
 * Responsibilities: Ayub Ali
 * - Provides buttons for navigation to event creation, viewing, and updating screens.
 */

public class EventTestFragment extends Fragment {

    private EventTestBinding binding;

    // Inflate the layout for this fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = EventTestBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    // Set up navigation and initialize Firebase on view creation
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase
        FirebaseApp.initializeApp(view.getContext());

        // Navigate to event creation screen
        binding.createEvent.setOnClickListener(view1 -> Navigation.findNavController(view).navigate(R.id.action_eventTestFragment_to_eventCreationFragment));

        // Navigate to event list screen for updating events
        binding.updateEvents.setOnClickListener(view1 -> Navigation.findNavController(view).navigate(R.id.action_eventTestFragment_to_eventListFragment));
    }
}

/**
 * Code Sources
 *
 * Stack Overflow:
 * - Firebase initialization in fragment lifecycle
 *
 * Android Developers:
 * - Setting up navigation between fragments - Guide on using the Navigation component to transition between fragments.
 */
