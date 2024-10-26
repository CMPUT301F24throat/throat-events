package com.example.pickme.views;

import android.os.Bundle;
import android.provider.Settings;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pickme.R;
import com.example.pickme.models.User;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        //if device exists already then set static user to be the one in db
        //otherwise create new instance in db and make it the static one

        //TEMP
        User u = new User();
        u.setUserId("temp");
        User.setInstance(u);

        //now we can retreive it from anywhere using:
        User u2 = User.getInstance();
        u2.getUserId();
    }
}