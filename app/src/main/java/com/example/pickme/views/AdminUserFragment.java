package com.example.pickme.views;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.pickme.R;
import com.example.pickme.databinding.FragmentAdminUsersBinding;
import com.example.pickme.databinding.LayoutDeleteUserAdminDialogBinding;
import com.example.pickme.databinding.LayoutUserFacilityDetailsDialogBinding;
import com.example.pickme.models.Facility;
import com.example.pickme.models.User;
import com.example.pickme.repositories.FacilityRepository;
import com.example.pickme.repositories.UserRepository;
import com.example.pickme.views.adapters.UserProfilesAdapter;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment displaying the apps list of users for administrative purposes.
 * Provides functionality to refresh the user list, delete their accounts, and search/filter users.
 */
public class AdminUserFragment extends Fragment implements UserProfilesAdapter.OnItemClickListener{

    private FragmentAdminUsersBinding binding;
    private UserRepository userRepository;
    private FacilityRepository facilityRepository;
    private List<User> usersList = new ArrayList<>();
    private UserProfilesAdapter userProfilesAdapter;

    /**
     * Called to inflate the fragment's view.
     *
     * @param inflater The LayoutInflater object to inflate the view.
     * @param container The container this fragment is attached to.
     * @param savedInstanceState The saved instance state if the fragment is being re-created.
     * @return The root view of the fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminUsersBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    /**
     * Called after the fragment's view has been created. Initializes repositories,
     * sets up the adapter for the user list, and handles UI interactions like refreshing the list.
     *
     * @param view The root view of the fragment.
     * @param savedInstanceState The saved instance state from the last session.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userRepository = UserRepository.getInstance();
        facilityRepository = FacilityRepository.getInstance();

        // Refresh button logic
        ImageView refreshButton = view.findViewById(R.id.imageCatalogRefresh);
        refreshButton.setOnClickListener(v ->  {
            usersList.clear();
            userProfilesAdapter.updateList(usersList);
            binding.searchBar.setText("");
            binding.noUsersText.setVisibility(View.GONE);
            loadUserData();
        });

        // Back button logic
        binding.imageCatalogBack.setOnClickListener(listener -> Navigation.findNavController(requireView()).navigateUp());

        // Setting up the RecyclerView
        binding.allUsersList.setLayoutManager(new LinearLayoutManager(requireActivity()));
        userProfilesAdapter = new UserProfilesAdapter(requireActivity(), usersList, this);
        binding.allUsersList.setAdapter(userProfilesAdapter);

        // Search bar logic to filter users
        binding.searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userProfilesAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        loadUserData();
    }


    /**
     * Loads the user data into the list, typically from a remote source or database.
     * This method fetches data from userRepository.
     */
    private void loadUserData() {
        usersList.clear();
        binding.noUsersText.setVisibility(View.GONE);

        // Fetch data from userRepository.
        userRepository.getAllUsers(query -> {
            if (query.isSuccessful()) {
                List<DocumentSnapshot> docs = query.getResult().getDocuments();

                for (DocumentSnapshot doc : docs) {
                    User user = doc.toObject(User.class);
                    if (user != null) {
                        usersList.add(user);
                    }
                }

                // Notify the adapter that the data has changed.
                userProfilesAdapter.updateList(usersList);

                // Backup message for is user information is not loading.
                if (usersList.isEmpty()) {
                    binding.noUsersText.setVisibility(View.VISIBLE);
                } else {
                    binding.noUsersText.setVisibility(View.GONE);
                }

            } else {
                binding.noUsersText.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * This method is called when a user profile item is clicked.
     *
     * @param user The user item that was clicked.
     * @param pos The position of the clicked item in the list.
     */
    @SuppressLint("NotifyDataSetChanged")
    private void deleteUser(User user, int pos) {

        // Inflate the custom layout via View Binding
        LayoutDeleteUserAdminDialogBinding binding = LayoutDeleteUserAdminDialogBinding.inflate(LayoutInflater.from(requireContext()));
        AlertDialog dialogue = new AlertDialog.Builder(requireContext())
                .setView(binding.getRoot())
                .create();

        dialogue.setContentView(binding.getRoot());

        // Set up the dialogue views
        binding.idUser.setText(user.getDeviceId());
        binding.nameUser.setText(user.fullName(user.getFirstName(), user.getLastName()));
        binding.deleteButton.setOnClickListener(view -> {
            userRepository.deleteUser(user.getDeviceId());
            dialogue.dismiss();
            usersList.clear();
            userProfilesAdapter.notifyDataSetChanged();
            Toast.makeText(requireContext(), "User deleted successfully!", Toast.LENGTH_SHORT).show();
            userProfilesAdapter.updateList(usersList);
            loadUserData();
        });

        binding.cancelButton.setOnClickListener(view -> dialogue.dismiss());

        dialogue.show();
    }

    /**
     * Fetches the facility details associated with a specific user by their device ID.
     * If the user has a facility, it displays a dialog with the facility details.
     * If no facility is found, a toast message is shown to inform the user.
     *
     * @param user The user whose associated facility details are being fetched.
     * @param pos The position of the user in the list (if needed for any further logic).
     */
    private void facilityDetails(User user, int pos) {
        facilityRepository.getFacilityByOwnerId(user.getDeviceId(), query -> {
            if (query.isSuccessful()) {
                boolean hasFacility = false; // Flag to check if any facility is found
                for (DocumentSnapshot document : query.getResult().getDocuments()) {
                    Facility facility = document.toObject(Facility.class);
                    if (facility != null) {
                        hasFacility = true;
                        showFacilityDialog(facility); // Show the dialog with facility details
                    }
                }
                if (!hasFacility) {
                    // If no facility is found for this user, show a toast message
                    Toast.makeText(requireContext(), "This user does not have a facility.", Toast.LENGTH_SHORT).show();
                }
            } else {
                System.err.println("Failed to fetch facilities: " + query.getException().getMessage());
            }
        });
    }


    /**
     * Displays a dialogue showing the details of a facility.
     * The dialogue allows the user to either delete the facility or cancel the action.
     * If the facility is successfully deleted, a toast message is shown.
     * If the deletion fails, an error message is displayed.
     *
     * @param facility The facility whose details are displayed in the dialog.
     */
    private void showFacilityDialog(Facility facility){
        // Create a dialogue instance
        Dialog dialogue = new Dialog(requireContext());

        // Inflate the custom layout using View Binding
        LayoutUserFacilityDetailsDialogBinding binding = LayoutUserFacilityDetailsDialogBinding.inflate(LayoutInflater.from(requireContext()));
        dialogue.setContentView(binding.getRoot());

        if (facility != null) {
            // Set up the dialogue views
            binding.facilityName.setText(facility.getFacilityName());
            binding.location.setText(facility.getLocation());
            binding.delete.setOnClickListener(view -> facilityRepository.deleteFacility(facility.getFacilityId(), task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(requireContext(), "Facility deleted successfully!", Toast.LENGTH_SHORT).show();
                    dialogue.dismiss();
                } else {
                    dialogue.dismiss();
                    Toast.makeText(requireContext(), "Failed to delete facility: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    System.err.println("Failed to delete facility: " + task.getException().getMessage());
                }
            }));
        }

        binding.cancel.setOnClickListener(view -> dialogue.dismiss());

        dialogue.show();
    }

    /**
     * Handles user item clicks. This method is called when a user profile item is clicked.
     *
     * @param user The user item that was clicked.
     * @param position The position of the clicked item in the list.
     */
    @Override
    public void onDeleteClick(User user, int position) {
        deleteUser(user, position);
    }

    /**
     * Handles facility details clicks. This method is called when the facility details button is clicked.
     *
     * @param user The user whose facility details are being viewed.
     * @param position The position of the clicked item in the list.
     */
    @Override
    public void onFacilityClick(User user, int position) {
        facilityDetails(user, position);
    }

}