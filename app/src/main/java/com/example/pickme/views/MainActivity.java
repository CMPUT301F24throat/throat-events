package com.example.pickme.views;

import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.pickme.R;
import com.example.pickme.models.User;
import com.example.pickme.repositories.UserRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private UserRepository userRepository;
    private BottomNavigationView bottomNavigationView;
    private View loadingScreen; // Reference to the loading screen view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        userRepository = new UserRepository();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        loadingScreen = findViewById(R.id.loading_screen); // Find the loading screen view

        bottomNavigationView.setVisibility(View.GONE); // Initially hide the bottom navigation

        String deviceID = getDocumentationDeviceId();
        if (deviceID != null && !deviceID.isEmpty()) {
            showLoadingScreen(); // Show loading screen while checking Firestore
            checkUserInFirestore(deviceID);
        }

        // Set up NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // Set up bottom navigation item selection listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                navController.navigate(R.id.action_global_homeFragment);
                return true;
            } else if (itemId == R.id.navigation_qr_camera) {
                navController.navigate(R.id.action_global_qrCameraFragment);
                return true;
            } else if (itemId == R.id.navigation_inbox) {
                navController.navigate(R.id.action_global_inboxFragment);
                return true;
            } else if (itemId == R.id.navigation_my_events) {
                navController.navigate(R.id.action_global_myEventsFragment);
                return true;
            }
            return false;
        });
    }

    // Get the device ID
    public String getDocumentationDeviceId() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    // Check if the user exists in Firestore
    private void checkUserInFirestore(String deviceID) {
        userRepository.getUserByDeviceId(deviceID, task -> {
            hideLoadingScreen(); // Hide the loading screen after Firestore operation
            if (!task.isSuccessful() || task.getResult() == null) {
                handleFirestoreError(task.getException());
                return;
            }

            DocumentSnapshot document = task.getResult();
            if (!isUserDocumentValid(document)) {
                handleUserNotFound();
                return;
            }

            User user = document.toObject(User.class);
            if (!isUserDeserialized(user)) {
                handleDeserializationError();
                return;
            }

            User.setInstance(user);
            // Show the Admin Tools menu item if the user is an admin
            if (user.isAdmin()) {
                bottomNavigationView.getMenu().findItem(R.id.navigation_admin_tools).setVisible(true);
            }
            navigateToHomeFragment();
        });
    }

    // Show the loading screen
    private void showLoadingScreen() {
        if (loadingScreen != null) {
            loadingScreen.setVisibility(View.VISIBLE);
        }
    }

    // Hide the loading screen with a delay
    private void hideLoadingScreen() {
        if (loadingScreen != null) {
            // Delay for 2 seconds before hiding the loading screen
            new Handler().postDelayed(() -> loadingScreen.setVisibility(View.GONE), 2000);
        }
    }

    // Navigate to the HomeFragment and show the bottom navigation
    private void navigateToHomeFragment() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.navigate(R.id.action_global_homeFragment);
        bottomNavigationView.setVisibility(View.VISIBLE); // Show the bottom navigation
    }

    // Handle Firestore access errors
    private void handleFirestoreError(Exception exception) {
        Log.e("Firestore", "Error accessing Firestore: ", exception);
    }

    // Handle the case where the user is not found in Firestore
    private void handleUserNotFound() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.navigate(R.id.action_global_userSignUpFragment);
    }

    // Handle deserialization errors
    private void handleDeserializationError() {
        Log.e("Firestore", "User object is null after deserialization.");
    }

    // Check if the user document is valid
    private boolean isUserDocumentValid(DocumentSnapshot document) {
        return document != null && document.exists();
    }

    // Check if the user object is deserialized correctly
    private boolean isUserDeserialized(User user) {
        return user != null;
    }
}
