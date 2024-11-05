package com.example.pickme.views;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pickme.R;
import com.example.pickme.models.User;
import com.example.pickme.repositories.UserRepository;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * An entry point for the app and is responsible for user authentication.
 *
 * @author Kenneth (aesoji)
 * @version 1.2
 *
 * Responsibilities:
 * - Checks for user existence in our Firebase Firestore based on a unique device ID.
 * - Navigates the user to HomeActivity for existing users or UserSignUpActivity for new users.
 **/
public class MainActivity extends AppCompatActivity {

    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EdgeToEdge.enable(this);

        FirebaseApp.initializeApp(this);
        userRepository = new UserRepository();

        // Hides the action bar
        androidx.appcompat.app.ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.hide();
        }

        // Checks user in the Firestore
        String deviceID = getDocumentationDeviceId();
        if (deviceID != null && !deviceID.isEmpty()) {
            checkUserInFirestore(deviceID);
        }
    }

    public String getDocumentationDeviceId() {
        // Retrieves the user's unique DeviceId.
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

            // User proven to exist, load their information.
            loadUserInformation(user, document);
        });
    }

    //---------- Activity Redirection --------------------
    private void loadUserInformation(User user, DocumentSnapshot document) {
        // Navigates to HomeActivity for existing users via deserialization.
        User.setInstance(user);
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        intent.putExtra("user_data", user);
        startActivity(intent);
        finish();
    }

    private void loadUserSignIn() {
        // Navigates to the SignInActivity for new users or first-time login.
        Intent intent = new Intent(MainActivity.this, UserSignUpActivity.class);
        startActivity(intent);
        finish();
    }

    //---------- Error Cases --------------------
    private void handleFirestoreError(Exception exception) {
        // Handles a Firestore accessing errors.
        Log.e("Firestore", "Error accessing Firestore: ", exception);
    }

    private void handleUserNotFound() {
        // Handles the case when a user document does not exist in Firestore.
        Log.d("Firestore", "Device-specific user not found, redirecting to SignInActivity.");
        loadUserSignIn();
    }

    private void handleDeserializationError() {
        // Handles errors related to deserialization of the user object.
        Log.e("Firestore", "User object is null after deserialization.");
    }

    private boolean isUserDocumentValid(DocumentSnapshot document) {
        // Checks if the Firestore document is valid.
        return document != null && document.exists();
    }

    private boolean isUserDeserialized(User user) {
        // Checks if the document can be successfully deserialized from Firestore.
        return user != null;
    }
}

/**
 * Coding Sources
 * <p>
 * ChatGBT
 * - "It's more intuitive to compare DeviceId's since it's one device per person. How do we set our Firebase and code to align?"
 * - "How do we load Firebase information if we already have an account created?"
 * - "What are some Firebase Firestore errors regarding documenting DeviceIds that I should be aware of?"
 * - "How does lambda insertion tasks work?"
 * <p>
 * Stack Overflow
 * - "How do i hid the action bar Android Studio?"
 * - "Android: Difference between Parcelable and Serializable?"
 * <p>
 * Firebase Documentation
 * - Authentication > Admin > Manage Users
 * <p>
 * Android Developers
 * - "Display content edge-to-edge in your app"
 **/
