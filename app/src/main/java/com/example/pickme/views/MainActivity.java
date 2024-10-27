package com.example.pickme.activities;

import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pickme.R;
import com.example.pickme.repositories.UserRepository;
import com.example.pickme.views.SignUpFragment;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;

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
                DocumentSnapshot document = task.getResult();
                //loadFragment(new EventViewModel());
            } else {
                // Handle the error or case where the user does not exist
                loadFragment(new SignUpFragment());
            }
        });
    }

    private void loadFragment(SignUpFragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.user_signup_activity, fragment) // Ensure this is the correct container ID
                .addToBackStack(null)
                .commit();
    }

}
