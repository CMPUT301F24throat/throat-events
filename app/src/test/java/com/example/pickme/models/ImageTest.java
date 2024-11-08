package com.example.pickme.models;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.net.Uri;

import com.example.pickme.models.Enums.ImageType;
import com.example.pickme.repositories.ImageRepository;
import com.example.pickme.utils.ImageQuery;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;

/**
 * Unit tests for the Image class.
 * Tests validate uploading, updating, and downloading images
 *
 * @version 1.0
 * @author etdong
 */
public class ImageTest {

    @Mock
    private ImageRepository mockImageRepo;

    @Mock
    private Task<Image> mockImageTask;

    @Mock
    private Image mockImage;

    private Image image;

    private String userId;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize the mocks
        userId = "test_user_id";

        mockImageRepo = Mockito.mock(ImageRepository.class);

        image = createImage();

        // Mock the behavior for uploading uris
        doAnswer(invocation -> {
            OnCompleteListener<Image> listener = invocation.getArgument(2);
            listener.onComplete(mockImageTask);
            return null;
        }).when(mockImageRepo).upload(any(Image.class), any(Uri.class), any());

        // Mock the behavior for uploading bytes
        doAnswer(invocation -> {
            OnCompleteListener<Image> listener = invocation.getArgument(2);
            listener.onComplete(mockImageTask);
            return null;
        }).when(mockImageRepo).upload(any(Image.class), any(byte[].class), any());

        // Mock the behavior for downloading
        doAnswer(invocation -> {
            ImageQuery iq = invocation.getArgument(1);
            iq.onSuccess(createImage());
            return null;
        }).when(mockImageRepo).download(any(), any());

        when(mockImageTask.isSuccessful()).thenReturn(true);
        when(mockImageTask.getResult()).thenReturn(createImage());
    }

    public Image createImage() {return new Image(userId, userId, mockImageRepo);}

    public byte[] generateBytes() {
        ByteArrayOutputStream bytes = Mockito.mock(ByteArrayOutputStream.class);
        return bytes.toByteArray();
    }

    @Test
    public void testCreate() {
        Assert.assertEquals(image.getUploaderId(), userId);
        Assert.assertEquals(image.getImageAssociation(), userId);
        Assert.assertEquals(image.getImageType(), ImageType.PROFILE_PICTURE);
        Assert.assertNull(image.getImageUrl());
    }

    @Test
    public void testUploadUri() {
        Uri uri = mock(Uri.class);
        image.upload(uri, task -> {
            Assert.assertTrue(task.isSuccessful());
            Image image = task.getResult();
            Assert.assertNotNull(image);
            Assert.assertEquals(image.getUploaderId(), userId);
            Assert.assertEquals(image.getImageAssociation(), userId);
            Assert.assertEquals(image.getImageType(), ImageType.PROFILE_PICTURE);
        });
    }

    @Test
    public void testUploadBytes() {
        byte[] data = generateBytes();
        image.upload(data, task -> {
            Assert.assertTrue(task.isSuccessful());
            Image image = task.getResult();
            Assert.assertNotNull(image);
            Assert.assertEquals(image.getUploaderId(), userId);
            Assert.assertEquals(image.getImageAssociation(), userId);
            Assert.assertEquals(image.getImageType(), ImageType.PROFILE_PICTURE);
        });

    }

    @Test
    public void testDownload() {
        Uri uri = mock(Uri.class);
        image.upload(uri, task -> {
            Assert.assertTrue(task.isSuccessful());
            image.download(new ImageQuery() {
                @Override
                public void onSuccess(Image image) {
                    Assert.assertNotNull(image);
                    Assert.assertEquals(image.getUploaderId(), userId);
                    Assert.assertEquals(image.getImageAssociation(), userId);
                    Assert.assertEquals(image.getImageType(), ImageType.PROFILE_PICTURE);
                }

                @Override
                public void onEmpty() {
                }
            });
        });
    }
}
