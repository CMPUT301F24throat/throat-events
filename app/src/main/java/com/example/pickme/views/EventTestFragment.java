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

public class EventTestFragment extends Fragment {

    private EventTestBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = EventTestBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase
        FirebaseApp.initializeApp(view.getContext());

        binding.createEvent.setOnClickListener(view1 -> Navigation.findNavController(view).navigate(R.id.action_eventTestFragment_to_eventCreationActivity2));

        binding.viewEvents.setOnClickListener(view1 -> Navigation.findNavController(view).navigate(R.id.action_eventTestFragment_to_eventDetailsActivity));
    }
}