package com.example.pickme.views;

import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pickme.R;
import com.example.pickme.repositories.UserRepository;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * MainActivity serves as the entry point of the application and is responsible for checking if the user exists in the Firebase database.
 *
 * @author Kenneth (aesoji)
 * @version 1.0
 *
 * Responsibilities:
 * - Initializes Firebase services and user repository.
 * - Checks for user existence in the database.
 * - Directs users to the SignUpFragment if they are NOT found.
 * - Directs users to the [HomeActivity] if they are found.
 * - Manages window insets to ensure proper UI layout.
 */

public class MainActivity extends AppCompatActivity {

    private UserRepository userRepository;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialization of the Firebase.
        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();

        // Initialization of the UserRepository.
        userRepository = new UserRepository();

        // Allows loading splash art to appear cleanly.
        androidx.appcompat.app.ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.hide();
        }

        // Perform anonymous sign-in and user existence check after a delay.
        Handler handler = new Handler();
        handler.postDelayed(() -> userSignIn(), 4000);

        // Window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void userSignIn() {
        /*
        * FYI: [TEXT] means placeholder variable information.
        * This method attempts to sign the user in via auth.signInAnonymously().
        * If the sign-in is successful, then it will retrieve the user's uniqueID.
        * It takes the uniqueID and checks our Firebase via the userVerification() function.
        * If both checks pass, then proceed to our [Home Activity].
        * Otherwise, direct the user to the SignUpFragment.
         */
        auth.signInAnonymously().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = auth.getCurrentUser();
                if (firebaseUser != null) {
                    String userId = firebaseUser.getUid();
                    userVerification(userId);
                }
            } else {
                loadSignUpFragment(new UserSignUpFragment());
            }
        });
    }

    private void userVerification(String userId) {
        /* Called from userSignIn, it checks if the authenticated user exists in our Firestore database.
        * If this verification fails, then it redirects the user to the SignUpFragment.
        */
        userRepository.getUserById(userId, task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                // [Incomplete Method]: Load the main content fragment or proceed to main app functionality
                System.out.println("Whoopsies, did an oopsies");
            } else {
                loadSignUpFragment(new UserSignUpFragment());
            }
        });
    }

    private void loadSignUpFragment(UserSignUpFragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.user_signup_activity, fragment)
                .addToBackStack(null)
                .commit();
    }

    private <HomeFragment> void loadHomeFragment(HomeFragment fragment) {
        // [Incomplete Method]: Similar to loadSignUpFragment but will direct user to our Home Page.
    }

}
