package com.example.pickme.views;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.pickme.R;
import com.example.pickme.repositories.QrRepository;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.List;

public class QRCameraFragment extends Fragment {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 101;
    private static final String TAG = "QRCameraFragment";

    private DecoratedBarcodeView barcodeScannerView;
    private TextView scanResultTextView;
    private QrRepository qrRepository;
    private boolean isNavigationComplete = false;
    private boolean isFirebaseInquiryComplete = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr_camera, container, false);

        // Initialize the scanner view
        barcodeScannerView = view.findViewById(R.id.barcodeScannerView);
        scanResultTextView = view.findViewById(R.id.scanResult);

        qrRepository = new QrRepository();

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

    @Override
    public void onResume() {
        super.onResume();
        if (barcodeScannerView != null) {
            barcodeScannerView.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (barcodeScannerView != null && isFirebaseInquiryComplete) {
            barcodeScannerView.pause();
        }
    }

    private void setupScanner() {
        Log.d(TAG, "Setting up scanner...");
        barcodeScannerView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result.getText() != null) {
                    Log.d(TAG, "Scanned QR Code: " + result.getText());
                    fetchEventDetails(result.getText());
                }
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
                Log.d(TAG, "Possible result points detected");
            }
        });
    }

    private void fetchEventDetails(String qrID) {
        Log.d(TAG, "Starting Firestore query for QR ID: " + qrID);

        isFirebaseInquiryComplete = false;

        qrRepository.readQRByID(qrID).addOnCompleteListener(task -> {
            if (!isAdded() || getView() == null) {
                Log.w(TAG, "Fragment is not active; skipping Firestore result handling");
                isFirebaseInquiryComplete = true;
                return;
            }

            if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                Log.d(TAG, "Firestore Query Result: " + task.getResult().getDocuments());

                String qrAssociation = task.getResult().getDocuments().get(0).getString("qrAssociation");
                if (qrAssociation != null && qrAssociation.contains("/events/")) {
                    String eventID = qrAssociation.substring(qrAssociation.lastIndexOf("/") + 1);
                    Log.d(TAG, "Extracted Event ID: " + eventID);

                    completeCameraTaskAndNavigate(eventID);
                } else {
                    Log.d(TAG, "Invalid qrAssociation format: " + qrAssociation);
                    Toast.makeText(requireContext(), "QR code association is invalid", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d(TAG, "No matching QR code found in Firestore for ID: " + qrID);
                Toast.makeText(requireContext(), "QR code not associated with an event", Toast.LENGTH_SHORT).show();
            }

            isFirebaseInquiryComplete = true;
        }).addOnFailureListener(e -> {
            if (!isAdded() || getView() == null) {
                Log.w(TAG, "Fragment is not active; skipping Firestore error handling");
                isFirebaseInquiryComplete = true;
                return;
            }

            Log.e(TAG, "Firestore query error", e);
            Toast.makeText(requireContext(), "Error fetching event details", Toast.LENGTH_SHORT).show();

            isFirebaseInquiryComplete = true;
        });
    }

    private void completeCameraTaskAndNavigate(String eventID) {
        if (barcodeScannerView != null) {
            barcodeScannerView.pause();
        }
        navigateToEventDetails(eventID);
    }

    private void navigateToEventDetails(String eventID) {
        if (isNavigationComplete) {
            Log.d(TAG, "Navigation already completed, skipping...");
            return;
        }

        if (eventID != null && !eventID.isEmpty()) {
            if (isAdded() && getView() != null) {
                isNavigationComplete = true;
                Bundle args = new Bundle();
                args.putString("eventID", eventID);
                NavController navController = Navigation.findNavController(requireView());

                Log.d(TAG, "Navigating to EventDetailsFragment with Event ID: " + eventID);
                navController.navigate(R.id.action_qrCameraFragment_to_eventDetailsFragment, args);
            } else {
                Log.e(TAG, "Fragment not attached or view is unavailable for navigation");
            }
        } else {
            Log.e(TAG, "Event ID is null or empty");
            Toast.makeText(requireContext(), "Event ID is missing", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestCameraPermission() {
        Log.d(TAG, "Requesting camera permission...");
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
    }

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
