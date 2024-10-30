package com.example.pickme.repositories;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Handles interactions with the QRs collection
 * @author sophiecabungcal
 * @version 1.0
 * Responsibilities:
 * CRUD operations for QR data
 */
public class QrRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference qrRef = db.collection("QRs");

    // TODO: not sure how QR works yet; need to figure out how to store them in Firebase Storage

}
