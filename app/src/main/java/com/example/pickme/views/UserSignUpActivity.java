package com.example.pickme.views;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pickme.R;
import com.example.pickme.models.User;
import com.example.pickme.repositories.UserRepository;

/**
 * Handles the sign-up process for new users entering our app.
 *
 * @author Kenneth (aesoji)
 * @version 1.0
 *
 * Responsibilities:
 * - Creates a new document for the user.
 * - Information will always contain their firstName, deviceId, and userAuthId.
 */
public class UserSignUpActivity extends AppCompatActivity {

    private EditText firstNameEditText, lastNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_signup_activity); // Ensure this is the correct layout file

        // Initialize Views and Data
        firstNameEditText = findViewById(R.id.signupFirstNameEdit);
        lastNameEditText = findViewById(R.id.signupLastNameEdit);
        Button submitButton = findViewById(R.id.signupSubmitButton);

        // Sets up the response to if the user wants to proceed.
        submitButton.setOnClickListener(v -> validateInformation());
    }

    private void validateInformation() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();

        // Validates the first name using User class's validation function
        if (User.validateFirstName(firstName)) {
            Toast.makeText(this, "Invalid first name. Please enter a valid first name.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validates the optional last name using User class's validation function
        if (!lastName.isEmpty() && User.validateLastName(lastName)) {
            Toast.makeText(this, "Invalid last name. Please enter a valid last name.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sets up remaining information to ensure a successful initialization.
        String deviceId = getDeviceID();
        String dummyEmail = "temp@tempmail.com";
        String dummyContact = "0-000-000-0000";

        // Create user in Firebase with the provided and dummy data
        createUser(firstName, lastName.isEmpty() ? "" : lastName, dummyEmail, dummyContact, deviceId);
    }

    private String getDeviceID() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private void createUser(String firstName, String lastName, String email, String contact, String deviceId) {
        // Creates the user in our firebase.
        UserRepository userRepository = new UserRepository();
        userRepository.addUser(firstName, lastName, email, contact, deviceId, new UserRepository.OnUserCreatedCallback() {
            @Override
            public void onSuccess(User user) {
                Toast.makeText(UserSignUpActivity.this, firstName + " " + (lastName.isEmpty() ? "" : lastName) + " has been added successfully!", Toast.LENGTH_SHORT).show();

                // Navigate to HomeActivity after successful save
                Intent intent = new Intent(UserSignUpActivity.this, HomeActivity.class);
                intent.putExtra("user_data", user);
                startActivity(intent);
                clearText();
                finish();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(UserSignUpActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                Log.e("UserRepository", "Error: " + errorMessage);
            }
        });
    }

    private void clearText() {
        firstNameEditText.setText("");
        lastNameEditText.setText("");
    }
}

/**
 * Coding Sources
 * <p>
 * Stack Overflow
 * - "What is the difference between a Fragment and Intent/Activity?"
 **/
