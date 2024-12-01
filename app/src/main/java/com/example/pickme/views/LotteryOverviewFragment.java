package com.example.pickme.views;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickme.R;
import com.example.pickme.models.Enums.EntrantStatus;
import com.example.pickme.models.Event;
import com.example.pickme.models.User;
import com.example.pickme.utils.LotteryUtils;
import com.example.pickme.utils.WaitingListUtils;

public class LotteryOverviewFragment extends Fragment {

    private RecyclerView entrantList;
    private WaitingListUtils waitingListUtils;
    private LotteryUtils lotteryUtils;
    private Event event;
    private User currentUser;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        entrantList = view.findViewById(R.id.lotteryOverview_entrantList);
        waitingListUtils = new WaitingListUtils();
        lotteryUtils = new LotteryUtils();
        currentUser = User.getInstance();

        if (getArguments() != null) {

        }


        updateLotteryStatsText();  // Update the lottery stats text
    }

    private void filterEntrantList(EntrantStatus status) {

    }

    private void updateLotteryStatsText() {

    }
}