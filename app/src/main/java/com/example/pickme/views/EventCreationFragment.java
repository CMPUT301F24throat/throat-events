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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.pickme.R;
import com.example.pickme.models.Event;
import com.example.pickme.models.Image;
import com.example.pickme.models.User;
import com.example.pickme.models.WaitingListEntrant;
import com.example.pickme.repositories.EventRepository;
import com.example.pickme.repositories.FacilityRepository;
import com.example.pickme.utils.ImageQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Fragment for creating or updating an event.
 */
public class EventCreationFragment extends Fragment {
    private String posterUrl;
    private Uri selectedImageUri;
    private Event event;

    private FacilityRepository facilityRepository = new FacilityRepository();
    private String organizerId;

    private EditText eventTitleEdit, descriptionEdit, eventDateEdit, startTimeEdit, endTimeEdit, locationEdit, maxWinnersEdit, maxEntrantsEdit;
    private TextView addImage;
    private Button upsertEventBtn, deleteEventBtn, backBtn;
    private CheckBox requireGeolocation;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.event_create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        organizerId = User.getInstance().getDeviceId();

        // Initialize views
        eventTitleEdit = view.findViewById(R.id.title);
        descriptionEdit = view.findViewById(R.id.description);
        eventDateEdit = view.findViewById(R.id.date);
        startTimeEdit = view.findViewById(R.id.startTime);
        endTimeEdit = view.findViewById(R.id.endTime);
        locationEdit = view.findViewById(R.id.address);
        maxWinnersEdit = view.findViewById(R.id.winners);
        maxEntrantsEdit = view.findViewById(R.id.entrants);
        addImage = view.findViewById(R.id.addImage);
        backBtn = view.findViewById(R.id.back);
        upsertEventBtn = view.findViewById(R.id.create);
        deleteEventBtn = view.findViewById(R.id.deleteEvent);
        requireGeolocation = view.findViewById(R.id.requireGeolocation);

        // Set click listeners for UI elements
        addImage.setOnClickListener(listener -> openGallery());
        eventDateEdit.setOnClickListener(listener -> pickDate());
        startTimeEdit.setOnClickListener(listener -> pickTime(true));
        endTimeEdit.setOnClickListener(listener -> pickTime(false));
        backBtn.setOnClickListener(listener -> Navigation.findNavController(requireView()).navigateUp());
        upsertEventBtn.setOnClickListener(listener -> {
            if (validateInputs()) {
                upsertEvent();
            } else {
                Toast.makeText(requireActivity(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            }
        });

        // Check if there are arguments passed to the fragment
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("selectedEvent");

            // If an event is being edited, set the event data in the UI
            if (event != null) {
                setEventData();

                deleteEventBtn.setVisibility(View.VISIBLE);
                deleteEventBtn.setOnClickListener(listener -> {
                    if (event == null) {
                        // Navigate up if no event is being edited
                        Navigation.findNavController(requireView()).navigateUp();
                    } else {
                        // Delete the event if it exists
                        deleteEvent(event);
                    }
                });
                backBtn.setText("Edit Event");
                upsertEventBtn.setText("Save Changes");
            }
        }
        // Set the current eventDateEdit and time in the UI
        setCurrentDateTime();
    }

    /**
     * Sets the event data in the UI if an event is being edited.
     */
    private void setEventData() {
        if (event == null) {
            return;
        }

        eventTitleEdit.setText(event.getEventTitle() != null ? event.getEventTitle() : "");
        descriptionEdit.setText(event.getEventDescription() != null ? event.getEventDescription() : "");
        String[] parts = event.getEventDate() != null ? event.getEventDate().split(", ") : new String[]{"", ""};
        eventDateEdit.setText(parts[0]);
        String[] times = parts.length > 1 ? parts[1].split(" - ") : new String[]{"", ""};
        startTimeEdit.setText(times[0]);
        endTimeEdit.setText(times.length > 1 ? times[1] : "");
        locationEdit.setText(event.getEventLocation() != null ? event.getEventLocation() : "");
        maxWinnersEdit.setText(event.getMaxWinners() != null ? event.getMaxWinners().toString() : "");
        maxEntrantsEdit.setText(event.getMaxEntrants() != null ? event.getMaxEntrants().toString() : "");
        requireGeolocation.setChecked(event.isGeoLocationRequired());

        // Download and set the event image
        Image image = new Image("1234567890", "123456789");
        image.download(new ImageQuery() {
            @Override
            public void onSuccess(Image image) {
                // TODO: FIXXXX Set image to ImageView
                //Glide.with(getView()).load(image.getImageUrl()).into(binding.camera);
            }

            @Override
            public void onEmpty() {}
        });
    }

