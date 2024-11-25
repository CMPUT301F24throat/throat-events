package com.example.pickme.views;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

import com.example.pickme.R;
import com.example.pickme.models.Event;
import com.example.pickme.models.WaitingListEntrant;
import com.example.pickme.repositories.EventRepository;
import com.example.pickme.utils.LotteryUtils;
import com.example.pickme.utils.WaitingListUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class LotteryRunDialog extends DialogFragment {

    private String eventId;
    private LotteryUtils lotteryUtils;
    private WaitingListUtils waitingListUtils;
    private EventRepository eventRepository;

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
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_lottery_run, null);
        dialog.setContentView(view);

        TextView title = view.findViewById(R.id.dialog_title);
        TextView description = view.findViewById(R.id.dialog_description);
        Button startButton = view.findViewById(R.id.start_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        title.setText("Event Lottery");

        waitingListUtils.getWaitingEntrants(eventId, new OnCompleteListener<List<WaitingListEntrant>>() {
            @Override
            public void onComplete(@NonNull Task<List<WaitingListEntrant>> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    int y = task.getResult().size();
                    eventRepository.getEventById(eventId, new OnCompleteListener<Event>() {
                        @Override
                        public void onComplete(@NonNull Task<Event> eventTask) {
                            if (eventTask.isSuccessful() && eventTask.getResult() != null) {
                                Event event = eventTask.getResult();
                                lotteryUtils.getNumToDraw(event, eventId, new OnCompleteListener<Integer>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Integer> task) {
                                        if (task.isSuccessful() && task.getResult() != null) {
                                            int x = task.getResult();
                                            description.setText("We will draw " + x + " winners from your waitlist of " + y + " entrants.\nProceed?");
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

        startButton.setOnClickListener(v -> {
            lotteryUtils.runLottery(eventId, new OnCompleteListener<List<String>>() {
                @Override
                public void onComplete(@NonNull Task<List<String>> task) {
                    if (task.isSuccessful()) {
                        dialog.dismiss();
                        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.action_eventDetailsFragment_to_lotteryWinnersFragment);
                    }
                }
            });
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }

    public static void showDialog(FragmentManager fragmentManager, String eventId) {
        LotteryRunDialog dialog = new LotteryRunDialog(eventId);
        dialog.show(fragmentManager, "LotteryRunDialog");
    }
}