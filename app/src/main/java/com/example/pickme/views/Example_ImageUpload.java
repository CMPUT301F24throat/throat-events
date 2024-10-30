package com.example.pickme.views;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.pickme.R;
import com.example.pickme.models.Event;
import com.example.pickme.models.User;
import com.example.pickme.repositories.ImageRepository;
import com.google.firebase.firestore.FirebaseFirestore;

public class Example_ImageUpload extends AppCompatActivity {

    //region Attributes

    String TAG = "Example_ImageUpload";
    private FirebaseFirestore db;

    User user1 = new User();
    Event event1 = new Event();
    //endregion

    //region Activity result contracts

    // these are the gallery picker promises/contracts so we can grab the image uri

    ActivityResultLauncher<PickVisualMediaRequest> pickPfp =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // callback is invoked after the user selects a media item or closes the photo picker
                if (uri != null) {
                    ImageRepository ir = new ImageRepository();
                    ir.download(user1, new ImageRepository.queryCallback() {
                        @Override
                        public void onQuerySuccess(String imageUrl) {
                            // if an image of this type already exists, remove it
                            ir.delete(user1);
                        }

                        @Override
                        public void onQueryEmpty() {
                        }
                    });
                    ir.upload(user1, uri);
                    Toast.makeText(
                            Example_ImageUpload.this,
                            "Profile picture uploaded for user: " + user1.getUserId(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(
                            Example_ImageUpload.this,
                            "No media selected",
                            Toast.LENGTH_SHORT).show();
                }
            });

    ActivityResultLauncher<PickVisualMediaRequest> pickEP =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // callback is invoked after the user selects a media item or closes the photo picker
                if (uri != null) {
                    ImageRepository ir = new ImageRepository();
                    ir.download(user1, event1, new ImageRepository.queryCallback() {
                        @Override
                        public void onQuerySuccess(String imageUrl) {
                            ir.delete(user1, event1);
                        }

                        @Override
                        public void onQueryEmpty() {
                        }
                    });
                    ir.upload(user1, event1, uri);
                    Toast.makeText(
                            Example_ImageUpload.this,
                            "Event poster uploaded for event: " + event1.getEventId(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("PhotoPicker", "No media selected");
                    Toast.makeText(
                            Example_ImageUpload.this,
                            "No media selected",
                            Toast.LENGTH_SHORT).show();
                }
            });

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.imageupload_example);

        user1.setUserId("1234_user_test");
        event1.setEventId("1234_event_test");

        // uploads
        Button uploadPfpButton = findViewById(R.id.uploadPfpButton) ;
        uploadPfpButton.setOnClickListener(view -> {
            // launch the picker to complete the promise (limited to single image)
            pickPfp.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        Button uploadEPButton = findViewById(R.id.uploadEPButton) ;
        uploadEPButton.setOnClickListener(view -> {
            pickEP.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        // downloads
        ImageView imageView = findViewById(R.id.imageView);

        Button downloadPfpButton = findViewById(R.id.downloadPfpButton);
        downloadPfpButton.setOnClickListener(view -> {
            ImageRepository ir = new ImageRepository();

            // clears the imageview
            imageView.setImageResource(0);

            // download method requires a callback to access asynchronous query data
            ir.download(user1, new ImageRepository.queryCallback() {
                @Override
                public void onQuerySuccess(String imageUrl) {
                    // using glide to show image on imageView; subject to change
                    Glide.with(view)
                            .load(imageUrl)
                            .into(imageView);
                }

                @Override
                public void onQueryEmpty() {
                    // show a toast when the query is empty
                    Toast.makeText(
                            Example_ImageUpload.this,
                            "Cannot find profile picture for user: " + user1.getUserId(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        });

        Button downloadEPButton = findViewById(R.id.downloadEPButton);
        downloadEPButton.setOnClickListener(view -> {
            ImageRepository ir = new ImageRepository();

            // clears the imageview
            imageView.setImageResource(0);

            ir.download(user1, event1, new ImageRepository.queryCallback() {
                @Override
                public void onQuerySuccess(String imageUrl) {
                    Glide.with(view)
                            .load(imageUrl)
                            .into(imageView);
                }

                @Override
                public void onQueryEmpty() {
                    Toast.makeText(
                            Example_ImageUpload.this,
                            "Cannot find event poster for event: " + event1.getEventId(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        });

        // deletes
        Button deletePfpButton = findViewById(R.id.deletePfpButton);
        deletePfpButton.setOnClickListener(view -> {
            ImageRepository ir = new ImageRepository();
            // deletion also checks for empty query but just logs to Logcat instead of toasting
            ir.delete(user1);
        });

        Button deleteEPButton = findViewById(R.id.deleteEPButton);
        deleteEPButton.setOnClickListener(view -> {
            ImageRepository ir = new ImageRepository();
            ir.delete(user1, event1);
        });


    }
}