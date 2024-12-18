package com.example.pickme.views;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.pickme.R;
import com.example.pickme.models.Event;
import com.example.pickme.repositories.EventRepository;
import com.example.pickme.utils.EventFetcher;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

/**
 * Fragment for scanning QR codes using the device's camera.
 * Handles camera permissions and QR code scanning functionality.
 *
 * This fragment relies on the ZXing library for continuous QR code scanning. It fetches event
 * details based on the scanned QR code and navigates to the event details screen upon success.
 *
 * Dependencies:
 * - ZXing for barcode scanning.
 * - EventFetcher for retrieving event details.
 * - Navigation component for fragment transitions.
 *
 * Assumptions:
 * - The device has a functional camera.
 * - The QR codes encode event IDs stored in the application's database.
 */
public class QRCameraFragment extends Fragment {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 101;
    private static final String TAG = "QRCameraFragment";

    private DecoratedBarcodeView barcodeScannerView;
    private boolean isProcessingScan = false; // Flag to prevent multiple processing

    /**
     * Inflates the fragment's layout and initializes the QR code scanner.
     * Checks for camera features and permissions before setting up the scanner.
     *
     * @param inflater The LayoutInflater object used to inflate the fragment's views.
     * @param container The parent view to which this fragment will be attached.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous state.
     * @return The root view of the fragment's layout.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr_camera, container, false);

        // Initialize the scanner view
        barcodeScannerView = view.findViewById(R.id.barcodeScannerView);

        // Check for camera feature
        if (!requireContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(requireContext(), "No camera detected on this device", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Check camera permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
        } else {
            setupScanner();
        }
        return view;
    }

    /**
     * Resumes the barcode scanner when the fragment is visible.
     * Ensures the scanner is ready to process QR codes and resets the scan flag.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (barcodeScannerView != null) {
            barcodeScannerView.resume();
            isProcessingScan = false; // Reset the scan flag to allow new scans
        }
    }

    /**
     * Pauses the barcode scanner when the fragment is not visible.
     * This prevents unnecessary camera usage while the fragment is in the background.
     */
    @Override
    public void onPause() {
        super.onPause();
        if (barcodeScannerView != null) {
            barcodeScannerView.pause();
        }
    }

    /**
     * Configures the barcode scanner to decode QR codes continuously.
     * Sets a callback to handle scanned results and prevents duplicate processing.
     */
    private void setupScanner() {
        Log.d(TAG, "Setting up scanner...");
        barcodeScannerView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result.getText() != null && !isProcessingScan) { // Only process if not already scanning
                    Log.d(TAG, "Scanned QR Code: " + result.getText());
                    isProcessingScan = true; // Set flag to prevent further scans
                    fetchEventDetails(result.getText());
                }
            }
        });
    }

    /**
     * Fetches the details of an event associated with a scanned QR code ID.
     * The result of the fetch determines the next navigation action.
     *
     * @param qrID The unique identifier extracted from the scanned QR code.
     *             Used to query the event repository.
     */
    private void fetchEventDetails(String qrID) {
        Log.d(TAG, "Starting fetcher for QR ID: " + qrID);

        // Create an instance of EventFetcher with the EventRepository
        EventFetcher eventFetcher = new EventFetcher(EventRepository.getInstance());

        eventFetcher.getEventByEventId(qrID, new EventFetcher.EventCallback() {
            @Override
            public void onEventFetched(Event event) {
                Log.d(TAG, "Event fetched: " + event.getEventTitle());
                navigateToEventDetails(event); // Pass the Event object
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Error fetching event: " + errorMessage);
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                isProcessingScan = false; // Allow scanning again for the next QR code
            }
        });
    }

    /**
     * Navigates to the EventDetailsFragment with the fetched event details.
     * If navigation fails or the event object is null, displays an error message.
     *
     * @param event The Event object containing event details. Must not be null.
     */
    private void navigateToEventDetails(Event event) {
        if (event != null) {
            if (isAdded() && getView() != null) {
                Bundle args = new Bundle();
                args.putSerializable("selectedEvent", event); // Pass the Event object as a Serializable
                NavController navController = Navigation.findNavController(requireView());

                Log.d(TAG, "Navigating to EventDetailsFragment with Event: " + event.getEventTitle());
                navController.navigate(R.id.action_qrCameraFragment_to_eventDetailsFragment, args);
            } else {
                Log.e(TAG, "Fragment not attached or view is unavailable for navigation");
            }
        } else {
            Log.e(TAG, "Event object is null");
            Toast.makeText(requireContext(), "Event details missing", Toast.LENGTH_SHORT).show();
            isProcessingScan = false; // Allow scanning again if navigation fails
        }
    }

    /**
     * Requests camera permission from the user if not already granted.
     * Displays a system dialog to prompt the user for permission.
     */
    private void requestCameraPermission() {
        Log.d(TAG, "Requesting camera permission...");
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
    }

    /**
     * Handles the result of the camera permission request.
     * If granted, sets up the scanner. Otherwise, shows an error message.
     *
     * @param requestCode The request code passed in requestPermissions.
     * @param permissions The requested permissions.
     * @param grantResults The results for the requested permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Camera permission granted");
                setupScanner();
            } else {
                Log.d(TAG, "Camera permission denied");
                Toast.makeText(requireContext(), "Camera permission is required to scan QR codes", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

/*
  Code Sources
  <p>
  ChatGPT-4o:
  - Explain how to implement camera permissions in java android applications
  - Example zxing scanning implementation
  <p>
  Github:
  - ZXing ("Zebra Crossing") Library documentation
  <p>
  Java Documentation:
 */