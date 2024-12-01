package com.example.pickme.views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickme.R;
import com.example.pickme.models.Event;
import com.example.pickme.models.WaitingListEntrant;
import com.example.pickme.repositories.EventRepository;
import com.example.pickme.views.adapters.WaitingListAdapter;

import java.util.ArrayList;

/**
 * Fragment representing the waiting list for an event.
 */
public class EventWaitingListFragment extends Fragment {
    private Event event;
    private RecyclerView waitingListRecyclerView;
    private WaitingListAdapter waitingListAdapter;
    private ArrayList<WaitingListEntrant> entrants = new ArrayList<>();

    /**
     * Inflates the layout for this fragment.
     *
     * @param inflater LayoutInflater to use for inflating the layout.
     * @param container ViewGroup to which the new view will be added.
     * @param savedInstanceState Bundle containing the saved state.
     * @return The View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.event_waitinglist, container, false);
    }

    /**
     * Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned.
     *
     * @param view The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle).
     * @param savedInstanceState Bundle containing the saved state.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("event");
            if (event == null) {
                Navigation.findNavController(requireView()).navigateUp();
                return;
            }

            waitingListRecyclerView = view.findViewById(R.id.waitingList_list);
            waitingListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            loadWaitingListEntrants();
        } else {
            Navigation.findNavController(requireView()).navigateUp();
            return;
        }

        // Set up on click listener for back button
        view.findViewById(R.id.waitingList_backBtn).setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
    }

    /**
     * Loads the waiting list entrants for the event and sets up the RecyclerView adapter.
     */
    private void loadWaitingListEntrants() {
        entrants = event.getWaitingList();

        for(WaitingListEntrant e : entrants){
            Log.i("WAITLIST", "ID: " + e.getEntrantId());
        }

        waitingListAdapter = new WaitingListAdapter(entrants, getContext());
        waitingListRecyclerView.setAdapter(waitingListAdapter);

        EventRepository.getInstance().attachEvent(event, () -> {
            waitingListAdapter.notifyDataSetChanged();
        });

//        waitingListUtils.getWaitingListEntrantsByStatus(event.getEventId(), null, new OnCompleteListener<List<WaitingListEntrant>>() {
//            @Override
//            public void onComplete(@NonNull Task<List<WaitingListEntrant>> task) {
//                if (task.isSuccessful() && task.getResult() != null) {
//                    List<WaitingListEntrant> waitingListEntrants = task.getResult();
//                    waitingListAdapter = new WaitingListAdapter(waitingListEntrants);
//                    waitingListRecyclerView.setAdapter(waitingListAdapter);
//                } else {
//                    Log.e("EventWaitingListFragment", "Error getting waiting list entrants: " + task.getException());
//                    Navigation.findNavController(requireView()).navigateUp();
//                }
//            }
//        });
    }
}