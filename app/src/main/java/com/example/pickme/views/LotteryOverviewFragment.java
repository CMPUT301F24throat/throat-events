package com.example.pickme.views;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickme.R;
import com.example.pickme.models.Enums.EntrantStatus;
import com.example.pickme.models.Event;
import com.example.pickme.models.User;
import com.example.pickme.models.WaitingListEntrant;
import com.example.pickme.repositories.EventRepository;
import com.example.pickme.utils.LotteryUtils;
import com.example.pickme.utils.WaitingListUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LotteryOverviewFragment extends Fragment {

    private RecyclerView entrantList;
    private WaitingListUtils waitingListUtils;
    private LotteryUtils lotteryUtils;
    private EventRepository eventRepository;
    private Event event;
    private User currentUser;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() == null) {
            return;
        }

        entrantList = view.findViewById(R.id.lotteryOverview_entrantList);
        waitingListUtils = new WaitingListUtils();
        lotteryUtils = new LotteryUtils();
        eventRepository = EventRepository.getInstance();
        currentUser = User.getInstance();
        event = (Event) getArguments().getSerializable("event");

        updateLotteryStatsText();  // Update the lottery stats text
    }

    private void filterEntrantList(EntrantStatus status) {

    }

    private void updateLotteryStatsText() {

    }

    private void cancelPendingEntrants() {
        List<WaitingListEntrant> pendingEntrants = event.getWaitingList().stream()
                .filter(e -> e.getStatus() == EntrantStatus.SELECTED)
                .collect(Collectors.toList());

        for (WaitingListEntrant entrant : pendingEntrants) {
            entrant.setStatus(EntrantStatus.CANCELLED);
        }

        // Need to update event waiting list so that the changes are reflected in the database
        ArrayList<WaitingListEntrant> updatedWaitingList = new ArrayList<>(event.getWaitingList());
        for (WaitingListEntrant entrant : pendingEntrants) {
            int index = updatedWaitingList.indexOf(entrant);
            if (index != -1) {
                updatedWaitingList.set(index, entrant);
            }
        }
        event.setWaitingList(updatedWaitingList);

        eventRepository.updateEvent(event, null, task -> {
            if (task.isSuccessful()) {
                updateLotteryStatsText();
            } else {
                Toast.makeText(getContext(), "Failed to cancel pending entrants", Toast.LENGTH_SHORT).show();
            }
        });
    }
}