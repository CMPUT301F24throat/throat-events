package com.example.pickme.views;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.pickme.databinding.DialogGeoLocationBinding;
import com.example.pickme.models.Enums.EntrantStatus;
import com.example.pickme.models.Event;
import com.example.pickme.models.User;
import com.example.pickme.models.WaitingListEntrant;
import com.example.pickme.repositories.EventRepository;
import com.example.pickme.repositories.UserRepository;
import com.example.pickme.utils.GeoLocationUtils;
import com.example.pickme.utils.LotteryUtils;
import com.google.firebase.firestore.GeoPoint;

import java.util.Objects;

/**
 * Fragment to display the details of a selected event.
 */
public class EventDetailsFragment extends Fragment {
    private Event event;
    private User currentUser;
    private EventRepository eventRepository;
    private UserRepository userRepository;

    private boolean alreadyIn = false;
    private LotteryUtils lotteryUtils;
    String logText = "";
    String toastText = "";
    private WaitingListEntrant entrant;

    /**
     * Inflates the layout for this fragment.
     *
     * @param inflater LayoutInflater to inflate the view.
     * @param container ViewGroup container for the fragment.
     * @param savedInstanceState Bundle containing saved state.
     * @return Inflated view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.event_details, container, false);
    }

    /**
     * Called after the view has been created.
     *
     * @param view The created view.
     * @param savedInstanceState Bundle containing saved state.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("selectedEvent");
            currentUser = User.getInstance();

            eventRepository = EventRepository.getInstance();
            userRepository = UserRepository.getInstance();
            lotteryUtils = new LotteryUtils();

            configureView(view, currentUser);
            displayEventDetails(view);
            eventRepository.attachEvent(event, () -> {
                if (event.getEventId() == null){
                    Toast.makeText(getContext(), "Event deleted", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigate(R.id.action_eventDetailsFragment_to_myEventsFragment);
                }
                else{
                    configureView(view, currentUser);
                    displayEventDetails(view);
                }
            });
        } else {
            Navigation.findNavController(requireView()).navigateUp();
        }

        // Set up click listeners for buttons
        view.findViewById(R.id.eventDetails_backBtn).setOnClickListener(listener -> Navigation.findNavController(requireView()).navigateUp());
        view.findViewById(R.id.eventDetails_qrBtn).setOnClickListener(v -> navigateToQRCodeView());
        view.findViewById(R.id.eventDetails_editBtn).setOnClickListener(v -> navigateToEditEvent());
        view.findViewById(R.id.eventDetails_sendNotifBtn).setOnClickListener(v -> navigateToCreateNotif());
        view.findViewById(R.id.eventDetails_moreInfoLink).setOnClickListener(v -> navigateToWaitlistInfo());
        view.findViewById(R.id.eventDetails_deleteBtn).setOnClickListener(v -> deleteEvent());
    }

    /**
     * Displays the details of the event.
     *
     * @param view The view to display the event details in.
     */
    private void displayEventDetails(View view) {
        if (event != null) {
            int waitingEntrantsCount = (int) event.getWaitingList().stream().filter(entrant -> entrant.getStatus() == EntrantStatus.WAITING || entrant.getStatus() == EntrantStatus.REJECTED).count();
            int winnerEntrantsCount  = (int) event.getWaitingList().stream().filter(entrant -> entrant.getStatus() == EntrantStatus.SELECTED || entrant.getStatus() == EntrantStatus.ACCEPTED).count();

            setText(view, R.id.eventDetails_eventTitle, event.getEventTitle() != null ? event.getEventTitle() : " ");
            setText(view, R.id.eventDetails_eventDesc, event.getEventDescription() != null ? event.getEventDescription() : "No description set for the event");
            setText(view, R.id.eventDetails_dateTime, event.getEventDate() != null ? event.getEventDate() : " ");
            setText(view, R.id.eventDetails_location, event.getEventLocation() != null ? event.getEventLocation() : " ");
            setText(view, R.id.eventDetails_maxWinners, event.getMaxWinners() != 0 ? winnerEntrantsCount + " / " + event.getMaxWinners() + (event.getMaxWinners() == 1 ? " Winner" : " Winners") : " ");
            setText(view, R.id.eventDetails_maxEntrants, waitingEntrantsCount + " / " + (event.getMaxEntrants() != null ? event.getMaxEntrants() + (event.getMaxEntrants() == 1 ? " Entrant" : " Entrants") : "No waitlist limit"));

            loadImage(view, R.id.eventDetails_poster, event.getPosterImageId());
        }
    }

