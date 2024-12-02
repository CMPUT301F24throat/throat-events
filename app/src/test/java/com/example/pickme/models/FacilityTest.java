package com.example.pickme.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.firebase.Timestamp;

import org.junit.Before;
import org.junit.Test;

public class FacilityTest {

    private Facility facility;

    @Before
    public void setUp() {
        facility = new Facility("owner123", "Sample Facility", "1 Main St");
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull(facility.getCreatedAt());  // CreatedAt should be initialized
        assertTrue(facility.getCreatedAt().compareTo(Timestamp.now()) <= 0); // Verify it's not in the future
    }

    @Test
    public void testConstructorWithArguments() {
        assertEquals("owner123", facility.getOwnerId());
        assertEquals("Sample Facility", facility.getFacilityName());
        assertEquals("1 Main St", facility.getLocation());
        assertNotNull(facility.getCreatedAt());
        assertEquals(facility.getCreatedAt(), facility.getUpdatedAt());  // Both timestamps should be the same initially
    }

    @Test
    public void testSetFacilityId() {
        String newId = "facility456";
        facility.setFacilityId(newId);
        assertEquals(newId, facility.getFacilityId());
    }

    @Test
    public void testNullFacilityId() {
        // Facility ID should be set by Firebase, so null test might not be applicable. 
        // However for completeness you can uncomment the line below. 
        // facility.setFacilityId(null);  // This might throw an exception depending on implementation
        assertNull(facility.getFacilityId());  // Probably won't be null, but depends on your implementation
    }

    @Test
    public void testSetOwnerId() {
        String newOwnerId = "owner789";
        facility.setOwnerId(newOwnerId);
        assertEquals(newOwnerId, facility.getOwnerId());
        assertTrue(facility.getUpdatedAt().compareTo(facility.getCreatedAt()) > 0); // UpdatedAt should be later than CreatedAt after update
    }

    @Test
    public void testNullOwnerId() {
        facility.setOwnerId(null);
        assertNull(facility.getOwnerId());
    }

    @Test
    public void testSetFacilityName() {
        String newFacilityName = "Updated Facility";
        facility.setFacilityName(newFacilityName);
        assertEquals(newFacilityName, facility.getFacilityName());
        assertTrue(facility.getUpdatedAt().compareTo(facility.getCreatedAt()) > 0); // UpdatedAt should be later than CreatedAt after update
    }

    @Test
    public void testNullFacilityName() {
        facility.setFacilityName(null);
        assertNull(facility.getFacilityName());
    }

    @Test
    public void testSetLocation() {
        String newLocation = "123 Elm St";
        facility.setLocation(newLocation);
        assertEquals(newLocation, facility.getLocation());
        assertTrue(facility.getUpdatedAt().compareTo(facility.getCreatedAt()) > 0); // UpdatedAt should be later than CreatedAt after update
    }

    @Test
    public void testNullLocation() {
        facility.setLocation(null);
        assertNull(facility.getLocation());
    }

    @Test
    public void testGetCreatedAtNotModified() {
        Timestamp initialCreatedAt = facility.getCreatedAt();
        // Since 'createdAt' should not be modified after initialization, ensure it remains unchanged
        facility.setUpdatedAt(Timestamp.now());  // Set updatedAt to check it doesn't affect createdAt
        assertEquals(initialCreatedAt, facility.getCreatedAt());
    }
}