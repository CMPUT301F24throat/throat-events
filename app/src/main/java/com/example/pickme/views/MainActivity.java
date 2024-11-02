package com.example.pickme.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.pickme.R;
import com.example.pickme.repositories.UserRepository;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Entry point of the app, responsible for authentication and navigation
 * Responsibilities:
 * Handle user authentication
 * Navigate between fragments and manage user sessions
 **/

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Initialize UserRepository instance
        userRepository = new UserRepository();

        // Hide the action bar
        androidx.appcompat.app.ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.hide();
        }

        // Set a delay to check user existence
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentUserId = "someUserId"; // Replace with the actual logic to get current user ID
                checkUserExists(currentUserId);
            }
        }, 4000);

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void checkUserExists(String userId) {
        userRepository.getUserById(userId, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                // User exists, start HomeActivity
                Intent intent = new Intent(MainActivity.this, HomeFragment.class);
                startActivity(intent);
                finish(); // Optional: close MainActivity so the user can't return with the back button
            } else {
                // User does not exist, load SignUpFragment
                loadFragment(new SignUpFragment());
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.user_signup_activity, fragment) // Ensure this ID matches your layout
                .addToBackStack(null)
                .commit();
    }
}
