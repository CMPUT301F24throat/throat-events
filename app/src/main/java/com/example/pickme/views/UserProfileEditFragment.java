package com.example.pickme.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.pickme.R;
import com.example.pickme.models.User;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserProfileEditFragment extends Fragment {

    private EditText editProfileFirstName;
    private EditText editProfileLastName;
    private EditText editProfileEmailAddress;
    private EditText editProfileContactNumber;
    private SwitchCompat editEnableAdminView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_profile_edit, container, false);

        // Initializations of Views and Data
        editProfileFirstName = view.findViewById(R.id.editProfileFirstName);
        editProfileLastName = view.findViewById(R.id.editProfileLastName);
        editProfileEmailAddress = view.findViewById(R.id.editProfileEmailAddress);
        editProfileContactNumber = view.findViewById(R.id.editProfileContactNumber);
        editEnableAdminView = view.findViewById(R.id.editEnableAdminView);
        Button saveButton = view.findViewById(R.id.editSaveButton);
        Button goBackButton = view.findViewById(R.id.editProfileGoBackButton);

        saveButton.setOnClickListener(v -> validateChanges()); // Listens for the Save Changes Button.
        goBackButton.setOnClickListener(v -> navigateToProfileMain()); // Listens for the Go Back Button.

        loadUserData(); // Loads the user data

        return view;
    }

    private void loadUserData() {
        User user = User.getInstance();
        if (user != null) {
            // Fill in user information
            editProfileFirstName.setText(user.getFirstName());
            editProfileLastName.setText(user.getLastName());
            editProfileEmailAddress.setText(user.getEmailAddress());
            editProfileContactNumber.setText(user.getContactNumber());

            // Show or hide the admin switch based on user type
            if (user.isAdmin()) {
                editEnableAdminView.setChecked(user.isAdmin());
                editEnableAdminView.setVisibility(View.VISIBLE); // Show the switch
            } else {
                editEnableAdminView.setVisibility(View.GONE); // Hide the switch
            }
        } else {
            // Handle the case where the user instance is null
            Toast.makeText(getContext(), "User data not loaded.", Toast.LENGTH_SHORT).show();
        }
    }

    private void validateChanges() {
        String firstName = editProfileFirstName.getText().toString().trim();
        String lastName = editProfileLastName.getText().toString().trim();
        String emailAddress = editProfileEmailAddress.getText().toString().trim();
        String contactNumber = editProfileContactNumber.getText().toString().trim();
        boolean isAdmin = editEnableAdminView.isChecked();

        // Validation Cases of User Information
        if (!User.validateFirstName(firstName)) {
            Toast.makeText(getContext(), "Please only use letters for your first name.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!User.validateLastName(lastName)) {
            Toast.makeText(getContext(), "Please only use letters for your last name.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!User.validateEmailAddress(emailAddress)) {
            Toast.makeText(getContext(), "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!User.validateContactInformation(contactNumber)) {
            Toast.makeText(getContext(), "Please enter a valid contact number.", Toast.LENGTH_SHORT).show();
            return;
        }
        updateFirestoreUserData(firstName, lastName, emailAddress, contactNumber, isAdmin);
    }

    private void updateFirestoreUserData(String firstName, String lastName, String emailAddress, String contactNumber, boolean isAdmin) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = User.getInstance().getUserId(); // Get the user's ID
        db.collection("users").document(userId)
                .update("firstName", firstName,
                        "lastName", lastName,
                        "emailAddress", emailAddress,
                        "contactNumber", contactNumber,
                        "isAdmin", isAdmin)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    navigateToProfileMain(); // Go back after saving
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateToProfileMain() {
        requireActivity(); // Return to Profile Main Fragment.
    }

}

