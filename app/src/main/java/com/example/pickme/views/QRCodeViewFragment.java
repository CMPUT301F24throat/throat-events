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
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.pickme.R;
import com.example.pickme.models.Event;
import com.example.pickme.models.QR;
import com.example.pickme.models.User;
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
    private static final String TAG = "QRCodeViewFragment"; // Add a TAG for logging

    private String eventID;

    private TextView eventTitleTextView;
    private ImageView qrCodeImageView;

    private EventRepository eventRepository;
    private QrRepository qrRepository;
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
        qrRepository = QrRepository.getInstance();
        qrCodeGenerator = new QRCodeGenerator(qrRepository);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.qr_display_activity, container, false);

        // Set up view references
        eventTitleTextView = view.findViewById(R.id.eventTitle);
        qrCodeImageView = view.findViewById(R.id.qrCodeImage);

        ImageButton backButton = view.findViewById(R.id.myImageButton);
        backButton.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        view.findViewById(R.id.shareButton).setOnClickListener(v -> shareQRCode());

        // Set up the delete button
        AppCompatButton deleteButton = view.findViewById(R.id.qrDeleteButton);

        // Retrieve and log the User instance
        User user = User.getInstance();
        if (user != null) {
            // Log the entire User instance (assuming `toString` is implemented)
            Log.d(TAG, "User instance: " + user.toString());

            // Log specific fields if `toString` isn't implemented
            Log.d(TAG, "User details: " +
                    "ID=" + user.getUserId() +
                    ", FirstName=" + user.getFirstName() +
                    ", LastName=" + user.getLastName() +
                    ", Email=" + user.getEmailAddress() +
                    ", IsAdmin=" + user.isAdmin());

            if (user.isAdmin()) {
                deleteButton.setVisibility(View.VISIBLE); // Show for admin users
                Log.d(TAG, "Delete button made visible.");
            } else {
                deleteButton.setVisibility(View.GONE); // Hide for non-admin users
                Log.d(TAG, "Delete button hidden for non-admin user.");
            }
        } else {
            Log.e(TAG, "User instance is null. Cannot determine admin status.");
            deleteButton.setVisibility(View.GONE); // Hide if user instance is null
        }

        // Define the delete action
        deleteButton.setOnClickListener(v -> deleteAndRegenerateQRCode());

        // Load event details and QR code
        loadEventDetails();
        return view;
    }


    private void deleteAndRegenerateQRCode() {
        String qrPath = "/events/" + eventID;

        // Attempt to delete the QR document from the repository
        qrRepository.deleteQRByAssociation(qrPath)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "QR code removed successfully", Toast.LENGTH_SHORT).show();
                    regenerateQRCode(); // Proceed to regenerate the QR code
                })
                .addOnFailureListener(e -> {
                    // Log the failure but proceed to regenerate a new QR code
                    Log.e(TAG, "Failed to remove QR code: ", e);
                    Toast.makeText(getContext(), "Failed to remove old QR code, generating a new one", Toast.LENGTH_SHORT).show();
                    regenerateQRCode(); // Proceed to regenerate the QR code
                });
    }

    private void regenerateQRCode() {
        String newEncodedPath = "/events/" + eventID;

        // Clear old cached QR code file
        qrCodeGenerator.getQRCodeImage(requireContext(), eventID, new QRCodeGenerator.QRCodeCallback() {
            @Override
            public void onQRCodeReady(String filePath) {
                if (filePath != null) {
                    File qrCodeFile = new File(filePath);
                    if (qrCodeFile.exists() && qrCodeFile.delete()) {
                        Log.d(TAG, "Old QR code file deleted successfully.");
                    } else {
                        Log.d(TAG, "No old QR code file to delete or failed to delete.");
                    }
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Error clearing old QR code file: " + errorMessage);
            }
        });

        // Create a new QR object
        QR qr = new QR(newEncodedPath);

        qrRepository.createQR(qr)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "QR code regenerated successfully", Toast.LENGTH_SHORT).show();
                    displayQRCode(); // Reload and display the new QR code
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to regenerate QR code: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to regenerate QR code: ", e);
                });
    }


    private void loadEventDetails() {
        eventRepository.getEventById(eventID, new OnCompleteListener<Event>() {
            @Override
            public void onComplete(@NonNull Task<Event> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    Event event = task.getResult();
                    String eventTitle = event.getEventTitle();
                    if (eventTitle != null) {
                        eventTitleTextView.setText(eventTitle);
                    } else {
                        Log.e(TAG, "Event title not found in document.");
                        Toast.makeText(getContext(), "Event title not found", Toast.LENGTH_SHORT).show();
                    }
                    displayQRCode();
                } else {
                    Log.e(TAG, "Failed to load event details: ", task.getException());
                    Toast.makeText(getContext(), "Failed to load event details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void displayQRCode() {
        qrCodeGenerator.getQRCodeImage(requireContext(), eventID, new QRCodeGenerator.QRCodeCallback() {
            @Override
            public void onQRCodeReady(String filePath) {
                if (filePath != null) {
                    File qrCodeFile = new File(filePath);
                    if (qrCodeFile.exists()) {
                        Bitmap qrBitmap = BitmapFactory.decodeFile(filePath);
                        qrCodeImageView.setImageBitmap(qrBitmap);
                        qrCodeImageView.setVisibility(View.VISIBLE);
                        qrCodeImageView.setAlpha(0f);
                        qrCodeImageView.animate().alpha(1f).setDuration(300).start();
                    } else {
                        Toast.makeText(getContext(), "QR code file not found", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "QR code file not found: " + filePath);
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to load QR code file path", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to load QR code file path.");
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error displaying QR code: " + errorMessage);
            }
        });
    }

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

                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("image/png");
                        shareIntent.putExtra(Intent.EXTRA_STREAM, qrCodeUri);
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        startActivity(Intent.createChooser(shareIntent, "Share QR Code"));
                    } else {
                        Toast.makeText(getContext(), "QR code file not found for sharing", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "QR code file not found for sharing: " + filePath);
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to retrieve QR code file path", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to retrieve QR code file path.");
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), "Unable to share QR code: " + errorMessage, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error sharing QR code: " + errorMessage);
            }
        });
    }
}
