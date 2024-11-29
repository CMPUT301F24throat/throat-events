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
import com.example.pickme.models.QR;
import com.example.pickme.models.User;
import com.example.pickme.repositories.EventRepository;
import com.example.pickme.repositories.FacilityRepository;
import com.example.pickme.repositories.QrRepository;
import com.example.pickme.utils.ImageQuery;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Fragment for creating and editing events.
 */
public class EventCreationFragment extends Fragment {
    private String posterUrl;
    private Uri selectedImageUri;
    private Event event;

    private EventRepository eventRepository;
    private FacilityRepository facilityRepository;
    private QrRepository qrRepository;
    private String organizerId;

    private EditText eventTitleEdit, descriptionEdit, eventDateEdit, startTimeEdit, endTimeEdit, locationEdit, maxWinnersEdit, maxEntrantsEdit;
    private TextView addImage;
    private Button upsertEventBtn, deleteEventBtn, backBtn;
    private CheckBox requireGeolocation;

    /**
     * Inflates the layout for this fragment.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The View for the fragment's UI.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.event_create, container, false);
    }

    /**
     * Called immediately after onCreateView has returned, but before any saved state has been restored in to the view.
     *
     * @param view The View returned by onCreateView.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        organizerId = User.getInstance().getDeviceId();
        eventRepository = EventRepository.getInstance();
        facilityRepository = FacilityRepository.getInstance();
        qrRepository = QrRepository.getInstance();

        initializeViews(view);
        setClickListeners();

        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("selectedEvent");
            if (event != null) {
                setEventData();
                setupEditMode();
            }
        } else {
            setCurrentDateTime();
        }
    }

    /**
     * Initializes the views in the fragment.
     *
     * @param view The View returned by onCreateView.
     */
    private void initializeViews(View view) {
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
    }

    /**
     * Sets click listeners for the views.
     */
    private void setClickListeners() {
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
    }

    /**
     * Sets up the fragment for edit mode.
     */
    private void setupEditMode() {
        deleteEventBtn.setVisibility(View.VISIBLE);
        deleteEventBtn.setOnClickListener(listener -> {
            if (event == null) {
                Navigation.findNavController(requireView()).navigateUp();
            } else {
                deleteEvent(event);
            }
        });
        backBtn.setText("Edit Event");
        upsertEventBtn.setText("Save Changes");
    }

    /**
     * Sets the event data in the views.
     */
    private void setEventData() {
        if (event == null) return;

        eventTitleEdit.setText(event.getEventTitle() != null ? event.getEventTitle() : "");
        descriptionEdit.setText(event.getEventDescription() != null ? event.getEventDescription() : "");
        String[] parts = event.getEventDate() != null ? event.getEventDate().split(", ") : new String[]{"", ""};
        eventDateEdit.setText(parts[0]);
        String[] times = parts.length > 1 ? parts[1].split(" - ") : new String[]{"", ""};
        startTimeEdit.setText(times[0]);
        endTimeEdit.setText(times.length > 1 ? times[1] : "");
        locationEdit.setText(event.getEventLocation() != null ? event.getEventLocation() : "");
        maxWinnersEdit.setText(String.valueOf(event.getMaxWinners()));
        maxEntrantsEdit.setText(event.getMaxEntrants() != null ? event.getMaxEntrants().toString() : "");
        requireGeolocation.setChecked(event.isGeoLocationRequired());

        Image image = new Image("1234567890", "123456789");
        image.download(new ImageQuery() {
            @Override
            public void onSuccess(Image image) {
                // TODO: FIX - NEED TO SET UP IMAGE POSTER
                // Glide.with(getView()).load(image.getImageUrl()).into(binding.camera);
            }

            @Override
            public void onEmpty() {}
        });
    }

    /**
     * Sets the current date and time in the views.
     */
    private void setCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d yyyy");
        eventDateEdit.setText(dateFormat.format(calendar.getTime()));

        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        startTimeEdit.setText(timeFormat.format(calendar.getTime()));

