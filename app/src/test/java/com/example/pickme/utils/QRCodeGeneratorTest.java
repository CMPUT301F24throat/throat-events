package com.example.pickme.utils;

import static org.junit.Assert.assertNotNull;

import com.example.pickme.repositories.QrRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class QRCodeGeneratorTest {

    @Mock
    QrRepository mockQrRepository;

    QRCodeGenerator qrCodeGenerator;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        qrCodeGenerator = new QRCodeGenerator(mockQrRepository);
    }

    @Test
    public void testQRCodeGeneratorInitialization() {
        // This test just verifies that the QRCodeGenerator instance is created successfully
        assertNotNull("QRCodeGenerator instance should be created", qrCodeGenerator);
    }

}