    /**
     * Configures the view based on the current user.
     *
     * @param view The view to configure.
     * @param currentUser The current user.
     */
    private void configureView(View view, User currentUser) {
        String userDeviceId = currentUser.getDeviceId();
        String organizerId = event.getOrganizerId();

        boolean isAdmin = currentUser.isAdmin();
        boolean isOrganizer = Objects.equals(userDeviceId, organizerId);

        setVisibility(view, R.id.eventDetails_deleteBtn, isAdmin || isOrganizer);
        setVisibility(view, R.id.eventDetails_qrBtn, isAdmin || isOrganizer);
        setVisibility(view, R.id.eventDetails_editBtn, isOrganizer);
        setVisibility(view, R.id.eventDetails_moreInfoLink, isOrganizer);
        setVisibility(view, R.id.eventDetails_sendNotifBtn, isOrganizer);
        setVisibility(view, R.id.eventDetails_runLotteryBtn, isOrganizer);

        // Default of accept/decline buttons and lotteryResult is gone
        view.findViewById(R.id.eventDetails_joinWaitlistBtn).setVisibility(View.GONE);
        view.findViewById(R.id.eventDetails_acceptInviteBtn).setVisibility(View.GONE);
        view.findViewById(R.id.eventDetails_declineInviteBtn).setVisibility(View.GONE);
        view.findViewById(R.id.eventDetails_selectedText).setVisibility(View.GONE);

        // Waitlist button and response buttons display
        if (event!=null && !event.getHasLotteryExecuted() && !isOrganizer) {
            view.findViewById(R.id.eventDetails_joinWaitlistBtn).setVisibility(View.VISIBLE);
            configWaitlistBtn(view);
        } else if (!isOrganizer) {
            // need to check if the current user is an entrant of the waiting list
            WaitingListEntrant userEntrant = event.getWaitingList().stream()
                .filter(entrant -> entrant.getEntrantId().equals(currentUser.getDeviceId()))
                .findFirst()
                .orElse(null);

            if (userEntrant != null) {
                // User is an entrant on waiting list
                Log.i("EVENT", "userEntrant != null");
                view.findViewById(R.id.eventDetails_joinWaitlistBtn).setVisibility(View.GONE);
                configResponseBtns(view, userEntrant);
            }
            else {
                // User is not an entrant on waiting list and event has already ran lottery
                Log.i("EVENT", "userEndialoggeolotrant = null");
                TextView lotteryResultText = view.findViewById(R.id.eventDetails_selectedText);
                lotteryResultText.setVisibility(View.VISIBLE);
                lotteryResultText.setText("Sorry, the lottery has already been run for this event.");
                lotteryResultText.setBackgroundResource(R.drawable.cancelled_entrant_bg);
            }
        }

        configLotteryBtn(view);
    }

