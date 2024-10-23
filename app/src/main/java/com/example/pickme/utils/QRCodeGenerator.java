package com.example.pickme.utils;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * Utility to generate QR codes for events
 * Responsibilities:
 * Generate QR codes based on event data (e.g., event ID, hash)
 * Return the image URL for event QR codes
 **/

public class QRCodeGenerator {

    private static final int QR_CODE_WIDTH = 500;
    private static final int QR_CODE_HEIGHT = 500;

    /**
     * Generate QR Code Bitmap from a given event string
     *
     * @param eventData Data for the event (e.g., event ID or hash)
     * @return Bitmap representing the QR code
     */
    public static Bitmap generateQRCode(String eventData) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(eventData, BarcodeFormat.QR_CODE, QR_CODE_WIDTH, QR_CODE_HEIGHT);

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
            // add logging
            return null;
        }
    }

    /**
     * Convert event to a QR Code image URL
     *
     * @param eventData Data for the event
     * @return URL of the generated QR code image (Placeholder: add actual upload/storage logic)
     */
    public static String getQRCodeImageURL(String eventData) {
        // For now, this method is a placeholder.
        Bitmap qrCodeBitmap = generateQRCode(eventData);

        // TODO: Upload qrCodeBitmap to Firebase and return the URL/pointer to data
        return "https://example.com/qrcode_placeholder.png";
    }
}

