package com.example.pickme.views;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.pickme.R;
import com.example.pickme.models.User;
import com.example.pickme.repositories.UserRepository;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        userRepository = new UserRepository();

        String deviceID = getDocumentationDeviceId();
        if (deviceID != null && !deviceID.isEmpty()) {
            checkUserInFirestore(deviceID);
        }
    }

    public String getDocumentationDeviceId() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private void checkUserInFirestore(String deviceID) {
        userRepository.getUserByDeviceId(deviceID, task -> {
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
            navigateToHomeFragment();
        });
    }

    private void navigateToHomeFragment() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.navigate(R.id.action_global_homeFragment);
    }

    private void handleFirestoreError(Exception exception) {
        Log.e("Firestore", "Error accessing Firestore: ", exception);
    }

    private void handleUserNotFound() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.navigate(R.id.action_global_userSignUpFragment);
    }

    private void handleDeserializationError() {
        Log.e("Firestore", "User object is null after deserialization.");
    }

    private boolean isUserDocumentValid(DocumentSnapshot document) {
        return document != null && document.exists();
    }

    private boolean isUserDeserialized(User user) {
        return user != null;
    }
}