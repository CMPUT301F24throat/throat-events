package com.example.pickme.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickme.R;
import com.example.pickme.models.User;
import com.example.pickme.repositories.UserRepository;
import com.example.pickme.views.adapters.WinnerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A Fragment that displays the list of lottery winners.
 */
public class LotteryWinnersFragment extends Fragment {

    private RecyclerView winnerList;
    private WinnerAdapter winnerAdapter;
    private List<User> winners = new ArrayList<>();
    private UserRepository userRepository;

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here
     * @return Return the View for the fragment's UI, or null
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.lottery_winners, container, false);
    }

    /**
     * Called immediately after onCreateView has returned, but before any saved state has been restored in to the view.
     *
     * @param view The View returned by onCreateView
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        winnerList = view.findViewById(R.id.winnerList);
        Button confirmButton = view.findViewById(R.id.confirmWinnersButton);

        winnerAdapter = new WinnerAdapter(winners);
        winnerList.setLayoutManager(new LinearLayoutManager(requireContext()));
        winnerList.setAdapter(winnerAdapter);

        userRepository = UserRepository.getInstance();

        if (getArguments() != null) {
            List<String> selectedUserDeviceIds = getArguments().getStringArrayList("selectedUserDeviceIds");
            if (selectedUserDeviceIds != null) {
                fetchWinners(selectedUserDeviceIds);
            }
        }

        confirmButton.setOnClickListener(v -> {
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.action_lotteryWinnersFragment_to_eventDetailsFragment);
        });
    }

    /**
     * Fetches the User objects for each selected user device ID and updates the winners list.
     *
     * @param selectedUserDeviceIds The list of selected user device IDs
     */
    private void fetchWinners(List<String> selectedUserDeviceIds) {
        // Get User objects for each selected user device ID
        for (String userDeviceId : selectedUserDeviceIds) {
            userRepository.getUserByDeviceId(userDeviceId, userTask -> {
                if (userTask.isSuccessful() && userTask.getResult() != null) {
                    winners.add(userTask.getResult());
                    winnerAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}