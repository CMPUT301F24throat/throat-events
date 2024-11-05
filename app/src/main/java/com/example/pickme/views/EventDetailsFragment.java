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

public class EventDetailsFragment extends Fragment {
    private EventEventdetailsBinding binding;
    private Event event;
    private boolean isUpdate;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = EventEventdetailsBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("selectedEvent");
        }

        binding.back.setOnClickListener(listener -> Navigation.findNavController(requireView()).navigateUp());

        // Set the data to the UI elements
        if (event != null) {
            binding.title.setText(event.getEventTitle());
            binding.description.setText(event.getEventDescription());
            binding.date.setText(event.getEventDate());
            binding.address.setText(event.getEventLocation());
            binding.winners.setText(event.getMaxWinners()+" Winners");
            binding.entrants.setText(event.getMaxEntrants()+" Entrants");
            // Load the poster image using an image loading library (e.g., Glide)
            Image image = new Image("1234567890", "123456789");
            image.download(new ImageQuery() {
                @Override
                public void onSuccess(Image image) {
                    Glide.with(binding.getRoot()).load(image.getImageUrl()).into(binding.eventFlyer);
                }

                @Override
                public void onEmpty() {

                }
            });
        }
    }
}