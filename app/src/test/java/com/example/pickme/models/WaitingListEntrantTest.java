package com.example.pickme.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.example.pickme.models.Enums.EntrantStatus;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import org.junit.Before;
import org.junit.Test;

public class WaitingListEntrantTest {

    private WaitingListEntrant entrant;

    @Before
    public void setUp() {
        entrant = new WaitingListEntrant("user123", new GeoPoint(-33.8688, 151.2093), EntrantStatus.PENDING);
    }

    @Test
    public void testDefaultConstructor() {
        WaitingListEntrant defaultEntrant = new WaitingListEntrant();
        assertNotNull(defaultEntrant.getCreatedAt());  // CreatedAt should be initialized
        assertTrue(defaultEntrant.getCreatedAt().compareTo(Timestamp.now()) <= 0); // Verify it's not in the future
    }

    @Test
    public void testConstructorWithArguments() {
        assertEquals("user123", entrant.getEntrantId());
        assertEquals(EntrantStatus.PENDING, entrant.getStatus());
        assertNotNull(entrant.getCreatedAt());
        assertEquals(entrant.getCreatedAt(), entrant.getUpdatedAt());  // Both timestamps should be the same initially
    }

    @Test
    public void testSetWaitListEntrantId() {
        String newId = "waitlist456";
        entrant.setWaitListEntrantId(newId);
        assertEquals(newId, entrant.getWaitListEntrantId());
    }

    @Test
    public void testNullWaitListEntrantId() {
        entrant.setWaitListEntrantId(null);
        assertNull(entrant.getWaitListEntrantId());
    }

    @Test
    public void testSetEntrantId() {
        String newEntrantId = "user789";
        entrant.setEntrantId(newEntrantId);
        assertEquals(newEntrantId, entrant.getEntrantId());
        assertTrue(entrant.getUpdatedAt().compareTo(entrant.getCreatedAt()) > 0); // UpdatedAt should be later than CreatedAt after update
    }

    @Test
    public void testNullEntrantId() {
        entrant.setEntrantId(null);
        assertNull(entrant.getEntrantId());
    }

    @Test
    public void testSetGeoLocation() {
        GeoPoint newLocation = new GeoPoint(-25.3639, 131.0449);
        entrant.setGeoLocation(newLocation);
        assertEquals(newLocation, entrant.getGeoLocation());
        assertTrue(entrant.getUpdatedAt().compareTo(entrant.getCreatedAt()) > 0); // UpdatedAt should be later than CreatedAt after update
    }

    @Test
    public void testNullGeoLocation() {
        entrant.setGeoLocation(null);
        assertNull(entrant.getGeoLocation());
    }

    @Test
    public void testSetStatus() {
        entrant.setStatus(EntrantStatus.APPROVED);
        assertEquals(EntrantStatus.APPROVED, entrant.getStatus());
        assertTrue(entrant.getUpdatedAt().compareTo(entrant.getCreatedAt()) > 0); // UpdatedAt should be later than CreatedAt after update
    }

    @Test
    public void testSetNotified() {
        entrant.setNotified(true);
        assertTrue(entrant.isNotified());
        assertTrue(entrant.getUpdatedAt().compareTo(entrant.getCreatedAt()) > 0); // UpdatedAt should be later than CreatedAt after update
    }

    @Test
    public void testIsNotifiedFalse() {
        assertFalse(entrant.isNotified());  // Default notified status should be false
    }

    @Test
    public void testGetCreatedAtNotModified() {
        Timestamp initialCreatedAt = entrant.getCreatedAt();
        // Since 'createdAt' should not be modified after initialization, ensure it remains unchanged
        entrant.setUpdatedAt(Timestamp.now());  // Set updatedAt to check it doesn't affect createdAt
        assertEquals(initialCreatedAt, entrant.getCreatedAt());
    }
}