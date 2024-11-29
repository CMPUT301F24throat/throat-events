package com.example.pickme.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.Objects;

public class EventDetailsFragment extends Fragment {
    private Event event;
    private User currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.event_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.eventDetails_backBtn).setOnClickListener(listener -> Navigation.findNavController(requireView()).navigateUp());

        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("selectedEvent");
            currentUser = User.getInstance();
            configureView(view, currentUser);
            displayEventDetails(view);
        } else {
            Navigation.findNavController(requireView()).navigateUp();
        }

        view.findViewById(R.id.eventDetails_qrBtn).setOnClickListener(v -> navigateToQRCodeView());
    }

    private void displayEventDetails(View view) {
        if (event != null) {
            if (currentUser.isAdmin()) {
                view.findViewById(R.id.scanQr).setVisibility(View.GONE);
            }

            setText(view, R.id.eventDetails_eventTitle, event.getEventTitle());
            setText(view, R.id.eventDetails_eventDesc, event.getEventDescription());
            setText(view, R.id.eventDetails_dateTime, event.getEventDate());
            setText(view, R.id.eventDetails_location, event.getEventLocation());
            setText(view, R.id.eventDetails_maxWinners, event.getMaxWinners() + (event.getMaxWinners() == 1 ? " Winner" : " Winners"));
            setText(view, R.id.eventDetails_maxEntrants, event.getMaxEntrants() + (event.getMaxEntrants() == 1 ? " Entrant" : " Entrants"));

            loadImage(view, R.id.eventDetails_poster, "1234567890", "123456789");

            view.findViewById(R.id.createNotif).setOnClickListener(l -> navigateToCreateNotif());
        }
    }

    private void configureView(View view, User currentUser) {
        String userDeviceId = currentUser.getDeviceId();
        String organizerId = event.getOrganizerId();

        boolean isAdmin = currentUser.isAdmin();
        boolean isOrganizer = Objects.equals(userDeviceId, organizerId);

        setVisibility(view, R.id.eventDetails_deleteBtn, isAdmin || isOrganizer);
        setVisibility(view, R.id.eventDetails_qrBtn, isAdmin || isOrganizer);
        setVisibility(view, R.id.eventDetails_editBtn, isOrganizer);
        setVisibility(view, R.id.eventDetails_sendNotifBtn, isOrganizer);
        setVisibility(view, R.id.eventDetails_runLotteryBtn, isOrganizer);
        setVisibility(view, R.id.eventDetails_joinWaitlistBtn, !isOrganizer);
    }

    private void configWaitlistBtn() {
        // if waitlist is full, set text to 'Waitlist is full - try again later' and disable button, set bgTint to disabledButtonBG and set text color to disabledButtonTxt

        // if event has passed set text to 'This event has already passed' and disable button, set bgTint to disabledButtonBG and set text color to disabledButtonTxt

        // if neither, leave button as is
    }

    private void configLotterBtn() {

        if (!event.hasLotteryExecuted()) {
            // if lottery hasn't been ran, keep text as 'Run Lottery' and set up click listener to run lottery using lotteryUtils

        } else {
            // if lottery has been ran, set text to 'View Lottery Status' and set up click listener to navigate to lottery status fragment

        }
    }

    private void setText(View view, int viewId, String text) {
        ((TextView) view.findViewById(viewId)).setText(text);
    }

    private void setVisibility(View view, int viewId, boolean isVisible) {
        view.findViewById(viewId).setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    private void loadImage(View view, int imageViewId, String imageId, String imageUrl) {
        Image image = new Image(imageId, imageUrl);
        image.download(new ImageQuery() {
            @Override
            public void onSuccess(Image image) {
                if (isAdded()) {
                    Glide.with(view)
                            .load(image.getImageUrl())
                            .into((ImageView) view.findViewById(imageViewId));
                }
            }

            @Override
            public void onEmpty() {}
        });
    }

    private void navigateToQRCodeView() {
        if (event != null) {
            String eventID = event.getEventId();
            Bundle args = new Bundle();
            args.putString("eventID", eventID);
            Navigation.findNavController(requireView()).navigate(R.id.action_eventDetailsFragment_to_QRCodeViewFragment, args);
        } else {
            Toast.makeText(getContext(), "Event ID not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToCreateNotif() {
        Bundle bundle = new Bundle();
        bundle.putString("EventID", event.getEventId());
        Navigation.findNavController(requireView()).navigate(R.id.action_eventDetailsFragment_to_createNotif, bundle);
    }
}