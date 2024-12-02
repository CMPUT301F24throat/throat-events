package com.example.pickme.views;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickme.R;
import com.example.pickme.models.Enums.EntrantStatus;
import com.example.pickme.models.Event;
import com.example.pickme.models.WaitingListEntrant;
import com.example.pickme.repositories.EventRepository;
import com.example.pickme.utils.LotteryUtils;
import com.example.pickme.views.adapters.EntrantAdapter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Fragment that displays an overview of the lottery.
 */
public class LotteryOverviewFragment extends Fragment {

    private RecyclerView entrantList;
    private EntrantAdapter entrantAdapter;
    private Event event;
    private TextView lotteryStatsText;
    private LotteryUtils lotteryUtils;

    /**
     * Called when the fragment's view has been created.
     *
     * @param view The view of the fragment.
     * @param savedInstanceState The saved instance state.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() == null) {
            return;
        }

        event = (Event) getArguments().getSerializable("event");
        lotteryUtils = new LotteryUtils();

        entrantList = view.findViewById(R.id.lotteryOverview_entrantList);
        entrantList.setLayoutManager(new LinearLayoutManager(getContext()));
        entrantAdapter = new EntrantAdapter(filterEntrants(EntrantStatus.SELECTED));
        entrantList.setAdapter(entrantAdapter);

        lotteryStatsText = view.findViewById(R.id.lotteryOverview_lotteryStatsText);
        updateLotteryStatsText();

        Spinner statusSpinner = view.findViewById(R.id.lotteryOverview_dropdown);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.entrant_status_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);

        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                EntrantStatus selectedStatus = EntrantStatus.values()[position];
                entrantAdapter.updateEntrants(filterEntrants(selectedStatus));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        view.findViewById(R.id.lotteryOverview_backBtn).setOnClickListener(v -> {
            Navigation.findNavController(view).popBackStack();
        });

        view.findViewById(R.id.lotteryOverview_drawReplacementBtn).setOnClickListener(v -> {
            lotteryUtils.replaceCancelledEntrants(event, task -> {
                if (task.isSuccessful()) {

                    // Re-fetch the updated event from Firestore
                    EventRepository.getInstance().getEventById(event.getEventId(), eventTask -> {
                        if (eventTask.isSuccessful()) {
                            event = eventTask.getResult();
                            updateLotteryStatsText();
                            entrantAdapter.updateEntrants(filterEntrants(EntrantStatus.SELECTED));
                        } else {
                            Toast.makeText(getContext(), "Failed to fetch updated event", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Failed to draw replacements", Toast.LENGTH_SHORT).show();
                }
            });
        });

        view.findViewById(R.id.lotteryOverview_cancelPendingBtn).setOnClickListener(v -> {
            lotteryUtils.cancelPendingEntrants(event, task -> {
                if (task.isSuccessful()) {

                    // Re-fetch the updated event from Firestore
                    EventRepository.getInstance().getEventById(event.getEventId(), eventTask -> {
                        if (eventTask.isSuccessful()) {
                            event = eventTask.getResult();
                            updateLotteryStatsText();
                            entrantAdapter.updateEntrants(filterEntrants(EntrantStatus.SELECTED));
                        } else {
                            Toast.makeText(getContext(), "Failed to fetch updated event", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Failed to cancel pending entrants", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    /**
     * Filters the entrants based on their status.
     *
     * @param status The status to filter by.
     * @return A list of entrants with the specified status.
     */
    private List<WaitingListEntrant> filterEntrants(EntrantStatus status) {
        return event.getWaitingList().stream()
                .filter(e -> e.getStatus() == status)
                .collect(Collectors.toList());
    }

    /**
     * Updates the lottery statistics text view with the current statistics.
     */
    private void updateLotteryStatsText() {
        long p = event.getWaitingList().stream()
                .filter(e -> e.getStatus() == EntrantStatus.SELECTED || e.getStatus() == EntrantStatus.ACCEPTED || e.getStatus() == EntrantStatus.CANCELLED)
                .count();
        long q = event.getWaitingList().stream()
                .filter(e -> e.getStatus() == EntrantStatus.ACCEPTED)
                .count();
        long r = event.getWaitingList().stream()
                .filter(e -> e.getStatus() == EntrantStatus.SELECTED)
                .count();
        long s = event.getWaitingList().stream()
                .filter(e -> e.getStatus() == EntrantStatus.CANCELLED)
                .count();

        String statsText = p + " selected\n" + q + " accepted | " + r + " pending" + s + " declined";
        lotteryStatsText.setText(statsText);
    }
}

/*
   Coding Sources
   <p>
   Stack Overflow
   - https://stackoverflow.com/questions/52130338/update-ui-after-getting-data-from-firebase-database
   - https://stackoverflow.com/questions/16694786/how-to-customize-a-spinner-in-android?noredirect=1&lq=1
   <p>
   Medium
   - https://medium.com/vattenfall-tech/android-spinner-customizations-8b4980fb0ee3
  */