    /**
     * Configures the response buttons based on the user's entrant status.
     *
     * @param view The view to configure.
     * @param userEntrant The user as a userEntrant.
     */
    private void configResponseBtns(View view, WaitingListEntrant userEntrant) {
        Button acceptBtn = view.findViewById(R.id.eventDetails_acceptInviteBtn);
        Button declineBtn = view.findViewById(R.id.eventDetails_declineInviteBtn);
        TextView lotteryResultText = view.findViewById(R.id.eventDetails_selectedText);

        Log.i("EVENT", "in config response buttons");

        lotteryResultText.setVisibility(View.VISIBLE);

        switch (userEntrant.getStatus()) {
            case SELECTED:
                // User was selected
                lotteryResultText.setText("You have been selected to join the event!");
                lotteryResultText.setBackgroundResource(R.drawable.selected_entrant_bg);

                // Only if they're selected, we show the accept/decline buttons
                acceptBtn.setVisibility(View.VISIBLE);
                declineBtn.setVisibility(View.VISIBLE);

                acceptBtn.setOnClickListener(v -> {
                    userEntrant.setStatus(EntrantStatus.ACCEPTED);
                    eventRepository.updateEvent(event, null, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireContext(), "You have accepted the invitation", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Failed to accept invitation", Toast.LENGTH_SHORT).show();
                        }
                    });
                });

                declineBtn.setOnClickListener(v -> {
                    entrant.setStatus(EntrantStatus.CANCELLED);
                    eventRepository.updateEvent(event, null, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireContext(), "You have declined the invitation", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Failed to decline invitation", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
                break;

            case WAITING:
                lotteryResultText.setText("You are still waiting for a draw for entrants");
                lotteryResultText.setBackgroundResource(R.drawable.not_selected_entrant_bg);
                break;

            case CANCELLED:
                lotteryResultText.setText("You have already rejected the invitation");
                lotteryResultText.setBackgroundResource(R.drawable.cancelled_entrant_bg);
                break;

            case REJECTED:
                lotteryResultText.setText("Sorry! You were not selected to join the event\nYou will be notified if a spot opens up and you are selected");
                lotteryResultText.setBackgroundResource(R.drawable.not_selected_entrant_bg);
                view.findViewById(R.id.eventDetails_joinWaitlistBtn).setVisibility(View.GONE);

                break;

            case ACCEPTED:
                lotteryResultText.setText("You have already accepted the invitation");
                lotteryResultText.setBackgroundResource(R.drawable.selected_entrant_bg);
                break;
        }
    }

    /**
     * Configures the waitlist button based on the event and waiting list status.
     */
    private void configWaitlistBtn(View view) {
        if(event == null)
            return;

        Log.i("EVENT", "in config");

        View waitlistBtn = view.findViewById(R.id.eventDetails_joinWaitlistBtn);
        int waitingEntrantsCount = (int) event.getWaitingList().stream().filter(entrant -> entrant.getStatus() == EntrantStatus.WAITING).count();

        String buttonText = "";
        boolean enableButton = false;

        EntrantStatus status;
        alreadyIn = false;
        for(WaitingListEntrant entrant : event.getWaitingList()){
            if(entrant.getEntrantId().equals(User.getInstance().getDeviceId())){
                this.alreadyIn = true;
                this.entrant = entrant;
                status = entrant.getStatus();
                Log.i("EVENT", "Status: " + status.toString());
                break;
            }
        }

        if(event.getMaxEntrants() != null && waitingEntrantsCount >= event.getMaxEntrants()){
            buttonText = "Waitlist is full - try again later";
            enableButton = false;
        }
        else if(event.hasEventPassed()){
            buttonText = "This event has already passed";
            enableButton = false;
        }
        else if(alreadyIn && !event.getHasLotteryExecuted()){
            switch (this.entrant.getStatus()){
                case WAITING:
                    buttonText = "Leave Waitlist";
                    enableButton = true;
                    break;

                case ALL:
                    buttonText = " ";
                    break;
            }
        }
        else{
            buttonText = "Join Waitlist";
            enableButton = true;
        }

        ((TextView) waitlistBtn).setText(buttonText);
        waitlistBtn.setEnabled(enableButton);

        if (!enableButton) {
            waitlistBtn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.disabledButtonBG));
            ((TextView) waitlistBtn).setTextColor(ContextCompat.getColor(requireContext(), R.color.disabledButtonTxt));
        }

