package com.example.pickme.views;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pickme.R;
import com.example.pickme.repositories.EventRepository;
import com.example.pickme.repositories.QrRepository;
import com.example.pickme.utils.QRCodeGenerator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

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
        eventRepository = new EventRepository();
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
        eventRepository.getEventById(eventID, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    DocumentSnapshot document = task.getResult();
                    // Retrieve "eventTitle" instead of "title"
                    String eventTitle = document.getString("eventTitle");
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
                Bitmap qrBitmap = BitmapFactory.decodeFile(filePath);
                if (qrBitmap != null) {
                    qrCodeImageView.setImageBitmap(qrBitmap);
                    qrCodeImageView.setVisibility(View.VISIBLE); // Make the ImageView visible
                    qrCodeImageView.setAlpha(0f);
                    qrCodeImageView.animate().alpha(1f).setDuration(300).start(); // Apply fade-in animation
                } else {
                    Toast.makeText(getContext(), "Failed to load QR code", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
