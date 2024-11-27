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

    private String eventId;
    private LotteryUtils lotteryUtils;
    private WaitingListUtils waitingListUtils;
    private EventRepository eventRepository;

    /**
     * Constructor for LotteryRunDialog.
     *
     * @param eventId The ID of the event for which the lottery is being run.
     */
    public LotteryRunDialog(String eventId) {
        this.eventId = eventId;
        this.lotteryUtils = new LotteryUtils();
        this.waitingListUtils = new WaitingListUtils();
        this.eventRepository = new EventRepository();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext());
        View view = getLayoutInflater().inflate(R.layout.lottery_run_dialog, null);
        dialog.setContentView(view);

        TextView title = view.findViewById(R.id.dialog_title);
        TextView description = view.findViewById(R.id.dialog_description);
        Button startButton = view.findViewById(R.id.start_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        // Fetch the waiting list entrants and set the description text
        waitingListUtils.getWaitingEntrants(eventId, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                int y = task.getResult().size();
                eventRepository.getEventById(eventId, eventTask -> {
                    if (eventTask.isSuccessful() && eventTask.getResult() != null) {
                        Event event = eventTask.getResult();
                        lotteryUtils.getNumToDraw(event, eventId, task1 -> {
                            if (task1.isSuccessful() && task1.getResult() != null) {
                                int x = task1.getResult();
                                description.setText("We will draw " + x + " winners from your waitlist of " + y + " entrants.\nProceed?");
                                title.setText("Event Lottery");
                                dialog.show();  // Only show dialog once all fields are set!!!!
                            }
                        });
                    }
                });
            }
        });

        // Set the start button click listener to run the lottery
        startButton.setOnClickListener(v -> {
            lotteryUtils.runLottery(eventId, task -> {
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
     * @param eventId The ID of the event for which the lottery is being run.
     */
    public static void showDialog(FragmentManager fragmentManager, String eventId) {
        LotteryRunDialog dialog = new LotteryRunDialog(eventId);
        dialog.show(fragmentManager, "LotteryRunDialog");
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