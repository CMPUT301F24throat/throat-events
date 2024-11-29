package com.example.pickme.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

/**
 * Provides utilities for managing geolocation features
 * Responsibilities:
 * Check if geolocation is enabled
 * Fetch and store user geolocation
 * Validate geolocation data for event participation
 * Calculate distance between two geolocations
 **/

public class GeoLocationUtils {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    /**
     * Request location permissions from the user.
     *
     * @param activity The activity requesting the permission.
     */
    public void requestLocationPermission(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        } else {
            Toast.makeText(activity, "Location permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Check if location permissions are granted.
     *
     * @param context The context to check permissions.
     * @return True if location permissions are granted, false otherwise.
     */
    public boolean areLocationPermissionsGranted(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Check if location services are enabled.
     *
     * @param context The context to check location services.
     * @return True if location services are enabled, false otherwise.
     */
    public boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    /**
     * Fetch the current location.
     *
     * @param context  The context of the caller.
     * @param listener Callback to handle the fetched location.
     */
    @SuppressLint("MissingPermission")
    public void fetchCurrentLocation(Context context, OnLocationFetchedListener listener) {
        if (!areLocationPermissionsGranted(context)) {
            Toast.makeText(context, "Location permission is not granted", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isLocationEnabled(context)) {
            Toast.makeText(context, "Please enable location services", Toast.LENGTH_SHORT).show();
            return;
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000); // 10 seconds
        locationRequest.setFastestInterval(5000); // 5 seconds
        locationRequest.setNumUpdates(1); // Single update

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult != null && locationResult.getLastLocation() != null) {
                    Location location = locationResult.getLastLocation();
                    listener.onLocationFetched(location.getLatitude(), location.getLongitude());
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    /**
     * Stop location updates.
     */
    public void stopLocationUpdates() {
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    /**
     * Callback interface for location fetching.
     */
    public interface OnLocationFetchedListener {
        void onLocationFetched(double latitude, double longitude);
    }
}