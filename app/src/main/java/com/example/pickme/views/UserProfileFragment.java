package com.example.pickme.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import com.example.pickme.R;
import com.example.pickme.models.User;

public class UserProfileFragment extends Fragment {

    private TextView profileFullName;
    private TextView profileEmailAddress;
    private TextView profileContactNumber;
    private SwitchCompat profileLocationSwitch;
    private SwitchCompat profileNotificationOrganizerSwitch;
    private SwitchCompat profileNotificationAdminSwitch;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_profile_main, container, false);

        // Initializations of Views and Data
        profileFullName = view.findViewById(R.id.profileFullName);
        profileEmailAddress = view.findViewById(R.id.profileEmailAddress);
        profileContactNumber = view.findViewById(R.id.profileContactNumber);
        profileLocationSwitch = view.findViewById(R.id.profileLocationSwitch);
        profileNotificationOrganizerSwitch = view.findViewById(R.id.profileNotificationOrganizerSwitch);
        profileNotificationAdminSwitch = view.findViewById(R.id.profileNotificationAdminSwitch);
        Button editButton = view.findViewById(R.id.profileEditButton);
        Button editGoBackButton = view.findViewById(R.id.profileMainGoBackButton);

        editButton.setOnClickListener(v -> navigateToEditFragment()); // Listens for the Edit Profile Button.
        editGoBackButton.setOnClickListener(v -> navigateToMain()); // Listens for the Go Back Button.

        loadUserData(); // Loads the user data

        return view;
    }

    private void loadUserData() {
        User user = User.getInstance();

        if (user != null) {
            // Fill in user information
            profileFullName.setText(user.fullName(user.getFirstName(), user.getLastName()));
            profileEmailAddress.setText(user.getEmailAddress());
            profileContactNumber.setText(user.getContactNumber());
            profileLocationSwitch.setChecked(user.isGeoLocationEnabled());
            profileNotificationOrganizerSwitch.setChecked(user.isNotificationEnabled());
            profileNotificationAdminSwitch.setChecked(user.isAdmin());
        } else {
            getFirestoreUserData();
        }
    }

    private void getFirestoreUserData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(getContext(), "User is not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            User user = documentSnapshot.toObject(User.class);
                            if (user != null) {
                                User.setInstance(user); // Update the singleton instance
                                loadUserData(); // Reload user data
                            } else {
                                Toast.makeText(getContext(), "Failed to convert user data.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "User data not found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to fetch user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToEditFragment() {
        UserProfileEditFragment userProfileEditFragment = new UserProfileEditFragment();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.user_profile_edit, userProfileEditFragment) // Use your actual container ID
                .addToBackStack(null)
                .commit();
    }

    private void navigateToMain() {
        requireActivity().onBackPressed();
    }
}
