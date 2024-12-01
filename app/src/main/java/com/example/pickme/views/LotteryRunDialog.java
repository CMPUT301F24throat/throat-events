package com.example.pickme.views;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

import com.example.pickme.R;
import com.example.pickme.models.Enums.EntrantStatus;
import com.example.pickme.models.Event;
import com.example.pickme.repositories.EventRepository;
import com.example.pickme.utils.LotteryUtils;
import com.example.pickme.utils.WaitingListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A DialogFragment that handles the lottery run for an event.
 */
public class LotteryRunDialog extends DialogFragment {

    private Event event;
    private LotteryUtils lotteryUtils;
    private WaitingListUtils waitingListUtils;
    private EventRepository eventRepository;
    private TextView description;

    /**
     * Constructor for LotteryRunDialog.
     *
     * @param event The event for which the lottery is being run.
     */
    public LotteryRunDialog(Event event) {
        this.event = event;
        this.lotteryUtils = new LotteryUtils();
        this.waitingListUtils = new WaitingListUtils();
        this.eventRepository = EventRepository.getInstance();
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext());
        View view = getLayoutInflater().inflate(R.layout.lottery_run_dialog, null);
        dialog.setContentView(view);

        description = view.findViewById(R.id.lotteryRun_description);
        Button startButton = view.findViewById(R.id.lotteryRun_startBtn);
        Button cancelButton = view.findViewById(R.id.lotteryRun_cancelBtn);
        setDialogDescription();

        // Set the start button click listener to run the lottery
        startButton.setOnClickListener(v -> {
            lotteryUtils.runLottery(event, task -> {
                if (task.isSuccessful()) {
                    List<String> selectedUserDeviceIds = task.getResult();

                    // If lottery was successful, navigate to the winners fragment
                    dialog.dismiss();
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("selectedUserDeviceIds", new ArrayList<>(selectedUserDeviceIds));
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.action_eventDetailsFragment_to_lotteryWinnersFragment, bundle);
                } else {
                    // Error running the lottery
                    Toast.makeText(requireContext(), "Error running the event lottery - try again", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        });

        // Set the cancel button click listener to dismiss the dialog
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }

    /**
     * Static method to show the LotteryRunDialog.
     *
     * @param fragmentManager The FragmentManager to use for showing the dialog.
     * @param event The event for which the lottery is being run.
     */
    public static void showDialog(FragmentManager fragmentManager, Event event) {
        LotteryRunDialog dialog = new LotteryRunDialog(event);
        dialog.show(fragmentManager, "LotteryRunDialog");
    }

    /**
     * Sets the description text for the dialog.
     */
    private void setDialogDescription() {
        int numToDraw = lotteryUtils.determineNumToDraw(event);
        long numWaitingEntrants = event.getWaitingList().stream()
                .filter(entrant -> entrant.getStatus() == EntrantStatus.WAITING)
                .count();
        String descriptionText = "We will draw " + numToDraw + " winners from your waitlist of " + numWaitingEntrants + " entrants.\nProceed?";
        description.setText(descriptionText);

//        eventRepository.getEventById(event.getEventId(), task -> {
//            if (task.isSuccessful() && task.getResult() != null) {
//                Event event = task.getResult();
//                int numToDraw = lotteryUtils.determineNumToDraw(event);
//                long numWaitingEntrants = event.getWaitingList().stream()
//                        .filter(entrant -> entrant.getStatus() == EntrantStatus.WAITING)
//                        .count();
//                String descriptionText = "We will draw " + numToDraw + " winners from your waitlist of " + numWaitingEntrants + " entrants.\nProceed?";
//                description.setText(descriptionText);
//            } else {
//                description.setText("Error fetching event details.");
//            }
//        });
    }
}
/*
  Code Sources
  <p>
  Stack Overflow:
  - How do I pass arguments to a Dialog box in Android?
  <p>
  Android Developer Documentation:
  - Dialogs
  - Display dialogs with DialogFragment
 */