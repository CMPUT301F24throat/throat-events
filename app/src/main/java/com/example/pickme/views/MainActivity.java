package com.example.pickme.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pickme.R;
import com.example.pickme.controllers.CreateNotificationController;
import com.example.pickme.repositories.UserRepository;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
        db = FirebaseFirestore.getInstance();

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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button switchButton = findViewById(R.id.switchButton);
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateNotificationController.class);
                startActivity(intent);
            }
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