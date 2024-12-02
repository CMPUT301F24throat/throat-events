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
import com.example.pickme.utils.WaitingListUtils;
import com.example.pickme.views.adapters.WaitingListAdapter;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Fragment representing the waiting list for an event.
 */
public class EventWaitingListFragment extends Fragment implements OnMapReadyCallback {
    private Event event;
    private WaitingListUtils waitingListUtils;
    private RecyclerView waitingListRecyclerView;
    private WaitingListAdapter waitingListAdapter;
    private ArrayList<WaitingListEntrant> entrants = new ArrayList<>();
    private MapView mapView;
    private GoogleMap googleMap;

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

            waitingListUtils = new WaitingListUtils();

            waitingListRecyclerView = view.findViewById(R.id.waitingList_list);
            waitingListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            // Initialize MapView
            mapView = view.findViewById(R.id.waitingList_entrantMap);
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this);

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

        if (googleMap != null) {
            for (WaitingListEntrant entrant : entrants) {
                if (entrant.getGeoLocation() != null) {
                    LatLng position = new LatLng(
                            entrant.getGeoLocation().getLatitude(),
                            entrant.getGeoLocation().getLongitude()
                    );
                    googleMap.addMarker(new MarkerOptions().position(position).title(entrant.getEntrantId()));
                }
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }
}