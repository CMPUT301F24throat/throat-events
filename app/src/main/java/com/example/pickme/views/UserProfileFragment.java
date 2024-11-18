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
import com.example.pickme.models.User;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileFragment extends Fragment {

    private TextView profileFullName, profileEmailAddress, profileContactNumber;
    private TextView profileLocationText, profileNotificationText, profileAdminText;
    private ImageView profileLocationIcon, profileNotificationIcon, profileAdminIcon;
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
        profileLocationText = view.findViewById(R.id.profileLocationText);
        profileNotificationText = view.findViewById(R.id.profileNotificationText);
        profileAdminText = view.findViewById(R.id.profileAdminText);
        profileLocationIcon = view.findViewById(R.id.profileLocationIcon);
        profileNotificationIcon = view.findViewById(R.id.profileNotificationIcon);
        profileAdminIcon = view.findViewById(R.id.profileAdminIcon);
        profilePicture = view.findViewById(R.id.profilePicture);
        ImageButton editButton = view.findViewById(R.id.profileEditButton);
        Button editGoBackButton = view.findViewById(R.id.profileMainGoBackButton);

        loadUserData();

        // Set up button click listeners
        editButton.setOnClickListener(v -> navigateToEditFragment());
        editGoBackButton.setOnClickListener(v -> navigateToHomeFragment());
    }

    private void loadUserData() {
        User user = User.getInstance();

        if (user != null) {
            profileFullName.setText(user.fullName(user.getFirstName(), user.getLastName()));
            profileEmailAddress.setText(user.getEmailAddress());
            profileContactNumber.setText(user.getContactNumber());

            // Set drawable icons based on user settings
            profileLocationIcon.setImageResource(user.isGeoLocationEnabled() ? R.drawable.ic_enabled : R.drawable.ic_disabled);
            profileNotificationIcon.setImageResource(user.isNotificationEnabled() ? R.drawable.ic_enabled : R.drawable.ic_disabled);
            profileAdminIcon.setImageResource(user.isAdmin() ? R.drawable.ic_enabled : R.drawable.ic_disabled);

            Glide.with(this)
                    .load(user.getProfilePictureUrl())
                    .into(profilePicture);
        } else {
            Toast.makeText(getContext(), "User data not available.", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToEditFragment() {
        Navigation.findNavController(getView()).navigate(R.id.action_userProfileFragment_to_userProfileEditFragment);
    }

    private void navigateToHomeFragment() {
        Navigation.findNavController(getView()).navigate(R.id.action_userProfileFragment_to_homeFragment);
    }
}