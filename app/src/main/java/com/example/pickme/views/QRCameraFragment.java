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

import com.example.pickme.R;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr_camera, container, false);

        // Initialize the scanner view
        barcodeScannerView = view.findViewById(R.id.barcodeScannerView);
        scanResultTextView = view.findViewById(R.id.scanResult);

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
        // Resume camera preview when the fragment is visible
        if (barcodeScannerView != null) {
            barcodeScannerView.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Pause camera preview to release resources
        if (barcodeScannerView != null) {
            barcodeScannerView.pause();
        }
    }

    private void setupScanner() {
        Log.d(TAG, "Setting up scanner...");
        // Set up continuous scanning callback
        barcodeScannerView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result.getText() != null) {
                    // Display scanned result in the TextView
                    scanResultTextView.setText("Scanned: " + result.getText());
                    Log.d(TAG, "Scanned QR Code: " + result.getText());
                    Toast.makeText(getContext(), "Scanned: " + result.getText(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
                Log.d(TAG, "Possible result points detected");
            }
        });
    }

    private void requestCameraPermission() {
        Log.d(TAG, "Requesting camera permission...");
        // Request the CAMERA permission
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, set up the scanner
                Log.d(TAG, "Camera permission granted");
                setupScanner();
            } else {
                // Permission denied
                Log.d(TAG, "Camera permission denied");
                Toast.makeText(requireContext(), "Camera permission is required to scan QR codes", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
