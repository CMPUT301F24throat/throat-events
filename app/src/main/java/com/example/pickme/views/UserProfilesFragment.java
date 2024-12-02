package com.example.pickme.views;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.pickme.databinding.LayoutDeleteUserAdminDialogBinding;
import com.example.pickme.databinding.LayoutUserFacilityDetailsDialogBinding;
import com.example.pickme.databinding.UserAdminProfilesBinding;
import com.example.pickme.models.Facility;
import com.example.pickme.models.User;
import com.example.pickme.repositories.FacilityRepository;
import com.example.pickme.repositories.UserRepository;
import com.example.pickme.views.adapters.UserProfilesAdapter;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment to display and manage user profiles.
 */
public class UserProfilesFragment extends Fragment implements UserProfilesAdapter.OnItemClickListener {

    private UserAdminProfilesBinding binding;
    private UserRepository userRepository;
    private FacilityRepository facilityRepository;
    private List<User> usersList = new ArrayList<>();
    private UserProfilesAdapter userProfilesAdapter;

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = UserAdminProfilesBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    /**
     * Called immediately after onCreateView has returned, but before any saved state has been restored in to the view.
     *
     * @param view               The View returned by onCreateView.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userRepository = UserRepository.getInstance();
        facilityRepository = FacilityRepository.getInstance();

        binding.back.setOnClickListener(listener -> Navigation.findNavController(requireView()).navigateUp());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        userProfilesAdapter = new UserProfilesAdapter(requireActivity(), usersList, this);
        binding.recyclerView.setAdapter(userProfilesAdapter);
        binding.searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userProfilesAdapter.filter(s.toString()); // Filter the list based on query
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        loadEvents();
    }

    /**
     * Loads user events from the repository and updates the UI.
     */
    private void loadEvents() {
        usersList.clear();
        userRepository.getAllUsers(query -> {
            if (query.isSuccessful()) {
                List<DocumentSnapshot> docs = query.getResult().getDocuments();

                for (DocumentSnapshot doc : docs) {
                    // Parse each document to a User object
                    User user = doc.toObject(User.class);
                    if (user != null) {
                        usersList.add(user);
                    }
                }
                userProfilesAdapter.updateList(usersList);
                binding.noEventsText.setVisibility(usersList.isEmpty() ? View.VISIBLE : View.GONE);
            } else {
                System.err.println("Failed to fetch users: " + query.getException().getMessage());
                binding.noEventsText.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Deletes a user and updates the UI.
     *
     * @param user The user to be deleted.
     * @param pos  The position of the user in the list.
     */
    private void deleteUser(User user, int pos) {
        // Create a dialog instance
        Dialog dialog = new Dialog(requireContext());

        // Inflate the custom layout using View Binding
        LayoutDeleteUserAdminDialogBinding binding = LayoutDeleteUserAdminDialogBinding.inflate(LayoutInflater.from(requireContext()));
        dialog.setContentView(binding.getRoot());

        // Set up the dialog views
        binding.firstName.setText(user.getFirstName());
        binding.lastName.setText(user.getLastName());
        binding.idNumber.setText(String.valueOf(pos));
        binding.delete.setOnClickListener(view -> {
            userRepository.deleteUser(user.getDeviceId());
            usersList.remove(pos);
            userProfilesAdapter.notifyDataSetChanged();
            Toast.makeText(requireContext(), "User deleted successfully!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        binding.cancel.setOnClickListener(view -> dialog.dismiss());

        // Show the dialog
        dialog.show();
    }

    /**
     * Fetches facility details for a user and displays them in a dialog.
     *
     * @param user The user whose facility details are to be fetched.
     * @param pos  The position of the user in the list.
     */
    private void facilityDetails(User user, int pos) {
        facilityRepository.getFacilityByOwnerId(user.getDeviceId(), query -> {
            if (query.isSuccessful()) {
                for (DocumentSnapshot document : query.getResult().getDocuments()) {
                    Facility facility = document.toObject(Facility.class);
                    if (facility != null) {
                        showFacilityDialog(facility);
                    } else {
                        Toast.makeText(requireContext(), "Facility not found", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                System.err.println("Failed to fetch facilities: " + query.getException().getMessage());
            }
        });
    }

    /**
     * Displays facility details in a dialog.
     *
     * @param facility The facility whose details are to be displayed.
     */
    private void showFacilityDialog(Facility facility) {
        // Create a dialog instance
        Dialog dialog = new Dialog(requireContext());

        // Inflate the custom layout using View Binding
        LayoutUserFacilityDetailsDialogBinding binding = LayoutUserFacilityDetailsDialogBinding.inflate(LayoutInflater.from(requireContext()));
        dialog.setContentView(binding.getRoot());

        if (facility != null) {
            // Set up the dialog views
            binding.facilityName.setText(facility.getFacilityName());
            binding.location.setText(facility.getLocation());
            binding.delete.setOnClickListener(view -> facilityRepository.deleteFacility(facility.getFacilityId(), task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(requireContext(), "Facility deleted successfully!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    Toast.makeText(requireContext(), "Failed to delete facility: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    System.err.println("Failed to delete facility: " + task.getException().getMessage());
                }
            }));
        }

        binding.cancel.setOnClickListener(view -> dialog.dismiss());
        // Show the dialog
        dialog.show();
    }

    /**
     * Callback for when a delete button is clicked in the adapter.
     *
     * @param user     The user to be deleted.
     * @param position The position of the user in the list.
     */
    @Override
    public void onDeleteClick(User user, int position) {
        deleteUser(user, position);
    }

    /**
     * Callback for when a facility button is clicked in the adapter.
     *
     * @param user     The user whose facility details are to be fetched.
     * @param position The position of the user in the list.
     */
    @Override
    public void onFacilityClick(User user, int position) {
        facilityDetails(user, position);
    }
}