        // Geolocation part //
        waitlistBtn.setOnClickListener(v -> {
            if (event.isGeoLocationRequired()) {
                if(!currentUser.isGeoLocationEnabled())
                    dialogGeoLocation();
                else{
                    new GeoLocationUtils().fetchCurrentLocation(requireActivity(), (latitude, longitude) -> {
                        GeoPoint currentUserLocation = new GeoPoint(latitude, longitude);
                        Log.i("EVENT", "User location fetched: " + latitude + ", " + longitude);

                        // Perform waitlist logic with location
                        waitlistLogic(currentUserLocation);
                    });
                }
            } else {
                // Perform waitlist logic without requiring geolocation
                waitlistLogic(null);
            }
        });
    }

    private void dialogGeoLocation(){
        // Create a dialogue instance
        Dialog dialogue = new Dialog(requireContext());

        // Inflate the custom layout using View Binding
        DialogGeoLocationBinding binding = DialogGeoLocationBinding.inflate(LayoutInflater.from(requireContext()));
        dialogue.setContentView(binding.getRoot());

        binding.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeoLocationUtils geoLocationUtils = new GeoLocationUtils();

                // Check if location permissions are granted
                if (!geoLocationUtils.areLocationPermissionsGranted(requireActivity())) {
                    geoLocationUtils.requestLocationPermission(requireActivity());
                    return;
                }

                // Check if location services are enabled
                if (!geoLocationUtils.isLocationEnabled(requireActivity())) {
                    Toast.makeText(requireContext(), "Please enable location services", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Fetch the user's current location
                geoLocationUtils.fetchCurrentLocation(requireActivity(), (latitude, longitude) -> {
                    GeoPoint currentUserLocation = new GeoPoint(latitude, longitude);
                    Log.i("EVENT", "User location fetched: " + latitude + ", " + longitude);

                    currentUser.setGeoLocationEnabled(true);

                    UserRepository.updateUser(currentUser, task -> {
                        Log.i("EVENT", "updated user geolocation");
                    });
                    // Perform waitlist logic with location
                    waitlistLogic(currentUserLocation);
                });
                dialogue.dismiss();
            }
        });

        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogue.dismiss();
                Navigation.findNavController(requireView()).navigateUp();
            }
        });

        dialogue.show();
    }
    /**
     * Handles the waitlist logic based on the user's status and geolocation.
     *
     * @param location The user's current location, or null if not required.
     */
    // Geolocation
    private void waitlistLogic(GeoPoint location) {
        if (!alreadyIn) {
            WaitingListEntrant waitingListEntrant = new WaitingListEntrant(
                    currentUser.getDeviceId(),
                    location,
                    EntrantStatus.WAITING
            );
            event.getWaitingList().add(waitingListEntrant);
            currentUser.getEventIDs().add(event.getEventId());

            UserRepository.updateUser(currentUser, task -> {
                if(task.isSuccessful()){
                    Log.i("EVENT", "updated user with event ID");
                }
            });

            EventRepository.getInstance().updateEvent(event, null, task -> {
                if (task.isSuccessful()) {
                    Log.i("EVENT", "Added user to waitlist");
                    Toast.makeText(requireContext(), "You successfully joined the waitlist", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i("EVENT", "Failed to add user to waitlist");
                    Toast.makeText(requireContext(), "Failed to join waitlist. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        switch (entrant.getStatus()) {
            case WAITING:
                event.getWaitingList().remove(entrant);
                currentUser.getEventIDs().remove(event.getEventId());
                logText = "Removed user from waitlist";
                toastText = "You successfully left the waitlist";
                break;

            case SELECTED:
                entrant.setStatus(EntrantStatus.ACCEPTED);
                logText = "User accepted event";
                toastText = "You successfully accepted the invitation";
                break;

            case CANCELLED:
                entrant.setStatus(EntrantStatus.WAITING);
                logText = "User rejoined waitlist after rejection";
                toastText = "You successfully rejoined the waitlist";
                break;
        }

        UserRepository.updateUser(currentUser, task -> {
            if(task.isSuccessful()){
                Log.i("EVENT", "updated user with status");
            }
        });

        EventRepository.getInstance().updateEvent(event, null, task -> {
            if (task.isSuccessful()) {
                Log.i("EVENT", logText);
                Toast.makeText(requireContext(), toastText, Toast.LENGTH_SHORT).show();
            } else {
                Log.i("EVENT", "Failed to update waitlist: " + logText);
                Toast.makeText(requireContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Configures the lottery button based on the event status.
     */
    private void configLotteryBtn(View view) {
        Button lotteryBtn = view.findViewById(R.id.eventDetails_runLotteryBtn);

        if (!event.getHasLotteryExecuted()) {
            // If lottery hasn't been run, set up click listener to open LotteryRunDialog
            lotteryBtn.setOnClickListener(v -> {
                if (event.getWaitingList().isEmpty()) {
                    Toast.makeText(getContext(), "Waitinglist is empty - lottery cannot run", Toast.LENGTH_LONG).show();
                } else {
                    LotteryRunDialog.showDialog(getParentFragmentManager(), event);
                }
            });
        } else {
            // If lottery has been run, set up click listener to navigate to lottery status fragment
            lotteryBtn.setText("See Lottery Overview");
            lotteryBtn.setOnClickListener(v -> navigateToLotteryOverview());
        }
    }

    /**
     * Sets the text of a TextView.
     *
     * @param view The view containing the TextView.
     * @param viewId The ID of the TextView.
     * @param text The text to set.
     */
    private void setText(View view, int viewId, String text) {
        ((TextView) view.findViewById(viewId)).setText(text);
    }

    /**
     * Sets the visibility of a view.
     *
     * @param view The view containing the target view.
     * @param viewId The ID of the target view.
     * @param isVisible Whether the view should be visible.
     */
    private void setVisibility(View view, int viewId, boolean isVisible) {
        view.findViewById(viewId).setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * Loads an image into an ImageView using Glide.
     *
     * @param view The view containing the ImageView.
     * @param imageViewId The ID of the ImageView.
     * @param imageUrl The URL of the image to load.
     */
    private void loadImage(View view, int imageViewId, String imageUrl) {
        Glide.with(view)
                .load(imageUrl)
                .into((ImageView) view.findViewById(imageViewId));
    }

    /**
     * Deletes the event.
     */
    private void deleteEvent() {
        if (event != null) {
            eventRepository.deleteEvent(event.getEventId(), deleteTask -> {
                if (deleteTask.isSuccessful()) {
                    if (isAdded() && getView() != null) {
                        Navigation.findNavController(requireView()).navigate(R.id.action_eventDetailsFragment_to_myEventsFragment);
                        Toast.makeText(getContext(), "Event deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (isAdded() && getView() != null) {
                        Toast.makeText(getContext(), "Failed to delete event: " + deleteTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /**
     * Navigates to the lottery overview view.
     * Enabled once the lottery has been ran.
     */
    private void navigateToLotteryOverview() {
        if (event != null) {
            Log.i("LOTTERY", "navigating to overview");
            Bundle bundle = new Bundle();
            bundle.putSerializable("event", event);
            Navigation.findNavController(requireView()).navigate(R.id.action_eventDetailsFragment_to_lotteryOverviewFragment, bundle);
        }
    }

    /**
     * Navigates to the QR code view.
     */
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

    /**
     * Navigates to the create notification view.
     */
    private void navigateToCreateNotif() {
        if (event != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("Event", event);
            Navigation.findNavController(requireView()).navigate(R.id.action_eventDetailsFragment_to_createNotif, bundle);
        }
    }

    /**
     * Navigates to the edit event view.
     */
    private void navigateToEditEvent() {
        if (event != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("selectedEvent", event);
            Navigation.findNavController(requireView()).navigate(R.id.action_eventDetailsFragment_to_editEventFragment, bundle);
        }
    }

    /**
     * Navigates to the waitlist info view.
     */
    private void navigateToWaitlistInfo() {
        if (event != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("event", event);
            Navigation.findNavController(requireView()).navigate(R.id.action_eventDetailsFragment_to_eventWaitingListFragment, bundle);
        }
    }
}