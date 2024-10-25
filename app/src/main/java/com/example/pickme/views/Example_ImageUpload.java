package com.example.pickme.views;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pickme.R;
import com.example.pickme.repositories.ImageRepository;
import com.google.firebase.firestore.FirebaseFirestore;

public class Example_ImageUpload extends AppCompatActivity {

    private FirebaseFirestore db;

    // this is the gallery picker promise/contract so we can grab the image uri
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // callback is invoked after the user selects a media item or closes the photo picker
                if (uri != null) {
                    ImageRepository ir = new ImageRepository();
                    ir.upload(uri);
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.imageupload_example);

        Button uploadButton = findViewById(R.id.button) ;
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // launch the picker to complete the promise (limited to single image)
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });
    }
}