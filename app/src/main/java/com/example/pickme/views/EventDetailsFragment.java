package com.example.pickme.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.pickme.R;
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
 */
public class EventDetailsFragment extends Fragment {
    private Event event;
    private User currentUser;

    // Inflates the layout for the event details fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.event_details, container, false);
    }

    // Sets up the UI and initializes the event data
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.eventDetails_backBtn).setOnClickListener(listener -> Navigation.findNavController(requireView()).navigateUp());

        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("selectedEvent");
            currentUser = User.getInstance();

            displayEventDetails(view);
        } else {
            // Handle the case where no event is passed
            Navigation.findNavController(requireView()).navigateUp();
        }

        view.findViewById(R.id.eventDetails_backBtn).setOnClickListener(listener -> Navigation.findNavController(requireView()).navigateUp());

        // Set up navigation to QRCodeViewFragment
        view.findViewById(R.id.eventDetails_qrBtn).setOnClickListener(v -> {
            if (event != null) {
                String eventID = event.getEventId();
                Bundle args = new Bundle();
                args.putString("eventID", eventID);
                Navigation.findNavController(requireView()).navigate(R.id.action_eventDetailsFragment_to_QRCodeViewFragment, args);
            } else {
                Toast.makeText(getContext(), "Event ID not available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Displays the event information in the UI
    private void displayEventDetails(View view) {
        if (event != null) {
            if (currentUser.isAdmin()) {
                view.findViewById(R.id.scanQr).setVisibility(View.GONE);
            }

            ((TextView) view.findViewById(R.id.eventDetails_eventTitle)).setText(event.getEventTitle());
            ((TextView) view.findViewById(R.id.eventDetails_eventDesc)).setText(event.getEventDescription());
            ((TextView) view.findViewById(R.id.eventDetails_dateTime)).setText(event.getEventDate());
            ((TextView) view.findViewById(R.id.eventDetails_location)).setText(event.getEventLocation());
            ((TextView) view.findViewById(R.id.eventDetails_maxWinners)).setText(event.getMaxWinners() + (event.getMaxWinners() == 1 ? " Winner" : " Winners"));
            ((TextView) view.findViewById(R.id.eventDetails_maxEntrants)).setText(event.getMaxEntrants() + (event.getMaxEntrants() == 1 ? " Entrant" : " Entrants"));

            Image image = new Image("1234567890", "123456789");
            image.download(new ImageQuery() {
                @Override
                public void onSuccess(Image image) {
                    if (isAdded()) {
                        Glide.with(view)
                                .load(image.getImageUrl())
                                .into((ImageView) view.findViewById(R.id.eventDetails_poster));
                    }
                }

                @Override
                public void onEmpty() {}
            });

            ImageButton createNotif = view.findViewById(R.id.createNotif);

            createNotif.setOnClickListener(l -> {
                Bundle bundle = new Bundle();
                bundle.putString("EventID", event.getEventId());
                Navigation.findNavController(view).navigate(R.id.action_eventDetailsFragment_to_createNotif, bundle);
            });
        }
    }
}