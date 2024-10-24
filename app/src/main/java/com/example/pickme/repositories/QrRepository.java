package com.example.pickme.repositories;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Handles QR uploads and metadata storage
 * Responsibilities:
 * CRUD operations for QR data
 * Upload QR (event details, waiting list) to Firebase Storage
 * Save qr metadata to the QRs collection
 * Delete qr when required
 **/

public class QrRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference qrRef = db.collection("QRs");
}
