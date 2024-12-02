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

public class UserProfilesFragment extends Fragment implements UserProfilesAdapter.OnItemClickListener{

    private UserAdminProfilesBinding binding;
    private UserRepository userRepository;
    private FacilityRepository facilityRepository;
    private List<User> usersList = new ArrayList<>();
    private UserProfilesAdapter userProfilesAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = UserAdminProfilesBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

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

    private void facilityDetails(User user, int pos) {
        // Create a dialog instance
        Dialog dialog = new Dialog(requireContext());

        // Inflate the custom layout using View Binding
        LayoutUserFacilityDetailsDialogBinding binding = LayoutUserFacilityDetailsDialogBinding.inflate(LayoutInflater.from(requireContext()));
        dialog.setContentView(binding.getRoot());

        facilityRepository.getFacilityByOwnerId(user.getDeviceId(), query -> {
            if (query.isSuccessful()) {
                for (DocumentSnapshot document : query.getResult().getDocuments()) {
                    Facility facility = document.toObject(Facility.class);
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
                }

            } else {
                System.err.println("Failed to fetch facilities: " + query.getException().getMessage());
            }
        });

        binding.cancel.setOnClickListener(view -> dialog.dismiss());
        // Show the dialog
        dialog.show();
    }
    @Override
    public void onDeleteClick(User user, int position) {
        deleteUser(user, position);
    }

    @Override
    public void onFacilityClick(User user, int position) {
        facilityDetails(user, position);
    }
}