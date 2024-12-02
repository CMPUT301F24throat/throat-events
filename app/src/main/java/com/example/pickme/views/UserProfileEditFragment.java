package com.example.pickme.views;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.pickme.R;
import com.example.pickme.models.Image;
import com.example.pickme.models.User;
import com.example.pickme.repositories.UserRepository;
import com.example.pickme.utils.ImageQuery;
import com.google.android.gms.tasks.OnCompleteListener;

/**
 * Fragment for editing the user's profile.
 */
public class UserProfileEditFragment extends Fragment {

    private EditText editProfileFirstName, editProfileLastName, editProfileEmailAddress, editProfileContactNumber;
    private SwitchCompat editEnableLocation, editEnableNotifications;
    private ImageView editProfilePicture;
    private ImageButton removeProfilePicture;
    Button saveButton;
    Button goBackButton;
    private boolean isChanged = false;
    private Image img;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_profile_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the Views
        editProfileFirstName = view.findViewById(R.id.editProfileFirstName);
        editProfileLastName = view.findViewById(R.id.editProfileLastName);
        editProfileEmailAddress = view.findViewById(R.id.editProfileEmailAddress);
        editProfileContactNumber = view.findViewById(R.id.editProfileContactNumber);
        editEnableLocation = view.findViewById(R.id.editProfileEnableLocation);
        editEnableNotifications = view.findViewById(R.id.editProfileEnableNotifications);
        editProfilePicture = view.findViewById(R.id.editProfilePicture);
        removeProfilePicture = view.findViewById(R.id.removeProfilePicture);
        saveButton = view.findViewById(R.id.editSaveButton);
        goBackButton = view.findViewById(R.id.editProfileGoBackButton);

        loadUserData();

        // Track changes by the user
        View.OnFocusChangeListener changeListener = (v, hasFocus) -> {
            if (!hasFocus) {
                isChanged = true;
            }
        };

