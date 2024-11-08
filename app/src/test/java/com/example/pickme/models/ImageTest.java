package com.example.pickme.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.example.pickme.models.Enums.ImageType;
import com.example.pickme.repositories.ImageRepository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

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

    private Image profilePicture;
    private Image eventPoster;

    private String userId;
    private String eventId;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize the mocks
        userId = "test_user_id";
        eventId = "test_event_id";

        mockImageRepo = Mockito.mock(ImageRepository.class);
    }

    public Image createProfilePicture() {return new Image(userId, userId, mockImageRepo);}
    public Image createEventPoster() {return new Image(userId, eventId, mockImageRepo);}

    @Test
    public void testCreate_ProfilePicture() {
        profilePicture = createProfilePicture();
        Assert.assertEquals(profilePicture.getUploaderId(), userId);
        Assert.assertEquals(profilePicture.getImageAssociation(), userId);
        Assert.assertEquals(profilePicture.getImageType(), ImageType.PROFILE_PICTURE);
    }

    @Test
    public void testCreate_EventPoster() {
        eventPoster = createEventPoster();
        Assert.assertEquals(eventPoster.getUploaderId(), userId);
        Assert.assertEquals(eventPoster.getImageAssociation(), eventId);
        Assert.assertEquals(eventPoster.getImageType(), ImageType.EVENT_POSTER);
    }

    @Test
    public void testSetGet_ImageUrl() {
        String url = "test_image_url";
        profilePicture = createProfilePicture();
        assertNull(profilePicture.getImageUrl());
        profilePicture.setImageUrl(url);
        assertEquals(url, profilePicture.getImageUrl());
    }

    @Test
    public void testSetGet_ImageType() {
        ImageType type = ImageType.PROFILE_PICTURE;
        eventPoster = createEventPoster();
        Assert.assertEquals(eventPoster.getImageType(), ImageType.EVENT_POSTER);
        eventPoster.setImageType(type);
        Assert.assertEquals(eventPoster.getImageType(), type);
    }

    @Test
    public void testSetGet_ImageAssociation() {
        String association = "test_image_association";
        profilePicture = createProfilePicture();
        Assert.assertEquals(profilePicture.getImageAssociation(), userId);
        profilePicture.setImageAssociation(association);
        Assert.assertEquals(profilePicture.getImageAssociation(), association);
    }

    @Test
    public void testSetGet_UploaderId() {
        String uploaderId = "test_uploader_id";
        eventPoster = createEventPoster();
        Assert.assertEquals(eventPoster.getUploaderId(), userId);
        eventPoster.setUploaderId(uploaderId);
        Assert.assertEquals(eventPoster.getUploaderId(), uploaderId);
    }

    @Test
    public void testGet_CreatedAt() {
        profilePicture = createProfilePicture();
        assertNotNull(profilePicture.getCreatedAt());
    }

    @Test
    public void testGet_UpdatedAt() {
        eventPoster = createEventPoster();
        assertNotNull(eventPoster.getUpdatedAt());
    }
}
