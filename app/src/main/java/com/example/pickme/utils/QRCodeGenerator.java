package com.example.pickme.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.pickme.repositories.QrRepository;
import com.google.android.gms.tasks.Tasks;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class QRCodeGenerator {

    private static final int QR_CODE_WIDTH = 500;
    private static final int QR_CODE_HEIGHT = 500;
    private static final String CACHE_DIR = "qr_cache";

    private final QrRepository qrRepository;

    public QRCodeGenerator(QrRepository qrRepository) {
        this.qrRepository = qrRepository;
    }

    /**
     * Generate or retrieve cached QR Code for an event based on eventID.
     *
     * @param context Context to access cache directory
     * @param eventID ID of the event
     * @return File path to the cached QR code image or null if an error occurred
     */
    public String getQRCodeImage(Context context, String eventID) {
        String cacheFileName = eventID + ".png";
        File cacheFile = new File(context.getCacheDir(), CACHE_DIR + "/" + cacheFileName);

        // Check if QR code already exists in cache
        if (cacheFile.exists()) {
            return cacheFile.getAbsolutePath(); // Return cached file path if it exists
        }

        // QR code not in cache; retrieve qrID from Firestore using QrRepository
        try {
            String qrID = retrieveQrID(eventID);
            if (qrID == null) {
                Log.e("QRCodeGenerator", "QR ID not found for eventID: " + eventID);
                return null;
            }

            // Generate and cache QR code
            Bitmap qrCodeBitmap = generateQRCode(qrID);
            if (qrCodeBitmap != null) {
                saveBitmapToCache(cacheFile, qrCodeBitmap);
                return cacheFile.getAbsolutePath(); // Return the path to the cached QR code image
            } else {
                Log.e("QRCodeGenerator", "Failed to generate QR code bitmap for qrID: " + qrID);
                return null;
            }
        } catch (Exception e) {
            Log.e("QRCodeGenerator", "Error generating QR code", e);
            return null;
        }
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
     * Retrieve qrID for a given eventID from Firestore.
     *
     * @param eventID ID of the event
     * @return qrID if found, null otherwise
     */
    private String retrieveQrID(String eventID) throws ExecutionException, InterruptedException {
        return Tasks.await(qrRepository.readQRByAssociation("/events/" + eventID))
                .getDocuments().stream().findFirst()
                .map(document -> document.getString("qrID"))
                .orElse(null);
    }

    /**
     * Save bitmap to cache directory.
     *
     * @param file File where the bitmap will be saved
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
}
