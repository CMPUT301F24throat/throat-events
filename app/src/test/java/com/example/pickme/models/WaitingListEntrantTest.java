package com.example.pickme.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
        entrant = new WaitingListEntrant();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull(entrant.getCreatedAt());
        assertTrue(entrant.getCreatedAt().compareTo(Timestamp.now()) <= 0);
    }

    @Test
    public void testConstructorWithArguments() {
        String entrantId = "user123";
        GeoPoint location = new GeoPoint(-33.8688, 151.2093); // Example coordinates (Sydney)
        EntrantStatus status = EntrantStatus.PENDING;

        entrant = new WaitingListEntrant(entrantId, location, status);

        assertEquals(entrantId, entrant.getEntrantId());
        assertEquals(location, entrant.getGeoLocation());
        assertEquals(status, entrant.getStatus());
        assertNotNull(entrant.getCreatedAt());
        assertEquals(entrant.getCreatedAt(), entrant.getUpdatedAt());
    }

    @Test
    public void testSetWaitListEntrantId() {
        String newId = "entrant456";
        entrant.setWaitListEntrantId(newId);
        assertEquals(newId, entrant.getWaitListEntrantId());
    }

    @Test
    public void testGetCreatedAtNotModified() {
        Timestamp initialCreatedAt = entrant.getCreatedAt();
        entrant.setUpdatedAt(Timestamp.now()); // Modify updatedAt
        assertEquals(initialCreatedAt, entrant.getCreatedAt()); // Ensure createdAt remains unchanged
    }

    @Test
    public void testNotEquals() {
        WaitingListEntrant other = new WaitingListEntrant();
        other.setWaitListEntrantId("differentId");

        assertFalse(entrant.equals(other));
    }

}