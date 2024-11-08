package com.example.pickme.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class QRTest {

    // Helper method to create a QR instance
    private QR mockQR() {
        return new QR("testAssociation");
    }

    @Test
    public void testQrAssociation() {
        QR qr = mockQR();

        // Test association value from constructor
        assertEquals("testAssociation", qr.getQrAssociation());

        // Test association with a different value
        QR anotherQr = new QR("newAssociation");
        assertEquals("newAssociation", anotherQr.getQrAssociation());
    }

    @Test
    public void testQrIdSetterAndGetter() {
        QR qr = mockQR();

        // Ensure QR ID starts as null
        assertNull(qr.getQrId());

        // Set and verify QR ID
        qr.setQrId("testQrId");
        assertEquals("testQrId", qr.getQrId());
    }

    @Test
    public void testQrDefaultConstructor() {
        // Using default constructor
        QR defaultQR = new QR();

        // Check that both qrId and qrAssociation are null
        assertNull("QR ID should be null by default", defaultQR.getQrId());
        assertNull("QR association should be null by default", defaultQR.getQrAssociation());
    }

    @Test
    public void testQrObjectCreation() {
        QR qr = new QR("event123");

        // Validate association and ID after setting
        qr.setQrId("qr12345");
        assertEquals("event123", qr.getQrAssociation());
        assertEquals("qr12345", qr.getQrId());
    }
}
