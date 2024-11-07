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

import com.bumptech.glide.Glide;
import com.example.pickme.R;
import com.example.pickme.controllers.EventViewModel;
import com.example.pickme.databinding.EventEventcreationBinding;
import com.example.pickme.models.Event;
import com.example.pickme.models.Image;
import com.example.pickme.repositories.QrRepository;
import com.example.pickme.utils.ImageQuery;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

/**
 * Fragment for creating and managing events in the system.
 * Provides a UI for organizers to input event details, such as title, description, date, location, and poster image.
 * Handles validation, image selection, Firestore data storage, and QR code generation for events.
 *
 * @version 2.0
 * @author Ayub Ali
 * Responsibilities:
 * - Capture and validate user input for event details.
 * - Manage event creation, updating, and deletion in Firestore.
 * - Handle image selection and upload for event posters.
 * - Generate QR codes for promotional and waiting list purposes.
 * - Navigate to appropriate screens based on user actions.
 *
 */

public class EventCreationFragment extends Fragment {
    private EventEventcreationBinding binding;
    private String posterUrl;
    private Uri selectedImageUri;
    private Event event;
    private EventViewModel eventViewModel = new EventViewModel();
    private QrRepository qrRepository = new QrRepository();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = EventEventcreationBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    // Initializes UI elements and sets up event listeners
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCurrentDateTime();

        binding.addImage.setOnClickListener(listener -> openGallery());
        binding.date.setOnClickListener(listener -> pickDate());
        binding.startTime.setOnClickListener(listener -> pickTime(true));
        binding.endTime.setOnClickListener(listener -> pickTime(false));
        binding.back.setOnClickListener(listener -> {
            if (event == null) {
                Navigation.findNavController(requireView()).navigateUp();
            } else {
                deleteEvent(event);
            }
        });

        binding.back1.setOnClickListener(listener -> {
            Navigation.findNavController(requireView()).navigateUp();
        });

        binding.create.setOnClickListener(listener -> {
            if (validateInputs()) {
                if (selectedImageUri != null) {
                    uploadImageToFirebase(selectedImageUri);
                } else {
                    if (event == null) {
                        Toast.makeText(requireActivity(), "Please select an image", Toast.LENGTH_SHORT).show();
                    } else {
                        posterUrl = event.getPosterImageId();
                        createEventInFirestore();
                    }
                }
            } else {
                Toast.makeText(requireActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });

        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("selectedEvent");
        }
        if (event != null) {
            setEventData();
        }
    }

    // Set the data to the UI elements if editing an event
    private void setEventData() {
        binding.title.setText(event.getEventTitle());
        binding.description.setText(event.getEventDescription());
        String[] parts = event.getEventDate().split(", ");
        binding.date.setText(parts[0]);
        String[] times = parts[1].split(" - ");
        binding.startTime.setText(times[0]);
        binding.endTime.setText(times[1]);
        binding.address.setText(event.getEventLocation());
        binding.winners.setText(event.getMaxWinners());
        binding.entrants.setText(event.getMaxEntrants().toString());

        // Load the poster image using an image loading library (e.g., Glide)
        Image image = new Image("1234567890", "123456789");
        image.download(new ImageQuery() {
            @Override
            public void onSuccess(Image image) {
                Glide.with(binding.getRoot()).load(image.getImageUrl()).into(binding.camera);
            }

            @Override
            public void onEmpty() {}
        });
        binding.back1.setVisibility(View.VISIBLE);
        binding.titleMain.setText("Edit Event");
        binding.back.setImageResource(R.drawable.ic_delete);
    }

    // Sets the current date and time in the UI as default values
    private void setCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d yyyy");
        String currentDate = dateFormat.format(calendar.getTime());
        binding.date.setText(currentDate);

        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        String currentTime = timeFormat.format(calendar.getTime());
        binding.startTime.setText(currentTime);

        calendar.add(Calendar.HOUR_OF_DAY, 3);
        String endTime = timeFormat.format(calendar.getTime());
        binding.endTime.setText(endTime);
    }

    // Validates that all required input fields are filled
    private boolean validateInputs() {
        return !binding.title.getText().toString().isEmpty() &&
                !binding.address.getText().toString().isEmpty() &&
                !binding.winners.getText().toString().isEmpty() &&
                !binding.entrants.getText().toString().isEmpty() &&
                !binding.description.getText().toString().isEmpty();
    }

    // Opens the gallery for the user to select an image
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

    // Uploads image to Firebase Storage and retrieves the download URL
    private void uploadImageToFirebase(Uri imageUri) {
        Image image = new Image("1234567890", "123456789");
        image.upload(imageUri, task -> {
            if (task.isSuccessful()) {
                posterUrl = task.getResult().getImageUrl();
            }
        });
        createEventInFirestore();
    }

    private void createEventInFirestore() {
        // Generate a new event or update existing one based on user input
        String eventTitle = binding.title.getText().toString();
        String eventDescription = binding.description.getText().toString();
        String promoQrCodeId = generateRandomQrCodeId(10);
        String waitingListQrCodeId = generateRandomQrCodeId(10);
        String date = binding.date.getText().toString() + ", " + binding.startTime.getText().toString() + " - " + binding.endTime.getText().toString();

        if (event == null) {
            Event newEvent = new Event(
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
                    0,
                    System.currentTimeMillis(),
                    System.currentTimeMillis()
            );

            pushEventToFirestore(newEvent);
        } else {
            Event newEvent = new Event(
                    event.getEventId(),
                    event.getOrganizerId(),
                    event.getFacilityId(),
                    eventTitle,
                    eventDescription,
                    date,
                    event.getPromoQrCodeId(),
                    event.getWaitingListQrCodeId(),
                    posterUrl,
                    binding.address.getText().toString(),
                    binding.winners.getText().toString(),
                    true,
                    Integer.parseInt(binding.entrants.getText().toString()),
                    event.getEntrants(),
                    event.getCreatedAt(),
                    System.currentTimeMillis()
            );

            pushEventUpdateToFirestore(newEvent);
        }
    }

    private void pushEventUpdateToFirestore(Event event) {
        eventViewModel.updateEvent(event, new OnCompleteListener<Object>() {
            @Override
            public void onComplete(Task<Object> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(requireActivity(), "Event Updated Successfully!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigateUp();
                } else {
                    Toast.makeText(requireActivity(), "Failed to update event", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    private void deleteEvent(Event event) {
        eventViewModel.deleteEvent(event, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(requireActivity(), "Event deleted Successfully!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp();
            } else {
                Toast.makeText(requireActivity(), "Failed to delete event", Toast.LENGTH_SHORT).show();
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

    private void pickTime(boolean isStartTime) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePicker = new TimePickerDialog(requireActivity(), (timeView, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
            String formattedTime = timeFormat.format(calendar.getTime());

            if (isStartTime) {
                binding.startTime.setText(formattedTime);
            } else {
                binding.endTime.setText(formattedTime);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

        timePicker.show();
    }
}

/**
 * Code Sources
 *
 * ChatGPT:
 * - Implementing an image picker in Android Fragment.
 * - Managing Firebase Firestore data for UI fragments.
 *
 * Stack Overflow:
 * - How to open the image gallery in an Android Fragment.
 * - Using DatePickerDialog and TimePickerDialog in Fragment.
 *
 * Android Developer Documentation:
 * - Firebase Firestore for Android
 * - Fragments and Navigation
 * - Displaying Date and Time Pickers- Documentation for using date and time pickers.
 */
