package com.example.pickme.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.pickme.databinding.EventEventdetailsBinding;
import com.example.pickme.models.Event;
import com.example.pickme.models.Image;
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

public class EventDetailsFragment extends Fragment {
    private EventEventdetailsBinding binding;
    private Event event;

    // Inflates the layout for event details view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = EventEventdetailsBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    // Sets up UI elements and populates data after the view is created
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("selectedEvent");
        }

        binding.back.setOnClickListener(listener -> Navigation.findNavController(requireView()).navigateUp());

        // Populate UI with event data if available
        if (event != null) {
            binding.title.setText(event.getEventTitle());
            binding.description.setText(event.getEventDescription());
            binding.date.setText(event.getEventDate());
            binding.address.setText(event.getEventLocation());
            binding.winners.setText(event.getMaxWinners() + " Winners");
            binding.entrants.setText(event.getMaxEntrants() + " Entrants");

            // Load the event flyer image using Glide
            Image image = new Image("1234567890", "123456789");
            image.download(new ImageQuery() {
                @Override
                public void onSuccess(Image image) {
                    Glide.with(binding.getRoot()).load(image.getImageUrl()).into(binding.eventFlyer);
                }

                @Override
                public void onEmpty() {}
            });
        }
    }
}

/**
 * Code Sources
 *
 * ChatGPT:
 * - "How to navigate fragments and handle back navigation in Android."
 *
 * Stack Overflow:
 * - "Using Glide to load images into ImageView in a Fragment" - https://stackoverflow.com/questions/23978828/how-to-use-glide-in-fragment-to-load-image
 * - "Passing data between fragments using Android Architecture Components" - https://stackoverflow.com/questions/51075486/passing-data-between-fragments-using-android-architecture-components
 *
 * Android Developer Documentation:
 * - [Fragment Lifecycle](https://developer.android.com/guide/fragments/lifecycle) - Guidelines for managing fragment lifecycle.
 * - [Navigating with Fragments](https://developer.android.com/guide/navigation/navigation-getting-started) - Handling fragment navigation.
 * - [Using Glide for Image Loading](https://bumptech.github.io/glide/) - Documentation on image loading with Glide.
 */
