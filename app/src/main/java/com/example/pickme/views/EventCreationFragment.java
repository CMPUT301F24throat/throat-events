package com.example.pickme.views;

import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.pickme.controllers.EventViewModel;
import com.example.pickme.databinding.EventEventcreationBinding;
import com.example.pickme.models.Event;
import com.example.pickme.models.Image;
import com.example.pickme.utils.ImageQuery;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class EventCreationFragment extends Fragment {
    private EventEventcreationBinding binding;
    private String posterUrl;
    private Uri selectedImageUri;
    private EventViewModel eventViewModel = new EventViewModel();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = EventEventcreationBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCurrentDateTime();

        binding.addImage.setOnClickListener(listener -> openGallery());
        binding.date.setOnClickListener(listener -> pickDate());
        binding.startTime.setOnClickListener(listener -> pickTime(true));
        binding.endTime.setOnClickListener(listener -> pickTime(false));
        binding.back.setOnClickListener(listener -> Navigation.findNavController(requireView()).navigateUp());

        binding.create.setOnClickListener(listener -> {
            if (validateInputs()) {
                if (selectedImageUri != null) {
                    uploadImageToFirebase(selectedImageUri);
                } else {
                    Toast.makeText(requireActivity(), "Please select an image", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
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
        Image image = new Image("1234567890", "123456789");
        image.upload(imageUri);
        image.download(new ImageQuery() {
            @Override
            public void onSuccess(Image image) {
                posterUrl = image.getImageUrl();
            }

            @Override
            public void onEmpty() {
            }
        });
        createEventInFirestore();
    }

    private void createEventInFirestore() {
        String eventTitle = binding.title.getText().toString();
        String eventDescription = binding.description.getText().toString();
        String promoQrCodeId = generateRandomQrCodeId(10);
        String waitingListQrCodeId = generateRandomQrCodeId(10);
        String date = binding.date.getText().toString() + ", " + binding.startTime.getText().toString() + " - " + binding.endTime.getText().toString();

        Event event = new Event(
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
                System.currentTimeMillis(),
                System.currentTimeMillis()
        );

        pushEventToFirestore(event);
    }

    private void pushEventToFirestore(Event event) {
        eventViewModel.addEvent(event, new OnCompleteListener<Object>() {
            @Override
            public void onComplete(Task<Object> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(requireActivity(), "Event Created Successfully!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigateUp();
                } else {
                    Toast.makeText(requireActivity(), "Failed to create event", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

        DatePickerDialog datePicker = new DatePickerDialog(requireActivity(), (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);

            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d yyyy");
            String formattedDate = dateFormat.format(calendar.getTime());

            binding.date.setText(formattedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePicker.show();
    }

    private void pickTime(boolean b){
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePicker = new TimePickerDialog(requireActivity(), (timeView, hourOfDay, minute) -> {
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