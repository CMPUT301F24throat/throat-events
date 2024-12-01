package com.example.pickme.views;

import android.os.Bundle;
import android.util.Log;
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
import com.example.pickme.models.User;
import com.example.pickme.models.WaitingListEntrant;
import com.example.pickme.repositories.EventRepository;
import com.example.pickme.repositories.UserRepository;
import com.example.pickme.utils.LotteryUtils;
import com.example.pickme.utils.WaitingListUtils;
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
    private WaitingListUtils waitingListUtils;
    private LotteryUtils lotteryUtils;

    private boolean alreadyIn = false;
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
            waitingListUtils = new WaitingListUtils();
            lotteryUtils = new LotteryUtils();

            configureView(view, currentUser);
            displayEventDetails(view);
            eventRepository.attachEvent(event, () -> {
                if (event.getEventId() == null){
                    Toast.makeText(getContext(), "Event deleted", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigate(R.id.action_eventDetailsFragment_to_myEventsFragment);
                }
                else{
                    if(view != null){
                        configureView(view, currentUser);
                        displayEventDetails(view);
                    }
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
            int waitingEntrantsCount = (int) event.getWaitingList().stream().filter(entrant -> entrant.getStatus() == EntrantStatus.WAITING).count();
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
        setVisibility(view, R.id.eventDetails_joinWaitlistBtn, !isOrganizer);
        setVisibility(view, R.id.eventDetails_moreInfoLink, isOrganizer);

        configWaitlistBtn(view);
        configLotteryBtn(view);
    }

    /**
     * Configures the waitlist button based on the event and waiting list status.
     */
    private void configWaitlistBtn(View view) {
        if(waitingListUtils == null || event == null)
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
        }
        else if(event.hasEventPassed()){
            buttonText = "This event has already passed";
        }
        else if(alreadyIn){
            switch (this.entrant.getStatus()){
                case WAITING:
                    buttonText = "Leave Waitlist";
                    enableButton = true;
                    break;

                case SELECTED:
                    buttonText = "Accept";
                    enableButton = true;
                    //TODO: need more buttons and stuff for accept/decline
                    break;

                case REJECTED:      // User rejected an invitation; cannot rejoin waitlist will remain in waitlist with EntrantStatus REJECTED
                    buttonText = "You have rejected this event";
                    break;

                case ACCEPTED:      // User accepted an invitation; cannot rejoin waitlist will remain in waitlist with EntrantStatus ACCEPTED
                    buttonText = "Already accepted event invite";
                    break;

                case CANCELLED:     // User cancelled their spot; can rejoin waitlist
                    buttonText = "Rejoin Waitlist";
                    enableButton = true;
                    break;

                case ALL:
                    buttonText = "Error";
                    break;
            }
        }
        else{
            buttonText = "Join Waitlist";
            enableButton = true;
        }

        ((TextView) waitlistBtn).setText(buttonText);
        waitlistBtn.setEnabled(enableButton);
        if(!enableButton){
            waitlistBtn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.disabledButtonBG));
            ((TextView) waitlistBtn).setTextColor(ContextCompat.getColor(requireContext(), R.color.disabledButtonTxt));
        }

        waitlistBtn.setOnClickListener(v -> {
            GeoPoint currentUserLocation = null;
            if (event.isGeoLocationRequired()) {
                // TODO: Implement geolocation
            }

            waitlistLogic();
        });

    }

    private void waitlistLogic(){
        if(!alreadyIn){
            WaitingListEntrant waitingListEntrant = new WaitingListEntrant(currentUser.getDeviceId(), null, EntrantStatus.WAITING);
            event.getWaitingList().add(waitingListEntrant);
            currentUser.getEventIDs().add(event.getEventId());
            eventRepository.updateEvent(event, null, task -> {
                Log.i("EVENT", "Added user to waitlist");
                Toast.makeText(requireContext(), "You successfully joined the waitlist", Toast.LENGTH_SHORT).show();
            });
            return;
        }

        String logText = "";
        String toastText = "";
        switch (entrant.getStatus()){
            case WAITING:
                event.getWaitingList().remove(entrant);
                currentUser.getEventIDs().remove(event.getEventId());
                logText = "Removed user from waitlist";
                toastText = "You successfully left the waitlist";
                break;

            case SELECTED:
                // TODO: perhaps move this so it is under an accept/decline button
                entrant.setStatus(EntrantStatus.ACCEPTED);  // User stays in waiting list except status is changed to ACCEPTED so not counted as waiting entrant
                logText = "User accepted event";
                toastText = "You successfully accepted the invitation";
                break;

            case CANCELLED:
                entrant.setStatus(EntrantStatus.WAITING);
                logText = "User rejoined waitlist after cancelling spot";
                toastText = "You successfully rejoined the waitlist";
                break;
        }

        String finalLogText = logText;
        String finalToastText = toastText;
        eventRepository.updateEvent(event, null, task -> {
            if(task.isSuccessful()){
                Log.i("EVENT", finalLogText);
                Toast.makeText(requireContext(), finalToastText, Toast.LENGTH_SHORT).show();
            }
            else{
                Log.i("EVENT", "Waitlist button failed, attempt: " + finalLogText);
                Toast.makeText(requireContext(), "Sorry, something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Configures the lottery button based on the event status.
     */
    private void configLotteryBtn(View view) {
        View lotteryBtn = view.findViewById(R.id.eventDetails_runLotteryBtn);

        if (!event.hasLotteryExecuted()) {
            // If lottery hasn't been run, set up click listener to open LotteryRunDialog
            lotteryBtn.setOnClickListener(v -> LotteryRunDialog.showDialog(getParentFragmentManager(), event));
        } else {
            // If lottery has been run, set up click listener to navigate to lottery status fragment
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

    private void runLottery() {
        // Need to check if waitlist is empty and that event is still valid
        // If it is, open LotteryRunDialog

    }

    /**
     * Navigates to the lottery overview view.
     * Enabled once the lottery has been ran.
     */
    private void navigateToLotteryOverview() {
        if (event != null) {
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