package com.example.pickme.views;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.GridView;
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
import com.example.pickme.models.Image;
import com.example.pickme.models.User;
import com.example.pickme.repositories.ImageRepository;
import com.example.pickme.repositories.UserRepository;
import com.example.pickme.utils.ImageQuery;
import com.google.firebase.auth.FirebaseAuth;

public class Example_ImageUpload extends AppCompatActivity {

    //region Attributes

    String TAG = "Example_ImageUpload";
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    UserRepository ur = new UserRepository();
    User user1 = new User(ur, auth.getUid());
    Event event1 = new Event();
    //endregion

    //region Activity result contracts

    // these are the gallery picker promises/contracts so we can grab the image uri

    ActivityResultLauncher<PickVisualMediaRequest> pickPfp =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // callback is invoked after the user selects a media item or closes the photo picker
                if (uri != null) {

                    Image image = new Image(user1.getUserId(), user1.getUserId());
                    image.upload(uri);

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

                    Image image = new Image(user1.getUserId(), event1.getEventId());
                    image.upload(uri);

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
            // clears the imageview
            imageView.setImageResource(0);

            // download method requires a callback to access asynchronous query data
            Image image = new Image(user1.getUserId(), user1.getUserId());
            image.download(new ImageQuery() {
                @Override
                public void onSuccess(Image image) {
                    Glide.with(view)
                            .load(image.getImageUrl())
                            .into(imageView);
                }

                @Override
                public void onEmpty() {
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

            Image image = new Image(user1.getUserId(), event1.getEventId());
            image.download(new ImageQuery() {
                @Override
                public void onSuccess(Image image) {
                    Glide.with(view)
                            .load(image.getImageUrl())
                            .into(imageView);
                }

                @Override
                public void onEmpty() {
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
            Image image = new Image(user1.getUserId(), user1.getUserId());
            image.delete();
        });

        Button deleteEPButton = findViewById(R.id.deleteEPButton);
        deleteEPButton.setOnClickListener(view -> {
            Image image = new Image(user1.getUserId(), event1.getEventId());
            image.delete();
        });

        // get all in gallery
        Button getAllButton = findViewById(R.id.getAllButton);
        GridView gridView = findViewById(R.id.imageGridView);


        getAllButton.setOnClickListener(view -> {
            ImageRepository ir = new ImageRepository();
            ir.getAllImages(this ,gridView);
        });

    }
}