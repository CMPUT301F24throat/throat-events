package com.example.pickme.views;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.pickme.R;
import com.example.pickme.models.User;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * The main activity for our app.
 *
 * @author Kenneth (aesoji)
 * @version 1.0
 *
 * Responsibilities:
 * - Centre for most app features and acts as the main crossroads.
 * - Supports current event and notifications.
 * - Main access to profile information.
 **/
public class HomeActivity extends AppCompatActivity {

    private ImageButton homeProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        // Variable Initialization
        CircleImageView homeProfileButton = findViewById(R.id.homeProfileButton);

        // Retrieves the User documentation from MainActivity.
        User user = (User) getIntent().getSerializableExtra("user_data");
        Glide.with(this)
                .load(User.getInstance().getProfilePictureUrl())
                .into(homeProfileButton);

        homeProfileButton.setVisibility(View.VISIBLE);

        // Listens for if the User clicks their Profile.
        homeProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, UserProfileActivity.class);
            intent.putExtra("user_data", user);
            startActivity(intent);
        });

        askNotificationPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Make the button visible again when returning to the activity
        if (homeProfileButton != null) {
            homeProfileButton.setVisibility(View.VISIBLE);
        }
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    // TODO: Inform user that that your app will not show notifications.
                }
            });

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            }
            else if(shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                new AlertDialog.Builder(HomeActivity.this)
                        .setMessage("Please enable notifications to receive updates on events")
                        .setTitle("Allow Notifications")
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialog, which) -> {
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                        })
                        .setNegativeButton("No Thanks", (dialog, which) -> {
                            //NOTE: idk if i need to do anything
                        });
            }
            else{
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
}

