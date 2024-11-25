package com.example.pickme.views;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.pickme.R;
import com.example.pickme.models.User;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * HomeFragment class that represents the home screen of the application.
 * It handles the display of the user's profile picture and requests notification permissions.
 */
public class HomeFragment extends Fragment {

    private ImageButton homeProfileButton;

    /**
     * Called to have the fragment instantiate its user interface view.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    /**
     * Called immediately after onCreateView has returned, but before any saved state has been restored in to the view.
     * @param view The View returned by onCreateView.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CircleImageView homeProfileButton = view.findViewById(R.id.homeProfileButton);

        User user = User.getInstance();
        if (user != null) {
            String profilePictureUrl = user.getProfilePictureUrl();
            Glide.with(this)
                    .load(user.getProfilePictureUrl())
                    .into(homeProfileButton);

            homeProfileButton.setVisibility(View.VISIBLE);

            homeProfileButton.setOnClickListener(v -> {
                Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_userProfileFragment);
            });
        } else {
            // Handle the case where the user is null
            homeProfileButton.setVisibility(View.GONE);
        }

        askNotificationPermission();
    }

    /**
     * ActivityResultLauncher to handle the result of the notification permission request.
     */
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    // Inform user that your app will not show notifications.
                }
            });

    /**
     * Requests notification permission from the user.
     * If the permission is not granted, it shows a dialog explaining why the permission is needed.
     */
    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                new AlertDialog.Builder(requireContext())
                        .setMessage("Please enable notifications to receive updates on events")
                        .setTitle("Allow Notifications")
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialog, which) -> {
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                        })
                        .setNegativeButton("No Thanks", (dialog, which) -> {
                            // No action needed
                        }).show();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
}