        calendar.add(Calendar.HOUR_OF_DAY, 3);
        endTimeEdit.setText(timeFormat.format(calendar.getTime()));
    }

    /**
     * Validates the inputs in the views.
     *
     * @return true if all required fields are filled, false otherwise.
     */
    private boolean validateInputs() {
        if (isEmpty(eventTitleEdit) || isEmpty(eventDateEdit) || isEmpty(startTimeEdit) || isEmpty(endTimeEdit) || isEmpty(locationEdit) || isEmpty(maxWinnersEdit)) {
            return false;
        }

        String maxWinnersText = maxWinnersEdit.getText().toString();
        String maxEntrantsText = maxEntrantsEdit.getText().toString();

        try {
            int maxWinners = Integer.parseInt(maxWinnersText);
            if (!maxEntrantsText.isEmpty()) {
                int maxEntrants = Integer.parseInt(maxEntrantsText);
                if (maxEntrants < maxWinners) {
                    showToast("Max winners cannot be greater than max entrants");
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            showToast("Please enter valid numbers for max winners and max entrants");
            return false;
        }

        return true;
    }

    /**
     * Checks if an EditText is empty.
     *
     * @param editText The EditText to check.
     * @return true if the EditText is empty, false otherwise.
     */
    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().isEmpty();
    }

    /**
     * Shows a toast message.
     *
     * @param message The message to show.
     */
    private void showToast(String message) {
        if (isAdded()) {
            Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Opens the gallery to select an image.
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    /**
     * ActivityResultLauncher for handling the result of the gallery intent.
     */
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    // TODO: FIX
                    // binding.addImage.setImageURI(selectedImageUri);
                }
            }
    );

    /**
     * Uploads the selected image to Firebase.
     *
     * @param imageUri The URI of the selected image.
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
     * Creates or updates an event.
     */
    private void upsertEvent() {
        String eventTitle = eventTitleEdit.getText().toString();
        String eventDescription = descriptionEdit.getText().toString();
        String eventLocation = locationEdit.getText().toString();
        int maxWinners = Integer.parseInt(maxWinnersEdit.getText().toString());
        Integer maxEntrants = !maxEntrantsEdit.getText().toString().isEmpty() ? Integer.parseInt(maxEntrantsEdit.getText().toString()) : null;
        String dateTime = eventDateEdit.getText().toString() + ", " + startTimeEdit.getText().toString() + " - " + endTimeEdit.getText().toString();
        boolean isGeolocationRequired = requireGeolocation.isChecked();

        facilityRepository.getFacilityByOwnerId(organizerId, (querySnapshot, e) -> {
            if (e != null) {
                showToast("Failed to retrieve facility");
                return;
            }

            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                String facilityId = querySnapshot.getDocuments().get(0).getId();

                if (event == null) {
                    createNewEvent(eventTitle, eventDescription, dateTime, eventLocation, maxWinners, isGeolocationRequired, maxEntrants, facilityId);
                } else {
                    updateExistingEvent(eventTitle, eventDescription, dateTime, eventLocation, maxWinners, isGeolocationRequired, maxEntrants);
                }
            }
        });
    }

    /**
     * Creates a new event.
     *
     * @param eventTitle The title of the event.
     * @param eventDescription The description of the event.
     * @param dateTime The date and time of the event.
     * @param eventLocation The location of the event.
     * @param maxWinners The maximum number of winners.
     * @param isGeolocationRequired Whether geolocation is required for the event.
     * @param maxEntrants The maximum number of entrants.
     * @param facilityId The ID of the facility where the event is held.
     */
    private void createNewEvent(String eventTitle, String eventDescription, String dateTime, String eventLocation, int maxWinners, boolean isGeolocationRequired, Integer maxEntrants, String facilityId) {
        Event newEvent = new Event(
                null, organizerId, facilityId, eventTitle, eventDescription, dateTime, posterUrl, eventLocation, maxWinners, isGeolocationRequired, maxEntrants, null, null
        );
        eventRepository.addEvent(newEvent, addEventTask -> {
            if (addEventTask.isSuccessful()) {
                String newEventId = addEventTask.getResult();
                QR eventQr = new QR("/events/" + newEventId);

                qrRepository.createQR(eventQr)
                        .addOnSuccessListener(aVoid -> handleUpsertResult(true, "Event Created Successfully!", "Failed to create event QR"))
                        .addOnFailureListener(e -> handleUpsertResult(false, "Event Created Successfully!", "Failed to create event QR"));

                handleUpsertResult(true, "Event Created Successfully!", "Failed to create event");
            } else {
                handleUpsertResult(false, "Event Created Successfully!", "Failed to create event");
            }
        });
    }

    /**
     * Updates an existing event.
     *
     * @param eventTitle The new title of the event.
     * @param eventDescription The new description of the event.
     * @param dateTime The new date and time of the event.
     * @param eventLocation The new location of the event.
     * @param maxWinners The new maximum number of winners.
     * @param isGeolocationRequired Whether geolocation is required for the event.
     * @param maxEntrants The new maximum number of entrants.
     */
    private void updateExistingEvent(String eventTitle, String eventDescription, String dateTime, String eventLocation, int maxWinners, boolean isGeolocationRequired, Integer maxEntrants) {
        updateEventDetails(event, eventTitle, eventDescription, dateTime, eventLocation, maxWinners, isGeolocationRequired, maxEntrants);
        eventRepository.updateEvent(event, task -> handleUpsertResult(task.isSuccessful(), "Event Updated Successfully!", "Failed to update event"));
    }

    /**
     * Updates the details of an existing event.
     *
     * @param event The event to update.
     * @param title The new title of the event.
     * @param description The new description of the event.
     * @param dateTime The new date and time of the event.
     * @param location The new location of the event.
     * @param maxWinners The new maximum number of winners.
     * @param isGeolocationRequired Whether geolocation is required for the event.
     * @param maxEntrants The new maximum number of entrants.
     */
    private void updateEventDetails(Event event, String title, String description, String dateTime, String location, int maxWinners, boolean isGeolocationRequired, Integer maxEntrants) {
        event.setEventTitle(title);
        event.setEventDescription(description);
        event.setEventDate(dateTime);
        event.setEventLocation(location);
        event.setMaxWinners(maxWinners);
        if (maxEntrants != null) {
            event.setMaxEntrants(maxEntrants);
        }
        event.setGeoLocationRequired(isGeolocationRequired);
    }

    /**
     * Handles the result of creating or updating an event.
     *
     * @param isSuccess Whether the operation was successful.
     * @param successMessage The message to show if the operation was successful.
     * @param failureMessage The message to show if the operation failed.
     */
    private void handleUpsertResult(boolean isSuccess, String successMessage, String failureMessage) {
        showToast(isSuccess ? successMessage : failureMessage);
        if (isSuccess) {
            Navigation.findNavController(requireView()).navigateUp();
        }
    }

    /**
     * Deletes an event.
     *
     * @param event The event to delete.
     */
    private void deleteEvent(Event event) {
        EventRepository eventRepository = EventRepository.getInstance();
        eventRepository.deleteEvent(event.getEventId(), task -> handleUpsertResult(task.isSuccessful(), "Event deleted Successfully!", "Failed to delete event"));
    }

    /**
     * Opens a date picker dialog to select a date.
     */
    private void pickDate() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(requireActivity(), (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d yyyy");
            eventDateEdit.setText(dateFormat.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    /**
     * Opens a time picker dialog to select a time.
     *
     * @param isStartTime Whether the time picker is for the start time.
     */
    private void pickTime(boolean isStartTime) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePicker = new TimePickerDialog(requireActivity(), (timeView, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
            if (isStartTime) {
                startTimeEdit.setText(timeFormat.format(calendar.getTime()));
            } else {
                endTimeEdit.setText(timeFormat.format(calendar.getTime()));
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
        timePicker.show();
    }
}