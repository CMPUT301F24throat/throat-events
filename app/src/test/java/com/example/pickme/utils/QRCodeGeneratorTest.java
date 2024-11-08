package com.example.pickme.utils;

import static org.junit.Assert.assertNotNull;

import com.example.pickme.repositories.QrRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for the {@link QRCodeGenerator} class, ensuring proper initialization and setup.
 */
public class QRCodeGeneratorTest {

    /** Mock instance of {@link QrRepository} used to test {@link QRCodeGenerator} initialization. */
    @Mock
    QrRepository mockQrRepository;

    /** Instance of {@link QRCodeGenerator} being tested. */
    QRCodeGenerator qrCodeGenerator;

    /**
     * Sets up the test environment before each test case, initializing mock objects and
     * the {@link QRCodeGenerator} instance.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        qrCodeGenerator = new QRCodeGenerator(mockQrRepository);
    }

    /**
     * Verifies that the {@link QRCodeGenerator} instance is successfully created with
     * the mock {@link QrRepository} dependency.
     */
    @Test
    public void testQRCodeGeneratorInitialization() {
        // This test just verifies that the QRCodeGenerator instance is created successfully
        assertNotNull("QRCodeGenerator instance should be created", qrCodeGenerator);
    }
}
