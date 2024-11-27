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

    private void validateInformation() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();

        if (User.validateFirstName(firstName)) {
            Toast.makeText(getContext(), "Invalid first name. Please enter a valid first name.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!lastName.isEmpty() && User.validateLastName(lastName)) {
            Toast.makeText(getContext(), "Invalid last name. Please enter a valid last name.", Toast.LENGTH_SHORT).show();
            return;
        }

        String deviceId = getDeviceID();
        String dummyEmail = "temp@tempmail.com";
        String dummyContact = "0-000-000-0000";

        createUser(firstName, lastName.isEmpty() ? "" : lastName, dummyEmail, dummyContact, deviceId);
    }

    private String getDeviceID() {
        return Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private void createUser(String firstName, String lastName, String email, String contact, String deviceId) {
        UserRepository userRepository = new UserRepository();
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

    private void clearText() {
        firstNameEditText.setText("");
        lastNameEditText.setText("");
    }

    private void navigateToHomeFragment() {
        Navigation.findNavController(getView()).navigate(R.id.action_userSignUpFragment_to_homeFragment);
    }

    private void showBottomNavigation() {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.findViewById(R.id.bottom_navigation).setVisibility(View.VISIBLE);
        }
    }
}