    /**
     * Sets the current date and time in the UI.
     */
    private void setCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d yyyy");
        String currentDate = dateFormat.format(calendar.getTime());
        eventDateEdit.setText(currentDate);

        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        String currentTime = timeFormat.format(calendar.getTime());
        startTimeEdit.setText(currentTime);

        calendar.add(Calendar.HOUR_OF_DAY, 3);
        String endTime = timeFormat.format(calendar.getTime());
        endTimeEdit.setText(endTime);
    }

    /**
     * Validates the input fields to ensure all required fields are filled.
     * @return true if all required fields are filled, false otherwise.
     */
    private boolean validateInputs() {
        return !eventTitleEdit.getText().toString().isEmpty() &&
                !eventDateEdit.getText().toString().isEmpty() &&
                !startTimeEdit.getText().toString().isEmpty() &&
                !endTimeEdit.getText().toString().isEmpty() &&
                !locationEdit.getText().toString().isEmpty() &&
                !maxWinnersEdit.getText().toString().isEmpty();
    }

    /**
     * Opens the gallery to select an image.
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

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
     * Uploads the selected image to Firebase.
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
     * Creates or updates an event in Firestore.
     */
    private void upsertEvent() {
        String eventTitle = eventTitleEdit.getText().toString();
        String eventDescription = descriptionEdit.getText().toString();
        String eventLocation = locationEdit.getText().toString();
        final Integer maxWinners;
        final Integer maxEntrants;

        // Check if maxWinnersEdit is not empty
        if (!maxWinnersEdit.getText().toString().isEmpty()) {
            try {
                maxWinners = Integer.parseInt(maxWinnersEdit.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(requireActivity(), "Please enter a valid number for max winners", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            Toast.makeText(requireActivity(), "Max winners is required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if maxEntrantsEdit is not empty
        if (!maxEntrantsEdit.getText().toString().isEmpty()) {
            try {
                maxEntrants = Integer.parseInt(maxEntrantsEdit.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(requireActivity(), "Please enter a valid number for max entrants", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            maxEntrants = null; // Optional field
        }

        String dateTime = eventDateEdit.getText().toString() + ", " + startTimeEdit.getText().toString() + " - " + endTimeEdit.getText().toString();
        boolean isGeolocationRequired = requireGeolocation.isChecked();

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
                            dateTime,
                            posterUrl,
                            eventLocation,
                            maxWinners,
                            isGeolocationRequired,
                            maxEntrants,
                            new ArrayList<WaitingListEntrant>()
                    );

                    EventRepository eventRepository = EventRepository.getInstance();
                    eventRepository.addEvent(newEvent, task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(requireActivity(), "Event Created Successfully!", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(requireView()).navigateUp();
                        } else {
                            Toast.makeText(requireActivity(), "Failed to create event", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Update the existing event
                    event.setEventTitle(eventTitle);
                    event.setEventDescription(eventDescription);
                    event.setEventDate(dateTime);
                    event.setEventLocation(eventLocation);
                    event.setMaxWinners(maxWinners);
                    if (maxEntrants != null) {
                        event.setMaxEntrants(maxEntrants);
                    }
                    event.setGeoLocationRequired(isGeolocationRequired);

                    EventRepository eventRepository = EventRepository.getInstance();
                    eventRepository.updateEvent(event, task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(requireActivity(), "Event Updated Successfully!", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(requireView()).navigateUp();
                        } else {
                            Toast.makeText(requireActivity(), "Failed to update event", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    /**
     * Deletes the specified event.
     * @param event the event to be deleted.
     */
    private void deleteEvent(Event event) {
        EventRepository eventRepository = EventRepository.getInstance();

        eventRepository.deleteEvent(event.getEventId(), task -> {
            if (task.isSuccessful()) {
                Toast.makeText(requireActivity(), "Event deleted Successfully!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp();
            } else {
                Toast.makeText(requireActivity(), "Failed to delete event", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Opens a date picker dialog to select a date.
     */
    private void pickDate() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(requireActivity(), (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);

            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d yyyy");
            String formattedDate = dateFormat.format(calendar.getTime());

            eventDateEdit.setText(formattedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePicker.show();
    }

    /**
     * Opens a time picker dialog to select a time.
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
                startTimeEdit.setText(formattedTime);
            } else {
                endTimeEdit.setText(formattedTime);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);

        timePicker.show();
    }
}