package com.example.pickme.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.pickme.R;
import com.example.pickme.models.User;
import com.example.pickme.repositories.UserRepository;

/**
 * An entry point for the app and is responsible for user authentication.
 *
 * @author Kenneth (aesoji)
 * @version 1.1
 *
 * Responsibilities:
 * - Saves and edits user firstName, lastName, emailAddress, contactInformation, and profilePicture.
 * - If the user is classified as an admin, displays access to admin mode.
 **/
public class UserProfileEditActivity extends AppCompatActivity {

    private EditText editProfileFirstName, editProfileLastName, editProfileEmailAddress, editProfileContactNumber;
    private SwitchCompat editEnableAdminView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_edit);

        // Initialization of Views
        editProfileFirstName = findViewById(R.id.editProfileFirstName);
        editProfileLastName = findViewById(R.id.editProfileLastName);
        editProfileEmailAddress = findViewById(R.id.editProfileEmailAddress);
        editProfileContactNumber = findViewById(R.id.editProfileContactNumber);
        editEnableAdminView = findViewById(R.id.editEnableAdminView);
        // TODO: Add the Profile Pictures initialization here
        Button saveButton = findViewById(R.id.editSaveButton);
        Button goBackButton = findViewById(R.id.editProfileGoBackButton);

        loadUserData();

        // Sets up the response to if the user wants to save their information.
        saveButton.setOnClickListener(v -> {
            if (validateUserChanges()) {
                saveUserDataToFirestore();
                navigateToHomeActivity();
            }
        });

        // Sets up the response to if the user wants to return to HomeActivity.
        goBackButton.setOnClickListener(v -> navigateToHomeActivity());
    }

    private void loadUserData() {
        User user = User.getInstance();

        if (user != null) {
            editProfileFirstName.setText(user.getFirstName());
            editProfileLastName.setText(user.getLastName());
            editProfileEmailAddress.setText(user.getEmailAddress());
            editProfileContactNumber.setText(user.getContactNumber());
            editEnableAdminView.setChecked(user.isAdmin());
            // TODO: Load the Profile Picture using user.getProfilePictureUrl();

        } else {
            Toast.makeText(this, "User data not available.", Toast.LENGTH_SHORT).show();
        }
    }

    //---------- Saving Process --------------------
    private boolean validateUserChanges() {
        // Validates the input fields and returns a boolean.
        String firstName = editProfileFirstName.getText().toString().trim();
        String lastName = editProfileLastName.getText().toString().trim();
        String emailAddress = editProfileEmailAddress.getText().toString().trim();
        String contactNumber = editProfileContactNumber.getText().toString().trim();
        // TODO: Save the Profile Picture using user.getProfilePictureUrl();

        // Validates firstName
        if (User.validateFirstName(firstName)) {
            Toast.makeText(this, "Please enter a valid first name.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validates lastName
        if (User.validateLastName(lastName)) {
            Toast.makeText(this, "Please enter a valid last name.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validates emailAddress
        if (!User.validateEmailAddress(emailAddress)) {
            Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validates contactNumber
        if (!User.validateContactInformation(contactNumber)) {
            Toast.makeText(this, "Please enter a valid contact number.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveUserDataToFirestore() {
        // Retrieve and update the current user instance with the new data from input fields
        User user = User.getInstance();
        if (user == null) {
            showToast("User data not available. Cannot save to Firestore.");
            return;
        }

        updateUserInstanceWithInput(user);

        // Use UserRepository to handle the update in Firestore
        UserRepository userRepository = new UserRepository();
        userRepository.updateUser(user, task -> {
            if (task.isSuccessful()) {
                showToast("Profile saved successfully!");
            } else {
                showToast("Failed to save profile: " + (task.getException() != null ? task.getException().getMessage() : ""));
                Log.e("Firestore", "Error updating document", task.getException());
            }
        });
    }

    private void updateUserInstanceWithInput(User user) {
        // Update the user object with data from input fields
        user.setFirstName(editProfileFirstName.getText().toString().trim());
        user.setLastName(editProfileLastName.getText().toString().trim());
        user.setEmailAddress(editProfileEmailAddress.getText().toString().trim());
        user.setContactNumber(editProfileContactNumber.getText().toString().trim());
    }

    //---------- Activity Redirection --------------------
    private void navigateToHomeActivity() {
        Intent intent = new Intent(UserProfileEditActivity.this, UserProfileActivity.class);
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
  * - "How do I set up a Firebase Firestore database collection?"
  * - "How do I properly setup a Map / Hashmap?"
  * <p>
  * Stack Overflow
  * - "What is the difference between the HashMap and Map objects in Java?"
  * - "Persistent Data Storage in Android Development"
  * <p>
  * Android Developers
  * - "Data and file storage overview"
  **/
