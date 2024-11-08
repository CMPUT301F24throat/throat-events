package com.example.pickme.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Unit tests for the {@link QR} class, validating the behavior of getters, setters,
 * constructors, and object initialization.
 */
public class QRTest {

    /**
     * Helper method to create a mock {@link QR} instance with a predefined association.
     *
     * @return A {@link QR} instance with "testAssociation" as the association.
     */
    private QR mockQR() {
        return new QR("testAssociation");
    }

    /**
     * Tests the {@link QR#getQrAssociation()} method to ensure the association is set correctly
     * via the constructor and retrieved accurately.
     */
    @Test
    public void testQrAssociation() {
        QR qr = mockQR();

        // Test association value from constructor
        assertEquals("testAssociation", qr.getQrAssociation());

        // Test association with a different value
        QR anotherQr = new QR("newAssociation");
        assertEquals("newAssociation", anotherQr.getQrAssociation());
    }

    /**
     * Tests the {@link QR#setQrId(String)} and {@link QR#getQrId()} methods to verify
     * that the QR ID can be set and retrieved correctly.
     */
    @Test
    public void testQrIdSetterAndGetter() {
        QR qr = mockQR();

        // Ensure QR ID starts as null
        assertNull(qr.getQrId());

        // Set and verify QR ID
        qr.setQrId("testQrId");
        assertEquals("testQrId", qr.getQrId());
    }

    /**
     * Tests the default constructor {@link QR#QR()} to ensure that it initializes
     * the QR ID and association as null.
     */
    @Test
    public void testQrDefaultConstructor() {
        // Using default constructor
        QR defaultQR = new QR();

        // Check that both qrId and qrAssociation are null
        assertNull("QR ID should be null by default", defaultQR.getQrId());
        assertNull("QR association should be null by default", defaultQR.getQrAssociation());
    }

    /**
     * Tests creating a {@link QR} object with an association and setting a QR ID,
     * then validates that both values are set and retrieved correctly.
     */
    @Test
    public void testQrObjectCreation() {
        QR qr = new QR("event123");

        // Validate association and ID after setting
        qr.setQrId("qr12345");
        assertEquals("event123", qr.getQrAssociation());
        assertEquals("qr12345", qr.getQrId());
    }
}
