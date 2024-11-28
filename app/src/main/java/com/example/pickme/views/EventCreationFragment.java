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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.pickme.controllers.EventViewModel;
import com.example.pickme.databinding.EventCreateBinding;
import com.example.pickme.models.Event;
import com.example.pickme.models.Image;
import com.example.pickme.models.User;
import com.example.pickme.repositories.FacilityRepository;
import com.example.pickme.utils.ImageQuery;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

/**
 * Fragment for creating and managing events in the system.
 * Provides a UI for organizers to input event details, such as title, description, date, location, and poster image.
 * Handles validation, image selection, Firestore data storage, and QR code generation for events.
 *
 * @version 2.0
 * Responsibilities:
 * - Capture and validate user input for event details.
 * - Manage event creation, updating, and deletion in Firestore.
 * - Handle image selection and upload for event posters.
 * - Generate QR codes for promotional and waiting list purposes.
 * - Navigate to appropriate screens based on user actions.
 */
public class EventCreationFragment extends Fragment {

    private Button backBtn, deleteEventBtn, upsertEventBtn;
    private EditText eventTitle, eventDescription, maxEntrants, maxWinners, eventLocation;

    private EventCreateBinding binding;
    private String posterUrl;
    private Uri selectedImageUri;
    private Event event;
    private EventViewModel eventViewModel = new EventViewModel();
    private FacilityRepository facilityRepository = new FacilityRepository();
    private String organizerId;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment using view binding
        binding = EventCreateBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get the organizer ID from the current user instance
        organizerId = User.getInstance().getUserId();
        // Set the current date and time in the UI
        setCurrentDateTime();

        // Set click listeners for various UI elements
        binding.addImage.setOnClickListener(listener -> openGallery());
        binding.date.setOnClickListener(listener -> pickDate());
        binding.startTime.setOnClickListener(listener -> pickTime(true));
        binding.endTime.setOnClickListener(listener -> pickTime(false));
        binding.deleteEvent.setOnClickListener(listener -> {
            if (event == null) {
                // Navigate up if no event is being edited
                Navigation.findNavController(requireView()).navigateUp();
            } else {
                // Delete the event if it exists
                deleteEvent(event);
            }
        });

        binding.back.setOnClickListener(listener -> Navigation.findNavController(requireView()).navigateUp());
        binding.create.setOnClickListener(listener -> {
            if (validateInputs()) {
                upsertEvent();
//                if (selectedImageUri != null) {
//                    // Upload the selected image to Firebase
//                    uploadImageToFirebase(selectedImageUri);
//                } else {
//                    if (event == null) {
//                        Toast.makeText(requireActivity(), "Please select an image", Toast.LENGTH_SHORT).show();
//                    } else {
//                        // Use the existing poster URL if no new image is selected
//                        posterUrl = event.getPosterImageId();
//                        createOrUpdateEventInFirestore();
//                    }
//                }
            } else {
                Toast.makeText(requireActivity(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            }
        });

        // Check if there are arguments passed to the fragment
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("selectedEvent");
        }
        // If an event is being edited, set the event data in the UI
        if (event != null) {
            setEventData();
        }
    }

    /**
     * Set the event data in the UI for editing.
     */
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

        // Download and set the event image
        Image image = new Image("1234567890", "123456789");
        image.download(new ImageQuery() {
            @Override
            public void onSuccess(Image image) {
                // TODO: FIXXXX Set image to ImageView
                //Glide.with(binding.getRoot()).load(image.getImageUrl()).into(binding.camera);
            }

            @Override
            public void onEmpty() {}
        });

        // Update UI elements for editing mode
        binding.deleteEvent.setVisibility(View.VISIBLE);
        binding.back.setText("Edit Event");
    }

    /**
     * Set the current date and time in the UI.
     */
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

    /**
     * Validate the user inputs to ensure all required fields are filled.
     * @return true if all required fields are filled, false otherwise.
     */
    private boolean validateInputs() {
        return !binding.title.getText().toString().isEmpty() &&
                !binding.date.getText().toString().isEmpty() &&
                !binding.startTime.getText().toString().isEmpty() &&
                !binding.endTime.getText().toString().isEmpty() &&
                !binding.address.getText().toString().isEmpty() &&
                !binding.entrants.getText().toString().isEmpty();
    }

    /**
     * Open the gallery to select an image.
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    // Activity result launcher for handling the result of the gallery intent
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    // TODO: FIXXXX Set image to ImageView
                    //binding.addImage.setImageURI(selectedImageUri);
                }
            }
    );

    /**
     * Upload the selected image to Firebase.
     * @param imageUri the URI of the selected image.
     */
    private void uploadImageToFirebase(Uri imageUri) {
        Image image = new Image("1234567890", "123456789");
        image.upload(imageUri, task -> {
            if (task.isSuccessful()) {
                posterUrl = task.getResult().getImageUrl();
                upsertEvent();
            }
        });
    }

    /**
     * Create or update the event in Firestore.
     */
    private void upsertEvent() {
        String eventTitle = binding.title.getText().toString();
        String eventDescription = binding.description.getText().toString();
        String promoQrCodeId = generateRandomQrCodeId(10);
        String waitingListQrCodeId = generateRandomQrCodeId(10);
        String date = binding.date.getText().toString() + ", " + binding.startTime.getText().toString() + " - " + binding.endTime.getText().toString();

        facilityRepository.getFacilityByOwnerId(organizerId, task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                String facilityId = task.getResult().getDocuments().get(0).getId();

                if (event == null) {
                    // Create a new event
                    Event newEvent = new Event(
                            null,
                            organizerId,
                            facilityId,
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

                    pushEventToFirestore(newEvent);
                } else {
                    // Update the existing event
                    Event updatedEvent = new Event(
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
                            event.getCreatedAt(),
                            System.currentTimeMillis()
                    );

                    pushEventUpdateToFirestore(updatedEvent);
                }
            }
        });
    }

    /**
     * Push the new event to Firestore.
     * @param event the event to be added.
     */
    private void pushEventToFirestore(Event event) {
        eventViewModel.addEvent(event, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(requireActivity(), "Event Created Successfully!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp();
            } else {
                Toast.makeText(requireActivity(), "Failed to create event", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Push the updated event to Firestore.
     * @param event the event to be updated.
     */
    private void pushEventUpdateToFirestore(Event event) {
        eventViewModel.updateEvent(event, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(requireActivity(), "Event Updated Successfully!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp();
            } else {
                Toast.makeText(requireActivity(), "Failed to update event", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Delete the event from Firestore.
     * @param event the event to be deleted.
     */
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

    /**
     * Generate a random QR code ID.
     * @param length the length of the QR code ID.
     * @return the generated QR code ID.
     */
    private String generateRandomQrCodeId(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder id = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            id.append(chars.charAt(random.nextInt(chars.length())));
        }
        return id.toString();
    }

    /**
     * Show a date picker dialog to select a date.
     */
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

    /**
     * Show a time picker dialog to select a time.
     * @param isStartTime true if selecting start time, false if selecting end time.
     */
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