package com.example.pickme.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.pickme.R;
import com.example.pickme.models.User;
import com.example.pickme.repositories.UserRepository;

public class UserProfileActivity extends AppCompatActivity {

    private TextView profileFullName, profileEmailAddress, profileContactNumber;
    private SwitchCompat profileLocationSwitch, profileNotificationOrganizerSwitch, profileNotificationAdminSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_main);

        // Initialization of Views
        profileFullName = findViewById(R.id.profileFullName);
        profileEmailAddress = findViewById(R.id.profileEmailAddress);
        profileContactNumber = findViewById(R.id.profileContactNumber);
        profileLocationSwitch = findViewById(R.id.profileLocationSwitch);
        profileNotificationOrganizerSwitch = findViewById(R.id.profileNotificationOrganizerSwitch);
        profileNotificationAdminSwitch = findViewById(R.id.profileNotificationAdminSwitch);
        // TODO: Add the Profile Pictures initialization here
        ImageButton editButton = findViewById(R.id.profileEditButton);
        Button editGoBackButton = findViewById(R.id.profileMainGoBackButton);

        loadUserData();

        // Set up button click listeners
        editButton.setOnClickListener(v -> navigateToEditActivity());
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
            // TODO: Load the Profile Picture using user.getProfilePictureUrl();
        } else {
            Toast.makeText(this, "User data not available.", Toast.LENGTH_SHORT).show();
        }
    }

    //---------- Saving Process --------------------
    private void saveUserData() {
        User user = User.getInstance();
        if (user == null) {
            showToast("User data not available. Cannot save to Firestore.");
            navigateToHomeActivity();
            return;
        }

        updateUserInstance(user);
        saveUserToFirestore(user);
    }

    private void updateUserInstance(User user) {
        user.setGeoLocationEnabled(profileLocationSwitch.isChecked());
        user.setNotificationEnabled(profileNotificationOrganizerSwitch.isChecked());
        user.setAdmin(profileNotificationAdminSwitch.isChecked());
        // TODO: Save the Profile Picture by setting and using what ever validation cases you need.
    }

    private void saveUserToFirestore(User user) {
        UserRepository userRepository = new UserRepository();
        userRepository.updateUser(user, task -> {
            if (task.isSuccessful()) {
                Log.d("UserProfileActivity", "User data updated successfully in Firestore.");
                showToast("User data saved.");
                navigateToHomeActivity(); // It will only navigate if the save is successful.
            } else {
                showToast("Failed to save user data.");
            }
        });
    }

    //---------- Activity Redirection --------------------
    private void navigateToEditActivity() {
        // Navigates to EditActivity for more updatable information.
        Intent intent = new Intent(UserProfileActivity.this, UserProfileEditActivity.class);
        startActivity(intent);
    }

    private void navigateToHomeActivity() {
        // Navigates to HomeActivity.
        Intent intent = new Intent(UserProfileActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    //---------- Android Popups --------------------
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}

/**
 * Coding Sources
 * <p>
 * ChatGBT
 * - "How do I use SharedPreferences?"
 * - "What are the parameters of SharedPreferences?"
 * - "How do I set up a Firebase Firestore database collection?"
 * - "How do I properly setup a Map / Hashmap?"
 * <p>
 * Stack Overflow
 * - "What is the difference between the HashMap and Map objects in Java?"
 * - "Persistent Data Storage in Android Development"
 * <p>
 * Android Developers
 * - "Save simple data with SharedPreferences"
 * - "Data and file storage overview"
 **/
