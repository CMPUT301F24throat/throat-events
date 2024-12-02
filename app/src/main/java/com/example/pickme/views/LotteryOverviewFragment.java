package com.example.pickme.views;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickme.R;
import com.example.pickme.models.Enums.EntrantStatus;
import com.example.pickme.models.Event;
import com.example.pickme.models.WaitingListEntrant;
import com.example.pickme.views.adapters.EntrantAdapter;

import java.util.List;
import java.util.stream.Collectors;

public class LotteryOverviewFragment extends Fragment {

    private RecyclerView entrantList;
    private EntrantAdapter entrantAdapter;
    private Event event;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() == null) {
            return;
        }

        event = (Event) getArguments().getSerializable("event");

        entrantList = view.findViewById(R.id.lotteryOverview_entrantList);
        entrantList.setLayoutManager(new LinearLayoutManager(getContext()));
        entrantAdapter = new EntrantAdapter(filterEntrants(EntrantStatus.SELECTED));
        entrantList.setAdapter(entrantAdapter);

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
    }

    private List<WaitingListEntrant> filterEntrants(EntrantStatus status) {
        return event.getWaitingList().stream()
                .filter(e -> e.getStatus() == status)
                .collect(Collectors.toList());
    }
}