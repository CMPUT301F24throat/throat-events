package com.example.pickme.views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.pickme.R;
import com.example.pickme.models.User;
import com.example.pickme.repositories.UserRepository;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileFragment extends Fragment {

    private TextView profileFullName, profileEmailAddress, profileContactNumber;
    private SwitchCompat profileLocationSwitch, profileNotificationOrganizerSwitch, profileNotificationAdminSwitch;
    private CircleImageView profilePicture;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_mainprofile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialization of Views
        profileFullName = view.findViewById(R.id.profileFullName);
        profileEmailAddress = view.findViewById(R.id.profileEmailAddress);
        profileContactNumber = view.findViewById(R.id.profileContactNumber);
        profileLocationSwitch = view.findViewById(R.id.profileLocationSwitch);
        profileNotificationOrganizerSwitch = view.findViewById(R.id.profileNotificationOrganizerSwitch);
        profileNotificationAdminSwitch = view.findViewById(R.id.profileNotificationAdminSwitch);
        profilePicture = view.findViewById(R.id.profilePicture);
        ImageButton editButton = view.findViewById(R.id.profileEditButton);
        Button editGoBackButton = view.findViewById(R.id.profileMainGoBackButton);

        loadUserData();

        // Set up button click listeners
        editButton.setOnClickListener(v -> navigateToEditFragment());
        editGoBackButton.setOnClickListener(v -> saveUserData());
    }

    private void loadUserData() {
        User user = User.getInstance();

        if (user != null) {
            profileFullName.setText(user.fullName(user.getFirstName(), user.getLastName()));
            profileEmailAddress.setText(user.getEmailAddress());
            profileContactNumber.setText(user.getContactNumber());
            profileLocationSwitch.setChecked(user.isGeoLocationEnabled());
            profileNotificationOrganizerSwitch.setChecked(user.isNotificationEnabled());
            profileNotificationAdminSwitch.setChecked(user.isAdmin());
            Glide.with(this)
                    .load(user.getProfilePictureUrl())
                    .into(profilePicture);
        } else {
            Toast.makeText(getContext(), "User data not available.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserData() {
        User user = User.getInstance();
        if (user == null) {
            showToast("User data not available. Cannot save to Firestore.");
            navigateToHomeFragment();
            return;
        }

        updateUserInstance(user);
        saveUserToFirestore(user);
    }

    private void updateUserInstance(User user) {
        user.setGeoLocationEnabled(profileLocationSwitch.isChecked());
        user.setNotificationEnabled(profileNotificationOrganizerSwitch.isChecked());
        user.setAdmin(profileNotificationAdminSwitch.isChecked());
    }

    private void saveUserToFirestore(User user) {
        UserRepository userRepository = new UserRepository();
        userRepository.updateUser(user, task -> {
            if (task.isSuccessful()) {
                Log.d("UserProfileFragment", "User data updated successfully in Firestore.");
                showToast("User data saved.");
                navigateToHomeFragment(); // It will only navigate if the save is successful.
            } else {
                showToast("Failed to save user data.");
            }
        });
    }

    private void navigateToEditFragment() {
        Navigation.findNavController(getView()).navigate(R.id.action_userProfileFragment_to_userProfileEditFragment);
    }

    private void navigateToHomeFragment() {
        Navigation.findNavController(getView()).navigate(R.id.action_userProfileFragment_to_homeFragment);
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}