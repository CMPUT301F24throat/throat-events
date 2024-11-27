package com.example.pickme.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.pickme.R;
import com.example.pickme.databinding.EventDetailsBinding;
import com.example.pickme.models.Event;
import com.example.pickme.models.Image;
import com.example.pickme.models.User;
import com.example.pickme.utils.ImageQuery;

/**
 * Fragment to display detailed information about a specific event.
 * This includes the event title, description, date, location, image, and participant limits.
 * Allows users to view all relevant details of the event, and navigate back to the previous screen.
 *
 * @version 2.0
 * @author Ayub Ali
 * Responsibilities:
 * - Display full event details for selected events.
 * - Load and display the event poster image from a stored URL.
 * - Handle back navigation for seamless user experience.
 */
// Fragment that displays complete details of a selected or random event
public class EventDetailsFragment extends Fragment {
    private EventDetailsBinding binding;
    private Event event;

    // Inflates the layout for the event details fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = EventDetailsBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    // Sets up the UI and initializes the event data
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.back.setOnClickListener(listener -> Navigation.findNavController(requireView()).navigateUp());

        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("selectedEvent");
            displayEventDetails();
        } else {
            // Handle the case where no event is passed
            Navigation.findNavController(requireView()).navigateUp();
        }
    }

    // Displays the event information in the UI
    private void displayEventDetails() {
        if (event != null) {
            User user = new User();
            if (user.isAdmin()) {
                binding.scanQr.setVisibility(View.GONE);
            }

            binding.title.setText(event.getEventTitle());
            binding.description.setText(event.getEventDescription());
            binding.date.setText(event.getEventDate());
            binding.address.setText(event.getEventLocation());
            binding.winners.setText(event.getMaxWinners() + (Integer.parseInt(event.getMaxWinners()) == 1 ? " Winner" : " Winners"));
            binding.entrants.setText(event.getMaxEntrants() + (event.getMaxEntrants() == 1 ? " Entrant" : " Entrants"));

            Image image = new Image("1234567890", "123456789");
            image.download(new ImageQuery() {
                @Override
                public void onSuccess(Image image) {
                    if (isAdded()) {
                        Glide.with(binding.getRoot())
                                .load(image.getImageUrl())
                                .into(binding.eventFlyer);
                    }
                }

                @Override
                public void onEmpty() {}
            });

            ImageButton createNotif = binding.createNotif;

            createNotif.setOnClickListener(l -> {
                Bundle bundle = new Bundle();
                bundle.putString("EventID", event.getEventId());
                Navigation.findNavController(getView()).navigate(R.id.action_eventDetailsFragment_to_createNotif, bundle);
            });
        }
    }
}
/*
  Code Sources
  <p>
  ChatGPT:
  - How to navigate fragments and handle back navigation in Android.
  <p>
  Stack Overflow:
  - Using Glide to load images into ImageView in a Fragment
  - Passing data between fragments using Android Architecture Components
  <p>
  Android Developer Documentation:
  - Fragment Lifecycle - Guidelines for managing fragment lifecycle.
  - Navigating with Fragments - Handling fragment navigation.
  - Using Glide for Image Loading - Documentation on image loading with Glide.
 */
