package com.example.pickme.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.pickme.R;
import com.example.pickme.models.Event;
import com.example.pickme.repositories.EventRepository;
import com.example.pickme.repositories.QrRepository;
import com.example.pickme.utils.QRCodeGenerator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.File;

/**
 * Fragment to display a large QR code and event title for a specific event.
 * Responsibilities:
 * - Fetch event details from the database and display the event title.
 * - Generate or retrieve a cached QR code based on eventID.
 * - Display the QR code and event title within the layout.
 */
public class QRCodeViewFragment extends Fragment {

    private static final String ARG_EVENT_ID = "eventID";

    private String eventID;

    private TextView eventTitleTextView;
    private ImageView qrCodeImageView;

    private EventRepository eventRepository;
    private QRCodeGenerator qrCodeGenerator;

    /**
     * Default constructor for QRCodeViewFragment.
     * Required for Fragment instantiation.
     */
    public QRCodeViewFragment() {
        // Required empty public constructor
    }

    /**
     * Static factory method to create a new instance of QRCodeViewFragment with the specified
     * event ID.
     *
     * @param eventID ID of the event for which the QR code is generated.
     * @return A new instance of QRCodeViewFragment with the specified arguments.
     */
    public static QRCodeViewFragment newInstance(String eventID) {
        QRCodeViewFragment fragment = new QRCodeViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve eventID from arguments if available
        if (getArguments() != null) {
            eventID = getArguments().getString(ARG_EVENT_ID);
        }

        // Initialize repositories and QR code generator
        eventRepository = EventRepository.getInstance();
        qrCodeGenerator = new QRCodeGenerator(new QrRepository());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.qr_display_activity, container, false);

        // Set up view references for event title and QR code image
        eventTitleTextView = view.findViewById(R.id.eventTitle);
        qrCodeImageView = view.findViewById(R.id.qrCodeImage);

        // Set up the back button to perform the default back action
        ImageButton backButton = view.findViewById(R.id.myImageButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        // Set up the share button to share the QR code
        view.findViewById(R.id.shareButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareQRCode();
            }
        });

        // Load event details and QR code for display
        loadEventDetails();
        return view;
    }

    /**
     * Loads event details using EventRepository and updates the event title TextView.
     * If the event title is fetched successfully, calls displayQRCode() to generate and display the QR code.
     */
    private void loadEventDetails() {
        // Fetch event details from Firestore based on eventID
        eventRepository.getEventById(eventID, new OnCompleteListener<Event>() {
            @Override
            public void onComplete(@NonNull Task<Event> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    Event event = task.getResult();
                    // Retrieve "eventTitle" from the Event object
                    String eventTitle = event.getEventTitle();
                    if (eventTitle != null) {
                        // Update the event title TextView
                        eventTitleTextView.setText(eventTitle);
                    } else {
                        Log.e("QRCodeViewFragment", "Event title not found in document.");
                        Toast.makeText(getContext(), "Event title not found", Toast.LENGTH_SHORT).show();
                    }

                    // Generate and display the QR code
                    displayQRCode();
                } else {
                    Log.e("QRCodeViewFragment", "Failed to load event details: " + task.getException());
                    Toast.makeText(getContext(), "Failed to load event details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Generates a QR code using QRCodeGenerator for the given eventID.
     * Checks if a cached QR code exists; if so, it displays the cached image.
     * Otherwise, generates a new QR code, caches it, and displays it in qrCodeImageView.
     */
    private void displayQRCode() {
        qrCodeGenerator.getQRCodeImage(requireContext(), eventID, new QRCodeGenerator.QRCodeCallback() {
            @Override
            public void onQRCodeReady(String filePath) {
                if (filePath != null) {
                    File qrCodeFile = new File(filePath);
                    if (qrCodeFile.exists()) {
                        Bitmap qrBitmap = BitmapFactory.decodeFile(filePath);
                        qrCodeImageView.setImageBitmap(qrBitmap);
                        qrCodeImageView.setVisibility(View.VISIBLE); // Make the ImageView visible
                        qrCodeImageView.setAlpha(0f);
                        qrCodeImageView.animate().alpha(1f).setDuration(300).start(); // Apply fade-in animation
                    } else {
                        Toast.makeText(getContext(), "QR code file not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to load QR code file path", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Shares the QR code currently displayed in the ImageView.
     * Uses the cached file path provided by QRCodeGenerator to create a shareable intent.
     */
    private void shareQRCode() {
        qrCodeGenerator.getQRCodeImage(requireContext(), eventID, new QRCodeGenerator.QRCodeCallback() {
            @Override
            public void onQRCodeReady(String filePath) {
                if (filePath != null) {
                    File qrCodeFile = new File(filePath);
                    if (qrCodeFile.exists()) {
                        Uri qrCodeUri = FileProvider.getUriForFile(
                                requireContext(),
                                requireContext().getPackageName() + ".provider",
                                qrCodeFile
                        );

                        // Create an intent to share the file
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("image/png");
                        shareIntent.putExtra(Intent.EXTRA_STREAM, qrCodeUri);
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        // Launch the share dialog
                        startActivity(Intent.createChooser(shareIntent, "Share QR Code"));
                    } else {
                        Toast.makeText(getContext(), "QR code file not found for sharing", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to retrieve QR code file path", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), "Unable to share QR code: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
