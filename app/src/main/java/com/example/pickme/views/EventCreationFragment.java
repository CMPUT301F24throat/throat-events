package com.example.pickme.views;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.pickme.R;
import com.example.pickme.models.Event;
import com.example.pickme.models.User;
import com.example.pickme.repositories.EventRepository;
import com.example.pickme.repositories.FacilityRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Fragment for creating or updating an event.
 */
public class EventCreationFragment extends Fragment {

    private Uri selectedImageUri;
    private Event event;

    private FacilityRepository facilityRepository;
    private EventRepository eventRepository;

    private String organizerId;

    private EditText eventTitleEdit, descriptionEdit, eventDateEdit, startTimeEdit, endTimeEdit, locationEdit, maxWinnersEdit, maxEntrantsEdit;
    private TextView addImage, titleCharCount, descriptionCharCount, locationCharCount;
    private Button upsertEventBtn, deleteEventBtn, backBtn;
    private CheckBox requireGeolocation;
    private ImageView iv;

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.event_create, container, false);
    }

    /**
     * Called after the view has been created.
     * Initializes the views and sets click listeners for UI elements.
     *
     * @param view The created view.
     * @param savedInstanceState Bundle containing saved state.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        facilityRepository = FacilityRepository.getInstance();
        eventRepository = EventRepository.getInstance();
        organizerId = User.getInstance().getDeviceId();

        initializeViews(view);
    }

    /**
     * Initializes the views and sets click listeners for UI elements.
     *
     * @param view The created view.
     */
    private void initializeViews(@NonNull View view) {

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
        iv = view.findViewById(R.id.addImage_preview);
        titleCharCount = view.findViewById(R.id.eventUpsert_titleCharCount);
        locationCharCount = view.findViewById(R.id.eventUpsert_locationCharCount);
        descriptionCharCount = view.findViewById(R.id.eventUpsert_descriptionCharCount);

        setUpClickListeners(view);
        setupCharacterCounts();
    }

    /**
     * Sets up click listeners for UI elements.
     *
     * @param view The created view.
     */
    private void setUpClickListeners(@NonNull View view) {

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
                setEventData(view);

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
        if (event == null) {
            setCurrentDateTime();
        }
    }

    /**
     * Sets the event data in the UI if an event is being edited.
     */
    private void setEventData(View view) {
        if (event == null) {
            return;
        }

        // Make sure to handle null data in event fields
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
        Glide
                .with(view.getContext())
                .load(event.getPosterImageId())
                .into(iv);
    }

    /**
     * Sets the current date and time in the UI.
     */
    private void setCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_YEAR, 1);  // Set the default date to tomorrow to avoid creating events in the past
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d yyyy", java.util.Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());
        eventDateEdit.setText(currentDate);

        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", java.util.Locale.getDefault());
        String currentTime = timeFormat.format(calendar.getTime());
        startTimeEdit.setText(currentTime);

        calendar.add(Calendar.HOUR_OF_DAY, 3);
        String endTime = timeFormat.format(calendar.getTime());
        endTimeEdit.setText(endTime);
    }

    /**
     * Sets up character count for input fields.
     * Limits event title to 100 characters max, description to 750 characters max, and location to 75 characters max.
     */
    private void setupCharacterCounts() {
        // Set character limits
        eventTitleEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
        descriptionEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(750)});
        locationEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(75)});

        // Add text change listeners to update character count
        eventTitleEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                titleCharCount.setText(s.length() + "/100");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        descriptionEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                descriptionCharCount.setText(s.length() + "/750");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        locationEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                locationCharCount.setText(s.length() + "/75");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    /**
     * Validates the input fields to ensure all required fields are filled.
     * @return true if all required fields are filled, false otherwise.
     */
    private boolean validateInputs() {
        String eventTitle = eventTitleEdit.getText().toString();
        String eventDescription = descriptionEdit.getText().toString();
        String eventLocation = locationEdit.getText().toString();

        if (eventTitle.isEmpty() || eventTitle.length() > 100) {
            Toast.makeText(requireActivity(), "Event title must be between 1 and 100 characters", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (eventDescription.length() > 750) {
            Toast.makeText(requireActivity(), "Event description must be 750 characters or less", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (eventLocation.isEmpty() || eventLocation.length() > 75) {
            Toast.makeText(requireActivity(), "Event location must be between 1 and 75 characters", Toast.LENGTH_SHORT).show();
            return false;
        }

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
        galleryLauncher.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    /**
     * Activity result launcher for picking an image from the gallery.
     */
    private final ActivityResultLauncher<PickVisualMediaRequest> galleryLauncher = registerForActivityResult (
            new ActivityResultContracts.PickVisualMedia(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    Glide
                            .with(iv.getRootView())
                            .load(selectedImageUri)
                            .into(iv);
                }
            }
    );

    /**
     * Creates or updates an event in Firestore.
     */
    private void upsertEvent() {
        String eventTitle = eventTitleEdit.getText().toString();
        String eventDescription = descriptionEdit.getText().toString();
        String eventLocation = locationEdit.getText().toString();
        final int maxWinners;
        final Integer maxEntrants;

        // Check if maxWinnersEdit is not empty
        if (!maxWinnersEdit.getText().toString().isEmpty()) {
            try {
                maxWinners = Integer.parseInt(maxWinnersEdit.getText().toString());

                if (maxWinners < 1) {
                    Toast.makeText(requireActivity(), "Max winners must be greater than 0", Toast.LENGTH_SHORT).show();
                    return;
                }

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

                if (maxEntrants < 1) {
                    Toast.makeText(requireActivity(), "Max entrants must be greater than 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (maxEntrants < maxWinners) {
                    Toast.makeText(requireActivity(), "Max entrants must be greater than or equal to max winners", Toast.LENGTH_SHORT).show();
                    return;
                }

            } catch (NumberFormatException e) {
                Toast.makeText(requireActivity(), "Please enter a valid number for max entrants", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            maxEntrants = null; // Optional field
        }

        String dateTime = eventDateEdit.getText().toString() + ", " + startTimeEdit.getText().toString() + " - " + endTimeEdit.getText().toString();
        boolean isGeolocationRequired = requireGeolocation.isChecked();

        facilityRepository.getFacilityByOwnerDeviceId(organizerId, task -> {
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
                            null,  // set to null here, will be initialized in EventRepository
                            eventLocation,
                            maxWinners,
                            isGeolocationRequired,
                            maxEntrants,
                            null,  // set to null here, will be initialized in EventRepository
                            false    // set to null here, will be initialized in EventRepository
                    );

                    eventRepository.addEvent(newEvent, selectedImageUri, addEventTask -> {
                        if (addEventTask.isSuccessful()) {
                            Toast.makeText(requireActivity(), "Event Created Successfully!", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(requireView()).navigateUp();
                        } else {
                            Toast.makeText(requireActivity(), "Failed to create event", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    event.setEventTitle(eventTitle);
                    event.setEventDescription(eventDescription);
                    event.setEventDate(dateTime);
                    event.setEventLocation(eventLocation);
                    event.setMaxWinners(maxWinners);
                    event.setMaxEntrants(maxEntrants);
                    event.setGeoLocationRequired(isGeolocationRequired);

                    eventRepository.updateEvent(event, selectedImageUri, updateEventTask -> {
                        if (updateEventTask.isSuccessful()) {
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
        eventRepository.deleteEvent(event.getEventId(), deleteEventTask -> {
            if (deleteEventTask.isSuccessful()) {
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

            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d yyyy", java.util.Locale.getDefault());
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

            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", java.util.Locale.getDefault());
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