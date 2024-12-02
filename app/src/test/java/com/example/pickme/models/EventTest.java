package com.example.pickme.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.google.firebase.Timestamp;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Unit tests for the Event class.
 * This test class verifies the behavior, constraints, and validation logic of the Event model class,
 * including date formatting, entrant limits, and winner specifications.
 *
 * @version 1.0
 */

public class EventTest {

    private Event event;

    @Before
    public void setUp() {
        event = new Event("1", "organizer123", "facility456", "Sample Event",
                "An event description", "October 5 2024, 7:00 PM", "poster123",
                "123 Main St", 5, true, 100, new ArrayList<>(), false);
    }

//    @Test
//    public void testInvalidMaxEntrants() {
//        // Test for negative max entrants
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            event.setMaxEntrants(-1);  // Invalid, negative value
//        });
//        assertEquals("Max entrants must be a positive value.", exception.getMessage());
//
//        // Test for zero max entrants
//        exception = assertThrows(IllegalArgumentException.class, () -> {
//            event.setMaxEntrants(0);  // Invalid, zero value
//        });
//        assertEquals("Max entrants must be a positive value.", exception.getMessage());
//    }

    @Test
    public void testHasLotteryExecuted() {
        // Test setting and getting GeoLocationRequired
        event.setHasLotteryExecuted(false);
        assertFalse(event.getHasLotteryExecuted());

        event.setHasLotteryExecuted(true);
        assertTrue(event.getHasLotteryExecuted());
    }

    @Test
    public void testInvalidEventDate() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            event.setEventDate("invalid date format");  // Invalid date
        });

        String expectedMessage = "Invalid date format. Please use 'MMMM d yyyy, h:mm a'.";
        String actualMessage = exception.getMessage();

        assertTrue(Objects.requireNonNull(actualMessage).contains(expectedMessage)); // Verify the message matches
    }

    @Test
    public void testMaxWinners() {
        // Test for negative max winners
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            event.setMaxWinners(-1);  // Invalid, negative value
        });

        String expectedMessage = "Max winners must be a non-negative numeric value.";
        String actualMessage = exception.getMessage();

        assertTrue(Objects.requireNonNull(actualMessage).contains(expectedMessage)); // Verify the message matches

        // Testing with a valid numeric value
        event.setMaxWinners(50);
        assertEquals(50, event.getMaxWinners());
    }

    @Test
    public void testNullEventTitle() {
        event.setEventTitle(null);
        assertNull(event.getEventTitle());  // Event title should be null

        event.setEventDescription(null);
        assertNull(event.getEventDescription());  // Event description should also be null
    }

//    @Test
//    public void testEdgeDateFormat() {
//        // Edge case for date formatting
//        String edgeDate = "December 31 2024, 11:59 PM";
//        event.setEventDate(edgeDate);
//        assertEquals(edgeDate, event.getEventDate());
//    }

    @Test
    public void testEmptyLocation() {
        event.setEventLocation("");
        assertEquals("", event.getEventLocation());
    }

    @Test
    public void testGeoLocationRequired() {
        // Test setting and getting GeoLocationRequired
        event.setGeoLocationRequired(false);
        assertFalse(event.isGeoLocationRequired());

        event.setGeoLocationRequired(true);
        assertTrue(event.isGeoLocationRequired());
    }

    @Test
    public void testEventWithLargeDescription() {
        // Test with large description
        String largeDescription = "A".repeat(1000);  // 1000 characters long description
        event.setEventDescription(largeDescription);
        assertEquals(largeDescription, event.getEventDescription());
    }

    @Test
    public void testEventLocationWithSpecialChars() {
        // Test location with special characters
        String locationWithSpecialChars = "123 Main St, #456, Suite 789";
        event.setEventLocation(locationWithSpecialChars);
        assertEquals(locationWithSpecialChars, event.getEventLocation());
    }

    @Test
    public void testCreatedAtNotModified() {
        Timestamp createdAtBefore = event.getCreatedAt();
        // Since 'createdAt' should not be modified after initialization, ensure it remains unchanged
        event.setUpdatedAt(Timestamp.now());  // Set updatedAt to check it doesn't affect createdAt
        assertEquals(createdAtBefore, event.getCreatedAt());
    }

    @Test
    public void testPosterImageId() {
        // Test setting poster image ID to a valid value
        String validPosterImageId = "poster456";
        event.setPosterImageId(validPosterImageId);
        assertEquals(validPosterImageId, event.getPosterImageId());
    }

//    @Test
//    public void testHasEventPassed() {
//        // Test if the event date has passed
//        event.setEventDate("October 5 2020, 7:00 PM");
//        assertTrue(event.hasEventPassed());
//
//        event.setEventDate("October 5 2024, 7:00 PM");
//        assertFalse(event.hasEventPassed());
//    }

    @Test
    public void testEqualityAndHashCode() {
        Event event2 = new Event("1", "organizer123", "facility456", "Sample Event",
                "An event description", "October 5 2024, 7:00 PM", "poster123",
                "123 Main St", 5, true, 100, new ArrayList<>(), false);

        assertEquals(event, event2);
        assertEquals(event.hashCode(), event2.hashCode());
    }
}