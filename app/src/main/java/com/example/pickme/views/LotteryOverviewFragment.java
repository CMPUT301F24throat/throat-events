package com.example.pickme.views;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickme.R;
import com.example.pickme.models.Enums.EntrantStatus;
import com.example.pickme.models.WaitingListEntrant;
import com.example.pickme.utils.WaitingListUtils;

import java.util.List;

public class LotteryOverviewFragment extends Fragment {

    private RecyclerView recyclerView;
    private WaitingListUtils waitingListUtils;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView2);
        waitingListUtils = new WaitingListUtils();

        view.findViewById(R.id.filterLotteryListBtn).setOnClickListener(v -> showFilterDialog());

        updateLotteryStatsText();  // Update the lottery stats text
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.lottery_filter_dialog, null);
        builder.setView(dialogView)
                .setTitle("Filter List")
                .setPositiveButton("Apply", (dialog, id) -> {
                    RadioGroup radioGroup = dialogView.findViewById(R.id.filterRadioGroup);
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    EntrantStatus status = null;

                    if (selectedId == R.id.radioAccepted) {
                        status = EntrantStatus.ACCEPTED;
                    } else if (selectedId == R.id.radioSelected) {
                        status = EntrantStatus.SELECTED;
                    } else if (selectedId == R.id.radioCancelled) {
                        status = EntrantStatus.CANCELLED;
                    } else if (selectedId == R.id.radioDeclined) {
                        status = EntrantStatus.REJECTED;
                    }

                    if (status != null) {
                        filterRecyclerView(status);
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void filterRecyclerView(EntrantStatus status) {
        String eventId = "your_event_id"; // Replace with actual event ID
        waitingListUtils.getEntrantsByStatus(eventId, status, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<WaitingListEntrant> filteredEntrants = task.getResult();
                // Update your RecyclerView adapter with the filtered list
                // recyclerView.setAdapter(new YourAdapter(filteredEntrants));
            } else {
                // Handle the error
            }
        });
    }

    private void updateLotteryStatsText() {
        String eventId = "your_event_id"; // Replace with actual event ID
//        waitingListUtils.getAllEntrantsFromWaitingList(eventId, task -> {
//            if (task.isSuccessful() && task.getResult() != null) {
//                List<WaitingListEntrant> entrants = task.getResult();
//                int acceptedCount = 0;
//                int selectedCount = 0;
//                int rejectedCount = 0;
//
//                for (WaitingListEntrant entrant : entrants) {
//                    if (entrant.getEntrantStatus() == EntrantStatus.ACCEPTED) {
//                        acceptedCount++;
//                    } else if (entrant.getEntrantStatus() == EntrantStatus.SELECTED) {
//                        selectedCount++;
//                    } else if (entrant.getEntrantStatus() == EntrantStatus.REJECTED) {
//                        rejectedCount++;
//                    }
//                }
//
//                int q = acceptedCount + selectedCount;
//                int r = acceptedCount;
//                int s = rejectedCount;
//                int t = selectedCount;
//
//                String statsText = q + " selected\n" + r + " accepted | " + s + " declined | " + t + " pending";
//                TextView lotteryStatsText = getView().findViewById(R.id.lotteryStatsText);
//                lotteryStatsText.setText(statsText);
//            } else {
//                // Handle the error
//            }
//        });

    }
}