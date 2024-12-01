package com.example.pickme.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.pickme.R;
import com.example.pickme.models.Facility;
import com.example.pickme.models.User;
import com.example.pickme.repositories.FacilityRepository;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Fragment representing the user profile screen.
 */
public class UserProfileFragment extends Fragment {

    private TextView profileFullName, profileEmailAddress, profileContactNumber;
    private ImageView profileLocationIcon, profileNotificationIcon, profileAdminIcon;
    private CircleImageView profilePicture;
    private User currentUser;

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_profile, container, false);
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

        // Get the current user
        currentUser = User.getInstance();

        // Initialization of Views
        profileFullName = view.findViewById(R.id.profileFullName);
        profileEmailAddress = view.findViewById(R.id.profileEmailAddress);
        profileContactNumber = view.findViewById(R.id.profileContactNumber);
        profileLocationIcon = view.findViewById(R.id.profileLocationIcon);
        profileNotificationIcon = view.findViewById(R.id.profileNotificationIcon);
        profilePicture = view.findViewById(R.id.profilePicture);
        ImageButton editButton = view.findViewById(R.id.profileEditButton);
        Button editGoBackButton = view.findViewById(R.id.profileMainGoBackButton);
        Button manageFacilityButton = view.findViewById(R.id.userProfile_manageFacilityBtn);

        loadUserData();

        // Set up button click listeners
        editButton.setOnClickListener(v -> navigateToEditFragment());
        editGoBackButton.setOnClickListener(v -> navigateToHomeFragment());
        manageFacilityButton.setOnClickListener(v -> navigateToManageFacility());
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     */
    @Override
    public void onResume() {
        super.onResume();
        loadUserData(); // Reload user data when the fragment resumes
    }

    /**
     * Loads the user data into the views.
     */
    private void loadUserData() {
        if (currentUser != null) {
            profileFullName.setText(currentUser.fullName(currentUser.getFirstName(), currentUser.getLastName()));
            profileEmailAddress.setText(currentUser.getEmailAddress());
            profileContactNumber.setText(currentUser.getContactNumber());

            // Set drawable icons based on user settings
            profileLocationIcon.setImageResource(currentUser.isGeoLocationEnabled() ? R.drawable.ic_enabled : R.drawable.ic_disabled);
            profileNotificationIcon.setImageResource(currentUser.isNotificationEnabled() ? R.drawable.ic_enabled : R.drawable.ic_disabled);

            Glide.with(this)
                    .load(currentUser.getProfilePictureUrl())
                    .into(profilePicture);
        } else {
            Toast.makeText(getContext(), "User data not available.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Navigates to the manage facility screen.
     */
    private void navigateToManageFacility() {
        if (currentUser != null) {
            FacilityRepository.getInstance().getFacilityByOwnerDeviceId(currentUser.getDeviceId(), task -> {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    Facility facility = task.getResult().toObjects(Facility.class).get(0);
                    FacilityEditDialog.showDialog(getParentFragmentManager(), facility);
                } else {
                    // If user doesn't have an existing facility display toast
                    // They will have to go to MyEvents and make a facility
                    Toast.makeText(getContext(), "You do not have an existing facility! Go to MyEvents and make a facility.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    /**
     * Navigates to the edit profile screen.
     */
    private void navigateToEditFragment() {
        Navigation.findNavController(requireView()).navigate(R.id.action_userProfileFragment_to_userProfileEditFragment);
    }

    /**
     * Navigates to the home screen.
     */
    private void navigateToHomeFragment() {
        Navigation.findNavController(requireView()).navigate(R.id.action_userProfileFragment_to_homeFragment);
    }
}