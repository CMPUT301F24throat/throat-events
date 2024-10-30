package com.example.pickme.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pickme.R;
import com.example.pickme.models.User;
import com.example.pickme.repositories.UserRepository;
import com.google.android.ads.mediationtestsuite.activities.HomeActivity; // THIS ACTIVITY DOESN'T EXIST!
import com.google.firebase.auth.FirebaseAuth;

/**
 * SignUpFragment handles the user sign-up process within the application.
 *
 * @author Kenneth (aesoji)
 * @version 1.0
 *
 * Responsibilities:
 * - Handles user registration only expecting a valid first and last name.
 * - Interacts with UserRepository (Firease) for user authentication and data storing.
 * - Clears input fields and closes the fragment after a successful registration.
 */

import java.util.Objects;

public class UserSignUpFragment extends Fragment {

    private EditText firstNameEditText, lastNameEditText;
    private UserRepository userRepository;
    private FirebaseAuth auth;

    /**
     * Initialization of SignUpFragment.java and its first and last Name input fields.
     * Triggers the user creation process.
     *
     * @param inflater           The LayoutInflater used to inflate any views in the fragment.
     * @param container          The parent view that this fragment's UI is attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     *
     * @return The root View.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_signup_activity, container, false);

        // Initialization of Views and Data
        userRepository = new UserRepository();
        firstNameEditText = view.findViewById(R.id.signupFirstNameEdit);
        lastNameEditText = view.findViewById(R.id.signupLastNameEdit);
        Button submitButton = view.findViewById(R.id.signupSubmitButton);

        submitButton.setOnClickListener(v -> createUser()); // Listens for the Submit Button.

        return view;
    }

    /**
     * Validates the first and last names entered by the user.
     *
     * @param firstName The first name to validate.
     * @param lastName  The last name to validate.
     * @return true if both names are valid; false otherwise.
     */
    private boolean validateUserInputName(String firstName, String lastName) {
        return User.validateFirstName(firstName) && User.validateLastName(lastName);
    }

    /**
     * Starting process for creating a valid user in our Firebase
     */
    private void createUser() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();

        // Validate the names and proceed to user creation if valid
        if (validateUserInputName(firstName, lastName)) {
            createUserInFirebase(firstName, lastName);
        } else {
            Toast.makeText(getActivity(), "Please enter valid first and last names!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Creates a user in Firebase after validation.
     *
     * @param firstName The first name of the user.
     * @param lastName  The last name of the user.
     */
    private void createUserInFirebase(String firstName, String lastName) {
        FirebaseAuth.getInstance().signInAnonymously()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        // Condition if userAuth ID has been grabbed successfully.
                        String userAuthId = Objects.requireNonNull(task.getResult().getUser()).getUid();

                        // Create the User object using userAuth ID.
                        User newUser = new User(userRepository, userAuthId, firstName, lastName, "", "", "", false, "", true, false);
                        newUser.signup(firstName, lastName);

                        // Save user information if valid.
                        userRepository.addUser(newUser, addUserTask -> {
                            if (addUserTask.isSuccessful()) {
                                Toast.makeText(getActivity(), newUser + " has been added successfully!", Toast.LENGTH_SHORT).show();
                                clearFirstLastText();
                                startActivity(new Intent(getActivity(), HomeActivity.class)); // Direct the user to [HomeActivity].
                                requireActivity().finish(); // Closes the SignUpFragment. Not added to the back stack.
                            } else {
                                Toast.makeText(getActivity(), "Failed to save user data!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), "Authentication failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Extra function to clean fields just in case.
     */
    private void clearFirstLastText() {
        // Clears these input fields after a successful submission.
        firstNameEditText.setText("");
        lastNameEditText.setText("");
    }

}
