package com.example.pickme.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pickme.models.QR;
import com.example.pickme.repositories.QrRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Utility class for generating and caching QR codes.
 * <p>
 * This class provides methods to:
 * - Retrieve QR codes from Firestore or generate them dynamically.
 * - Cache QR codes locally for reuse to minimize redundant computation.
 * - Handle QR code generation errors and provide callbacks for results.
 * <p>
 * It uses the ZXing library for QR code generation and stores cached images in the app's cache directory.
 */
public class QRCodeGenerator {

    private static final int QR_CODE_WIDTH = 500;
    private static final int QR_CODE_HEIGHT = 500;
    private static final String CACHE_DIR = "qr_cache";

    private final QrRepository qrRepository;

    /**
     * Constructor for QRCodeGenerator.
     *
     * @param qrRepository Repository to access QR data
     */
    public QRCodeGenerator(QrRepository qrRepository) {
        this.qrRepository = qrRepository;
    }

    /**
     * Generate or retrieve cached QR Code for an event based on eventID.
     *
     * @param context   Context to access cache directory
     * @param eventID   ID of the event
     * @param callback  Callback to handle the generated or retrieved QR code image file path
     */
    public void getQRCodeImage(Context context, String eventID, QRCodeCallback callback) {
        String cacheFileName = eventID + ".png";
        File cacheFile = new File(context.getCacheDir(), CACHE_DIR + "/" + cacheFileName);

        // Check if QR code already exists in cache
        if (cacheFile.exists()) {
            callback.onQRCodeReady(cacheFile.getAbsolutePath());
            return;
        }

        // Retrieve QR document from Firestore asynchronously
        qrRepository.readQRByAssociation("/events/" + eventID)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);

                            // Log the entire document for debugging
                            Log.d("QRCodeGenerator", "Retrieved document data: " + document.getData());

                            QR qr = document.toObject(QR.class);

                            if (qr != null && qr.getQrId() != null) {
                                // Generate and cache QR code
                                Bitmap qrCodeBitmap = generateQRCode(qr.getQrId());
                                if (qrCodeBitmap != null) {
                                    saveBitmapToCache(cacheFile, qrCodeBitmap);
                                    callback.onQRCodeReady(cacheFile.getAbsolutePath());
                                } else {
                                    Log.e("QRCodeGenerator", "Failed to generate QR code bitmap for qrID: " + qr.getQrId());
                                    callback.onError("Error generating QR code");
                                }
                            } else {
                                Log.e("QRCodeGenerator", "QR ID not found in document for eventID: " + eventID);
                                callback.onError("QR ID not found");
                            }
                        } else {
                            Log.e("QRCodeGenerator", "No QR document found for association: /events/" + eventID);
                            callback.onError("QR document not found");
                        }
                    }
                });

    }

    /**
     * Generate QR Code Bitmap from a given QR ID.
     *
     * @param qrID ID to encode into the QR code
     * @return Bitmap representing the QR code
     */
    private Bitmap generateQRCode(String qrID) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(qrID, BarcodeFormat.QR_CODE, QR_CODE_WIDTH, QR_CODE_HEIGHT);

            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);  // Black or White
                }
            }
            return bmp;
        } catch (WriterException e) {
            Log.e("QRCodeGenerator", "Error generating QR code", e);
            return null;
        }
    }

    /**
     * Save bitmap to cache directory.
     *
     * @param file   File where the bitmap will be saved
     * @param bitmap Bitmap to save
     */
    private void saveBitmapToCache(File file, Bitmap bitmap) {
        file.getParentFile().mkdirs(); // Ensure the directory exists
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (IOException e) {
            Log.e("QRCodeGenerator", "Error saving QR code to cache", e);
        }
    }

    /**
     * Callback interface for QR code generation and retrieval.
     */
    public interface QRCodeCallback {
        /**
         * Called when the QR code is ready.
         *
         * @param filePath Path to the QR code image file
         */
        void onQRCodeReady(String filePath);

        /**
         * Called when there is an error generating or retrieving the QR code.
         *
         * @param errorMessage Error message describing the issue
         */
        void onError(String errorMessage);
    }
}

/*
  Code Sources
  <p>
  ChatGPT-4o:
  - Explain how to implement caching of bitmaps in an android app
  - What are the best Java libraries for creating QR codes?
  <p>
  Github:
  - ZXing ("Zebra Crossing") Library documentation
  <p>
  Java Documentation:
  - Caching in java
 */