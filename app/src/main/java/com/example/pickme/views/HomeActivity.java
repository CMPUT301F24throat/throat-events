package com.example.pickme.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pickme.R;
import com.example.pickme.models.User;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        // Variable Initialization
        ImageButton homeProfileButton = findViewById(R.id.homeProfileButton);

        // Retrieves the User documentation from MainActivity.
        User user = (User) getIntent().getSerializableExtra("user_data");

        // Listens for if the User clicks their Profile.
        homeProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, UserProfileActivity.class);
            intent.putExtra("user_data", user);
            startActivity(intent);
        });
    }
}

