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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.pickme.R;
import com.example.pickme.models.Enums.EntrantStatus;
import com.example.pickme.models.Event;
import com.example.pickme.models.Image;
import com.example.pickme.models.User;
import com.example.pickme.models.WaitingListEntrant;
import com.example.pickme.utils.ImageQuery;
import com.example.pickme.utils.LotteryUtils;
import com.example.pickme.utils.WaitingListUtils;
import com.google.firebase.firestore.GeoPoint;

import java.util.Objects;

public class EventDetailsFragment extends Fragment {
    private Event event;
    private User currentUser;
    private WaitingListUtils waitingListUtils;
    private LotteryUtils lotteryUtils;

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

            int waitingEntrantsCount = (int) event.getWaitingList().stream().filter(entrant -> entrant.getStatus() == EntrantStatus.WAITING).count();

            setText(view, R.id.eventDetails_eventTitle, event.getEventTitle());
            setText(view, R.id.eventDetails_eventDesc, event.getEventDescription());
            setText(view, R.id.eventDetails_dateTime, event.getEventDate());
            setText(view, R.id.eventDetails_location, event.getEventLocation());
            setText(view, R.id.eventDetails_maxWinners, event.getMaxWinners() + (event.getMaxWinners() == 1 ? " Winner" : " Winners"));
            setText(view, R.id.eventDetails_maxEntrants, waitingEntrantsCount + " / " + event.getMaxEntrants() + (event.getMaxEntrants() == 1 ? " Entrant" : " Entrants"));

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

        configWaitlistBtn();
    }


    private void configWaitlistBtn() {
        View waitlistBtn = getView().findViewById(R.id.eventDetails_joinWaitlistBtn);
        int waitingEntrantsCount = (int) event.getWaitingList().stream().filter(entrant -> entrant.getStatus() == EntrantStatus.WAITING).count();

        if (waitingEntrantsCount >= event.getMaxEntrants()) {
            ((TextView) waitlistBtn).setText("Waitlist is full - try again later");
            waitlistBtn.setEnabled(false);
            waitlistBtn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.disabledButtonBG));
            ((TextView) waitlistBtn).setTextColor(ContextCompat.getColor(requireContext(), R.color.disabledButtonTxt));
        } else if (event.hasEventPassed()) {
            ((TextView) waitlistBtn).setText("This event has already passed");
            waitlistBtn.setEnabled(false);
            waitlistBtn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.disabledButtonBG));
            ((TextView) waitlistBtn).setTextColor(ContextCompat.getColor(requireContext(), R.color.disabledButtonTxt));
        } else {
            waitlistBtn.setOnClickListener(v -> {

                GeoPoint currentUserLocation = null;
                if (event.isGeoLocationRequired()) {
                    // TODO: Implement geolocation
                }

                waitingListUtils.addEntrantToWaitingList(event.getEventId(), new WaitingListEntrant(currentUser.getDeviceId(), currentUserLocation, EntrantStatus.WAITING), task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "You have been added to the waitlist", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to add to waitlist: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }
    }

    private void configLotteryBtn() {

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