package com.example.pickme.views;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pickme.R;
import com.example.pickme.databinding.ActivityEventCreationBinding;
import com.example.pickme.models.Event;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

public class EventCreationActivity extends AppCompatActivity {
    private ActivityEventCreationBinding binding;
    private String posterUrl;
    private Uri selectedImageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityEventCreationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setCurrentDateTime();

        binding.addImage.setOnClickListener(view -> openGallery());
        binding.date.setOnClickListener(view -> pickDate());
        binding.startTime.setOnClickListener(view -> pickTime(true));
        binding.endTime.setOnClickListener(view -> pickTime(false));

        binding.create.setOnClickListener(view -> {
            if (validateInputs()) {
                if (selectedImageUri != null) {
                    uploadImageToFirebase(selectedImageUri);
                } else {
                    Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();

        // Set current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d yyyy");
        String currentDate = dateFormat.format(calendar.getTime());
        binding.date.setText(currentDate);

        // Set current time
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        String currentTime = timeFormat.format(calendar.getTime());
        binding.startTime.setText(currentTime);

        calendar.add(Calendar.HOUR_OF_DAY, 3);
        String endTime = timeFormat.format(calendar.getTime());
        binding.endTime.setText(endTime);
    }

    private boolean validateInputs() {
        return !binding.title.getText().toString().isEmpty() &&
                !binding.address.getText().toString().isEmpty() &&
                !binding.winners.getText().toString().isEmpty() &&
                !binding.entrants.getText().toString().isEmpty() &&
                !binding.description.getText().toString().isEmpty();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    // Result handler for gallery selection
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    binding.camera.setImageURI(selectedImageUri); // Display selected image
                }
            }
    );

    // Upload image to Firebase Storage and get the download URL
    private void uploadImageToFirebase(Uri imageUri) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("event_posters/" + UUID.randomUUID().toString());
        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            posterUrl = uri.toString(); // Save download URL
                            createEventInFirestore(); // Call method to create event after getting the URL
                        }))
                .addOnFailureListener(e -> {
                    Log.e("FirebaseUpload", "Upload failed", e);
                    Toast.makeText(this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void createEventInFirestore() {
        String eventTitle = binding.title.getText().toString();
        String eventDescription = binding.description.getText().toString();
        String promoQrCodeId = generateRandomQrCodeId(10);
        String waitingListQrCodeId = generateRandomQrCodeId(10);
        String date = binding.date.getText().toString() + ", " + binding.startTime.getText().toString() + " - " + binding.endTime.getText().toString();

        Event event = new Event(
                Timestamp.now(),
                "123456789",
                "1234567890",
                "1234567890123",
                eventTitle,
                eventDescription,
                date,
                promoQrCodeId,
                waitingListQrCodeId,
                posterUrl,
                binding.address.getText().toString(),
                binding.winners.getText().toString(),
                true,
                Integer.parseInt(binding.entrants.getText().toString()),
                Timestamp.now()
        );

        pushEventToFirestore(event);
    }

    private void pushEventToFirestore(Event event) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events")
                .add(event)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Event Created Successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Finish the activity
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to create event", Toast.LENGTH_SHORT).show()
                );
    }

    private String generateRandomQrCodeId(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder id = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            id.append(chars.charAt(random.nextInt(chars.length())));
        }
        return id.toString();
    }

    private void pickDate() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);

            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d yyyy");
            String formattedDate = dateFormat.format(calendar.getTime());

            binding.date.setText(formattedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePicker.show();
    }

    private void pickTime(boolean b){
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePicker = new TimePickerDialog(this, (timeView, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
            String formattedTime = timeFormat.format(calendar.getTime());

            if (b) {
                binding.startTime.setText(formattedTime);
            } else {
                binding.endTime.setText(formattedTime);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

        timePicker.show();
    }
}