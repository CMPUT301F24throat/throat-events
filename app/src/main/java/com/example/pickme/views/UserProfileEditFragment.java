package com.example.pickme.views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.pickme.R;
import com.example.pickme.models.Image;
import com.example.pickme.models.User;
import com.example.pickme.repositories.UserRepository;
import com.google.android.gms.tasks.OnCompleteListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileEditFragment extends Fragment {

    private EditText editProfileFirstName, editProfileLastName, editProfileEmailAddress, editProfileContactNumber;
    private SwitchCompat editEnableAdminView;
    private CircleImageView editProfilePicture;
    private ImageButton removeProfilePicture;
    private Image img;
    private boolean isChanged = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_profile__edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialization of Views
        editProfileFirstName = view.findViewById(R.id.editProfileFirstName);
        editProfileLastName = view.findViewById(R.id.editProfileLastName);
        editProfileEmailAddress = view.findViewById(R.id.editProfileEmailAddress);
        editProfileContactNumber = view.findViewById(R.id.editProfileContactNumber);
        editEnableAdminView = view.findViewById(R.id.editEnableAdminView);
        editProfilePicture = view.findViewById(R.id.editProfilePicture);
        removeProfilePicture = view.findViewById(R.id.removeProfilePicture);
        Button saveButton = view.findViewById(R.id.editSaveButton);
        Button goBackButton = view.findViewById(R.id.editProfileGoBackButton);

        loadUserData();

        // Track changes by user
        View.OnFocusChangeListener changeListener = (v, hasFocus) -> {
            if (!hasFocus) {
                isChanged = true;
            }
        };

        editProfileFirstName.setOnFocusChangeListener(changeListener);
        editProfileLastName.setOnFocusChangeListener(changeListener);
        editProfileEmailAddress.setOnFocusChangeListener(changeListener);
        editProfileContactNumber.setOnFocusChangeListener(changeListener);
        editEnableAdminView.setOnCheckedChangeListener((buttonView, isChecked) -> isChanged = true);

        // profile image gallery picker
        ActivityResultLauncher<PickVisualMediaRequest> pickPfp =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        isChanged = true;
                        // Handle the selected image URI
                    }
                });

        // launches the gallery picker when the user clicks the profile picture
        editProfilePicture.setOnClickListener(v -> {
            pickPfp.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        removeProfilePicture.setOnClickListener(v -> {
            isChanged = true;
            Toast.makeText(getContext(), "Profile picture deleted.", Toast.LENGTH_SHORT).show();
            editProfilePicture.setImageBitmap(null);
            editProfilePicture.setTag("deleted");
        });

        saveButton.setOnClickListener(v -> {
            if (validateUserChanges()) {
                saveUserData();
            }
        });

        goBackButton.setOnClickListener(v -> {
            if (isChanged) {
                showUnsavedChangesDialog();
            } else {
                navigateToUserProfileFragment();
            }
        });
    }


    private void loadUserData() {
        //Load user data from the User instance and populate the EditText fields with the user's data.
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
            Toast.makeText(getContext(), "User data not available.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateUserChanges() {
        //Validate user changes before saving.
        String firstName = editProfileFirstName.getText().toString().trim();

        if (firstName.isEmpty()) {
            Toast.makeText(getContext(), "First Name required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveUserData() {
        //Save user data to Firestore.
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
                navigateToUserProfileFragment();
            });

        } else {
            updateUserInstanceWithInput(user);
            pushUserToFirestore(user);
            navigateToUserProfileFragment();
        }
    }

    private void pushUserToFirestore(User user) {
        UserRepository userRepository = UserRepository.getInstance();
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
        user.setFirstName(editProfileFirstName.getText().toString().trim());
        user.setLastName(editProfileLastName.getText().toString().trim());
        user.setEmailAddress(editProfileEmailAddress.getText().toString().trim());
        user.setContactNumber(editProfileContactNumber.getText().toString().trim());
        user.setProfilePictureUrl(img.getImageUrl());
    }

    private void generateProfilePicture(User user, OnCompleteListener<Image> listener) {
        // Generate a new profile picture using the user's initials (default pfp)
        String firstName = editProfileFirstName.getText().toString().trim();
        String lastName = editProfileLastName.getText().toString().trim();
        String initials = String.valueOf(firstName.charAt(0)) + lastName.charAt(0);

        img.generate(initials, listener);
    }

    private void navigateToUserProfileFragment() {
        Navigation.findNavController(getView()).navigate(R.id.action_userProfileEditFragment_to_userProfileFragment);
    }

    private void showUnsavedChangesDialog() {
        // Show a dialog to confirm if the user wants to continue without saving changes.
        new AlertDialog.Builder(requireContext())
                .setMessage("Unsaved Changes - Continue without saving?")
                .setPositiveButton("Yes", (dialog, which) -> navigateToUserProfileFragment())
                .setNegativeButton("No", null)
                .show();
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}

 /*
   Coding Sources
   <p>
   ChatGBT
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
