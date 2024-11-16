package com.example.pickme.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.bumptech.glide.Glide;
import com.example.pickme.R;
import com.example.pickme.models.Image;
import com.example.pickme.models.User;
import com.example.pickme.repositories.UserRepository;
import com.google.android.gms.tasks.OnCompleteListener;

import de.hdodenhof.circleimageview.CircleImageView;

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
    private CircleImageView editProfilePicture;
    private ImageButton removeProfilePicture;
    private Image img;

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
        editProfilePicture = findViewById(R.id.editProfilePicture);
        removeProfilePicture = findViewById(R.id.removeProfilePicture);
        Button saveButton = findViewById(R.id.editSaveButton);
        Button goBackButton = findViewById(R.id.editProfileGoBackButton);

        loadUserData();

        // profile image gallery picker
        ActivityResultLauncher<PickVisualMediaRequest> pickPfp =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // callback is invoked after the user selects a media item or closes the photo picker
                    if (uri != null) {
                        editProfilePicture.setTag("");
                        // uploads the image selected
                        img.upload(uri, task -> {
                            if (task.isSuccessful()) {
                                Image i = task.getResult();
                                img.setImageUrl(i.getImageUrl());
                                // preview
                                Glide.with(editProfilePicture.getRootView())
                                        .load(img.getImageUrl())
                                        .into(editProfilePicture);
                            }
                        });

                        Toast.makeText(
                                this,
                                "Profile picture uploaded!",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });

        // launches the gallery picker when the user clicks the profile picture
        editProfilePicture.setOnClickListener(v -> {
            pickPfp.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        removeProfilePicture.setOnClickListener(v -> {
            Toast.makeText(this, "Profile picture deleted.", Toast.LENGTH_SHORT).show();
            editProfilePicture.setImageBitmap(null);
            editProfilePicture.setTag("deleted");
        });

        // Sets up the response to if the user wants to save their information.
        saveButton.setOnClickListener(v -> {
            if (validateUserChanges()) {
                saveUserData();
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

            img = new Image(user.getUserId(), user.getUserId());
            img.setImageUrl(user.getProfilePictureUrl());
            Glide.with(editProfilePicture.getRootView())
                    .load(img.getImageUrl())
                    .into(editProfilePicture);
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

        // Validates firstName
        if (!User.validateFirstName(firstName)) {
            Toast.makeText(this, "Please enter a valid first name.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validates lastName
        if (!lastName.isEmpty() && !User.validateLastName(lastName)) {
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

    private void saveUserData() {
        // Retrieve and update the current user instance with the new data from input fields
        User user = User.getInstance();
        if (user == null) {
            showToast("User data not available. Cannot save to Firestore.");
            return;
        }

        if (editProfilePicture.getTag() == "deleted") {
            generateProfilePicture(user, task -> {
                Log.d("Image", "generate: New profile picture generated");
                updateUserInstanceWithInput(user);
                pushUserToFirestore(user);
                navigateToHomeActivity();
            });

        } else {
            updateUserInstanceWithInput(user);
            pushUserToFirestore(user);
            navigateToHomeActivity();
        }
    }

    private void pushUserToFirestore(User user) {
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
        user.setProfilePictureUrl(img.getImageUrl());
    }

    private void generateProfilePicture(User user, OnCompleteListener<Image> listener) {
        String firstName = editProfileFirstName.getText().toString().trim();
        String lastName = editProfileLastName.getText().toString().trim();
        String initials = String.valueOf(firstName.charAt(0)) + lastName.charAt(0);

        img.generate(initials, listener);
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