        editProfileFirstName.setOnFocusChangeListener(changeListener);
        editProfileLastName.setOnFocusChangeListener(changeListener);
        editProfileEmailAddress.setOnFocusChangeListener(changeListener);
        editProfileContactNumber.setOnFocusChangeListener(changeListener);
        editEnableLocation.setOnCheckedChangeListener((buttonView, isChecked) -> isChanged = true);
        editEnableNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> isChanged = true);

        // Profile Image Gallery Picker
        ActivityResultLauncher<PickVisualMediaRequest> pickPfp =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        isChanged = true;
                        img.setImageUrl(uri.toString());
                        Glide.with(editProfilePicture.getRootView())
                                .load(uri)
                                .into(editProfilePicture);
                        Toast.makeText(
                                this.getContext(),
                                "Profile picture uploaded!",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });

        // Launches the gallery picker when the user clicks the profile picture
        editProfilePicture.setOnClickListener(v -> pickPfp.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build()));

        // Remove profile picture
        removeProfilePicture.setOnClickListener(v -> {
            isChanged = true;
            Toast.makeText(getContext(), "Profile picture deleted.", Toast.LENGTH_SHORT).show();
            editProfilePicture.setImageBitmap(null);
            editProfilePicture.setTag("deleted");
        });

        // Save the changes
        saveButton.setOnClickListener(v -> {
            if (validateUserChanges()) {
                saveButton.setEnabled(false);
                saveUserData();
            }
        });

        // Navigate back if there are unsaved changes
        goBackButton.setOnClickListener(v -> {
            if (isChanged) {
                showUnsavedChangesDialog();
            } else {
                navigateToUserProfileFragment();
            }
        });
    }

    /**
     * Loads the user data and populates the EditText fields.
     */
    private void loadUserData() {
        // Load user data from the User instance and populate the EditText fields
        User user = User.getInstance();

        if (user != null) {
            editProfileFirstName.setText(user.getFirstName());
            editProfileLastName.setText(user.getLastName());
            editProfileEmailAddress.setText(user.getEmailAddress());
            editProfileContactNumber.setText(user.getContactNumber());
            editEnableLocation.setChecked(user.isGeoLocationEnabled());
            editEnableNotifications.setChecked(user.isNotificationEnabled());

            // Initialize img only if it's null
            if (img == null) {
                saveButton.setEnabled(false);
                goBackButton.setEnabled(false);
                img = new Image(user.getUserId(), user.getUserId()); // Ensure userId is not null here
                img.download(new ImageQuery() {
                    @Override
                    public void onSuccess(Image image) {
                        img = image;
                        saveButton.setEnabled(true);
                        goBackButton.setEnabled(true);
                    }

                    @Override
                    public void onEmpty() {
                        editProfilePicture.setImageBitmap(null);
                        Toast.makeText(
                                getContext(),
                                "Error loading profile picture, please try again",
                                Toast.LENGTH_SHORT).show();
                        generateProfilePicture(task -> {
                            Image i = task.getResult();
                            user.setProfilePictureUrl(img.getImageUrl());
                            pushUserToFirebase(user);
                        });
                    }
                });
            }

            // Ensure the imageUrl is not null or empty
            String profilePictureUrl = user.getProfilePictureUrl();
            if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                Glide.with(editProfilePicture.getRootView())
                        .load(profilePictureUrl)
                        .into(editProfilePicture);
            }
        } else {
            Log.e("UserProfileEditFragment", "User instance is null.");
        }
    }

    /**
     * Validates the user input changes.
     *
     * @return true if the input is valid, false otherwise.
     */
    private boolean validateUserChanges() {
        // Get the input data
        String firstName = editProfileFirstName.getText().toString().trim();
        String lastName = editProfileLastName.getText().toString().trim();
        String emailAddress = editProfileEmailAddress.getText().toString().trim();
        String contactNumber = editProfileContactNumber.getText().toString().trim();

        if (firstName.isEmpty()) {
            Toast.makeText(getContext(), "First name is required.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!User.validateFirstName(firstName)) {
            Toast.makeText(getContext(), "Invalid First Name. Try again.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!lastName.isEmpty() && !User.validateLastName(lastName)) {
            Toast.makeText(getContext(), "Invalid Last Name. Try again.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!emailAddress.isEmpty() && !User.validateEmailAddress(emailAddress)) {
            Toast.makeText(getContext(), "Invalid Email Address. Try again.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!contactNumber.isEmpty() && !User.validateContactInformation(contactNumber)) {
            Toast.makeText(getContext(), "Invalid Contact Number. Try again.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * Saves the user data.
     */
    private void saveUserData() {
        // Save user data here
        User user = User.getInstance();
        if (user != null) {
            boolean generate = isInitialsChanged(user);
            // Update the user with the input data
            user.setFirstName(editProfileFirstName.getText().toString());
            user.setLastName(editProfileLastName.getText().toString());
            user.setEmailAddress(editProfileEmailAddress.getText().toString());
            user.setContactNumber(editProfileContactNumber.getText().toString());
            user.setGeoLocationEnabled(editEnableLocation.isChecked());
            user.setNotificationEnabled(editEnableNotifications.isChecked());

            if (img != null) {
                // Update the user's profile picture URL if changed
                if (!img.getImageUrl().equals(user.getProfilePictureUrl())) {
                    // uploads the image selected
                    Uri uri = Uri.parse(img.getImageUrl());
                    img = new Image(user.getUserId(), user.getUserId());
                    img.upload(uri, task -> {
                                if (task.isSuccessful()) {
                                    Image i = task.getResult();
                                    user.setProfilePictureUrl(i.getImageUrl());
                                    pushUserToFirebase(user);
                                }
                            });

                // Update the user's profile picture with new initials if they changed/deleted
                } else if (generate && img.isGenerated() || editProfilePicture.getTag() == "deleted") {
                    img = new Image(user.getUserId(), user.getUserId());
                    generateProfilePicture(task -> {
                        user.setProfilePictureUrl(img.getImageUrl());
                        pushUserToFirebase(user);
                    });
                } else {
                    pushUserToFirebase(user);
                }
            }
        }
    }

    /**
     * Checks whether the initials of the user have changed for profile picture generation
     * @param user The user to check the entered fields against
     * @return True if the initials have changed, false otherwise
     */
    private boolean isInitialsChanged(User user) {
        String old_in;
        String new_in;
        if (!user.getLastName().isEmpty()) {
            old_in = String.valueOf(user.getFirstName().charAt(0)) + user.getLastName().charAt(0);
        } else {
            old_in = String.valueOf(user.getFirstName().charAt(0));
        }

        if (!editProfileLastName.getText().toString().isEmpty()) {
            new_in = String.valueOf(editProfileFirstName.getText().toString().trim().charAt(0))
                    + editProfileLastName.getText().toString().trim().charAt(0);
        } else {
            new_in = String.valueOf(editProfileFirstName.getText().toString().trim().charAt(0));
        }

        return !old_in.equalsIgnoreCase(new_in);
    }

    /**
     * Updates the user in firebase
     * @param user The user to update
     */
    private void pushUserToFirebase(User user) {
        // Update the user in the repository or database
        UserRepository.getInstance().updateUser(user, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Profile saved successfully!", Toast.LENGTH_SHORT).show();
                navigateToUserProfileFragment();
            } else {
                Toast.makeText(getContext(), "Failed to save profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Helper function to generate profile pictures based on initials
     * @param listener The onCompleteListener for when the generation task is complete
     */
    private void generateProfilePicture(OnCompleteListener<Image> listener) {
        // Generate a new profile picture using the user's initials (default pfp)
        String firstName = editProfileFirstName.getText().toString().trim();
        String lastName = editProfileLastName.getText().toString().trim();
        String last_initial = !lastName.isEmpty()? String.valueOf(lastName.charAt(0)) : "";
        String initials = firstName.charAt(0) + last_initial;
        img.generate(initials, listener);
    }

    /**
     * Shows a dialog if the user tries to navigate away with unsaved changes.
     */
    private void showUnsavedChangesDialog() {
        new AlertDialog.Builder(getContext())
                .setMessage("You have unsaved changes. Do you want to discard them?")
                .setPositiveButton("Discard", (dialog, which) -> navigateToUserProfileFragment())
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Navigates back to the User Profile Fragment.
     */
    private void navigateToUserProfileFragment() {
        Navigation.findNavController(getView()).navigate(R.id.action_userProfileEditFragment_to_userProfileFragment);
    }
}


/*
   Coding Sources
   <p>
   ChatGPT
   - "How do I set up a Firebase Firestore database collection?"
   - "How do I properly setup a Map / Hashmap?"
   <p>
   Stack Overflow
   - "What is the difference between the HashMap and Map objects in Java?"
   - "Persistent Data Storage in Android Development"
   <p>
   Android Developers
   - "Data and file storage overview"
  */
