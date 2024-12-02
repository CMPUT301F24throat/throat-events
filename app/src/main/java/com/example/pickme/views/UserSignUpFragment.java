package com.example.pickme.views;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.pickme.R;
import com.example.pickme.models.User;
import com.example.pickme.repositories.UserRepository;

/**
 * Fragment for user sign-up.
 */
public class UserSignUpFragment extends Fragment {

    private EditText firstNameEditText, lastNameEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Views and Data
        firstNameEditText = view.findViewById(R.id.signupFirstNameEdit);
        lastNameEditText = view.findViewById(R.id.signupLastNameEdit);
        Button submitButton = view.findViewById(R.id.signupSubmitButton);

        // Sets up the response to if the user wants to proceed.
        submitButton.setOnClickListener(v -> validateInformation());
    }

    /**
     * Validates the user input information.
     */
    private void validateInformation() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();

        if (!User.validateFirstName(firstName)) {
            Toast.makeText(getContext(), "Invalid first name. Please enter a valid first name.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!lastName.isEmpty() && !User.validateLastName(lastName)) {
            Toast.makeText(getContext(), "Invalid last name. Please enter a valid last name.", Toast.LENGTH_SHORT).show();
            return;
        }

        String deviceId = getDeviceID();
        String tempEmail = "User@domain.com";
        String tempContact = "000-000-0000";

        String finalLastName = lastName;
        if (lastName.isEmpty()) {
            finalLastName = "";
        }
        createUser(firstName, finalLastName, tempEmail, tempContact, deviceId);
    }

    /**
     * Retrieves the device ID.
     *
     * @return the device ID as a String.
     */
    private String getDeviceID() {
        return Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Creates a new user with the provided information.
     *
     * @param firstName the first name of the user.
     * @param lastName the last name of the user.
     * @param email the email address of the user.
     * @param contact the contact number of the user.
     * @param deviceId the device ID.
     */
    private void createUser(String firstName, String lastName, String email, String contact, String deviceId) {
        UserRepository userRepository = UserRepository.getInstance();
        userRepository.addUser(firstName, lastName, email, contact, deviceId, new UserRepository.OnUserCreatedCallback() {
            @Override
            public void onSuccess(User user) {
                Toast.makeText(getContext(), firstName + " " + (lastName.isEmpty() ? "" : lastName) + " has been added successfully!", Toast.LENGTH_SHORT).show();
                navigateToHomeFragment();
                clearText();
                showBottomNavigation();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                Log.e("UserRepository", "Error: " + errorMessage);
            }
        });
    }

    /**
     * Clears the text fields.
     */
    private void clearText() {
        firstNameEditText.setText("");
        lastNameEditText.setText("");
    }

    /**
     * Navigates to the Home Fragment.
     */
    private void navigateToHomeFragment() {
        Navigation.findNavController(requireView()).navigate(R.id.action_userSignUpFragment_to_homeFragment);
    }

    /**
     * Shows the bottom navigation.
     */
    private void showBottomNavigation() {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.findViewById(R.id.bottom_navigation).setVisibility(View.VISIBLE);
        }
    }
}