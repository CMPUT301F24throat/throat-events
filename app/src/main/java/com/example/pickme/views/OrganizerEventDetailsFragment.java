package com.example.pickme.views;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.pickme.R;
import com.example.pickme.databinding.EventOrganizerEventDetailsBinding;
import com.example.pickme.models.Event;
import com.example.pickme.models.Image;
import com.example.pickme.models.User;
import com.example.pickme.utils.ImageQuery;

public class OrganizerEventDetailsFragment extends Fragment {
    private EventOrganizerEventDetailsBinding binding;
    private Event event;

    // Inflates the layout for the event details fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = EventOrganizerEventDetailsBinding.inflate(getLayoutInflater(), container, false);
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
        binding.back.setOnClickListener(listener -> Navigation.findNavController(requireView()).navigateUp());

        binding.startLottery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomDialog();
            }
        });
        // Set up navigation to QRCodeViewFragment
        binding.goToQrView.setOnClickListener(v -> {
            if (event != null) {
                String eventID = event.getEventId();
                Bundle args = new Bundle();
                args.putString("eventID", eventID);
                Navigation.findNavController(requireView()).navigate(R.id.action_organizerEventDetailsFragment_to_QRCodeViewFragment, args);
            } else {
                Toast.makeText(getContext(), "Event ID not available", Toast.LENGTH_SHORT).show();
            }
        });

        binding.edit.setOnClickListener(v -> {
            if (event != null) {
                Bundle args = new Bundle();
                args.putSerializable("selectedEvent", event);
                Navigation.findNavController(requireView()).navigate(R.id.action_organizerEventDetailsFragment_to_eventCreationFragment, args);
            } else {
                Toast.makeText(getContext(), "Event ID not available", Toast.LENGTH_SHORT).show();
            }
        });

        binding.tapForMore.setOnClickListener(v -> {
            if (event != null) {
                Bundle args = new Bundle();
                args.putSerializable("selectedEvent", event);
                Navigation.findNavController(requireView()).navigate(R.id.action_organizerEventDetailsFragment_to_eventWaitingListFragment, args);
            } else {
                Toast.makeText(getContext(), "Event ID not available", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showCustomDialog() {
        Dialog dialog = new Dialog(requireContext()); // Use your activity context
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.event_lottery_dialog); // Use your custom layout XML

        // Set dialog to use full width
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        // Find and set up views from the custom dialog layout
        TextView cancelButton = dialog.findViewById(R.id.cancel); // Replace with your button ID
        TextView confirmButton = dialog.findViewById(R.id.start); // Replace with your button ID

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Dismiss the dialog when cancel is clicked
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform desired actions here
                Bundle args = new Bundle();
                args.putSerializable("selectedEvent", event);
                Navigation.findNavController(requireView()).navigate(R.id.action_organizerEventDetailsFragment_to_lotteryWinnersFragment, args);
                dialog.dismiss(); // Dismiss the dialog when confirm is clicked
            }
        });

        // Show the dialog
        dialog.show();
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

            Image image = new Image(event.getOrganizerId(), event.getEventId());
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

            binding.createNotif.setOnClickListener(l -> {
                Bundle bundle = new Bundle();
                bundle.putString("EventID", event.getEventId());
                Navigation.findNavController(getView()).navigate(R.id.action_organizerEventDetailsFragment_to_createNotif, bundle);
            });
        }
    }
}