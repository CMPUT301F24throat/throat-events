package com.example.pickme.views;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pickme.R;
import com.example.pickme.controllers.CreateNotificationController;
import com.example.pickme.models.User;
import com.example.pickme.repositories.UserRepository;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private UserRepository userRepository;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    // TODO: Inform user that that your app will not show notifications.
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

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

        askNotificationPermission();

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //------------- Omar's testing -----------------------

        User user = new User();
        user.setFirstName("Omar");
        new UserRepository().addUser(user, (task) -> {
            Log.i("ADDED", "User added");
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
//                loadFragment(new SignUpFragment());
            }
        });
    }

    private void loadFragment(SignUpFragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.user_signup_activity, fragment) // Ensure this is the correct container ID
                .addToBackStack(null)
                .commit();
    }

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            }
            else if(shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                new AlertDialog.Builder(MainActivity.this)
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