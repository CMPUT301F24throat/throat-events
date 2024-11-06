package com.example.pickme.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.pickme.models.Event;
import com.example.pickme.repositories.EventRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for the EventViewModel class using mocked EventRepository.
 * This test class verifies the ViewModel's behavior, particularly in CRUD operations,
 * data fetching, event selection, and Firestore interaction.
 *
 * @version 1.0
 * @author Ayub Ali
 * Responsibilities:
 * - Test CRUD operations via the ViewModel and mock repository.
 * - Validate that Firestore completion listeners trigger correctly for each operation.
 * - Ensure ViewModel functions like event selection and list maintenance behave as expected.
 * - Use Mockito to simulate Firestore operations and confirm correct method interactions.
 */

public class EventViewModelTest {

    @Mock
    private EventRepository mockEventRepository;

    @Mock
    private Task<QuerySnapshot> mockQuerySnapshotTask;

    @Mock
    private Task<Object> mockTaskObject;

    @Mock
    private OnCompleteListener<QuerySnapshot> mockOnCompleteListener;

    @Mock
    private OnCompleteListener<Object> mockOnCompleteListenerObject;
    @Mock
    private QuerySnapshot mockQuerySnapshot;

    private EventViewModel eventViewModel;
    private Event event;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);  // Initialize the mocks
        eventViewModel = new EventViewModel(mockEventRepository); // Initialize the ViewModel with mocked repository
        eventViewModel = spy(eventViewModel);  // Spy on the ViewModel to intercept calls to fetchEvents, etc.

        // Setup a sample event for testing
        event = new Event("1", "organizer123", "facility456", "Sample Event",
                "An event description", "October 5 2024, 7:00 PM",
                "promo123", "waitingList123", "poster123",
                "123 Main St", "5", true, 100, System.currentTimeMillis(), System.currentTimeMillis());

        // Mock the behavior of repository methods

        // Mock the behavior of repository methods for void-returning methods
        doAnswer(invocation -> {
            OnCompleteListener<QuerySnapshot> listener = invocation.getArgument(0); // Get the listener argument
            listener.onComplete(mockQuerySnapshotTask); // Simulate the onComplete callback
            return null; // Void method, no return value
        }).when(mockEventRepository).getEventsByOrganizerUserId(any());

        // Mock the behavior for addEvent, updateEvent, and deleteEvent using doAnswer() for void methods
        doAnswer(invocation -> {
            OnCompleteListener<Object> listener = invocation.getArgument(1); // Get the listener argument
            listener.onComplete(mockTaskObject); // Simulate the onComplete callback
            return null; // Void method, no return value
        }).when(mockEventRepository).addEvent(any(), any());

        doAnswer(invocation -> {
            OnCompleteListener<Object> listener = invocation.getArgument(1); // Get the listener argument
            listener.onComplete(mockTaskObject); // Simulate the onComplete callback
            return null; // Void method, no return value
        }).when(mockEventRepository).updateEvent(any(), any());

        doAnswer(invocation -> {
            OnCompleteListener<Object> listener = invocation.getArgument(1); // Get the listener argument
            listener.onComplete(mockTaskObject); // Simulate the onComplete callback
            return null; // Void method, no return value
        }).when(mockEventRepository).deleteEvent(any(), any());
    }

    @Test
    public void testGetSelectedEvent() {
        eventViewModel.setSelectedEvent(event);
        assertEquals(event, eventViewModel.getSelectedEvent());
    }

    @Test
    public void testSetSelectedEvent() {
        eventViewModel.setSelectedEvent(event);
        assertNotNull(eventViewModel.getSelectedEvent());
        assertEquals("1", eventViewModel.getSelectedEvent().getEventId());
    }

    @Test
    public void testGetEvents() {
        ArrayList<Event> eventsList = eventViewModel.getEvents();
        assertNotNull(eventsList);
        assertTrue(eventsList.isEmpty());  // Should be empty initially
    }

    @Test
    public void testFetchEventsSuccess() {
        when(mockQuerySnapshotTask.isSuccessful()).thenReturn(true);
        when(mockQuerySnapshotTask.getResult()).thenReturn(mockQuerySnapshot);
        when(mockQuerySnapshot.toObjects(Event.class)).thenReturn(new ArrayList<>(List.of(event))); // Mock the list of events

        eventViewModel.fetchEvents(mockOnCompleteListener);

        verify(mockEventRepository, times(1)).getEventsByOrganizerUserId(any());
        verify(mockOnCompleteListener).onComplete(mockQuerySnapshotTask);
    }

    @Test
    public void testFetchEventsFailure() {
        when(mockQuerySnapshotTask.isSuccessful()).thenReturn(false);

        eventViewModel.fetchEvents(mockOnCompleteListener);

        verify(mockEventRepository, times(1)).getEventsByOrganizerUserId(any());
        verify(mockOnCompleteListener).onComplete(mockQuerySnapshotTask);
    }


    @Test
    public void testAddEvent() {
        when(mockTaskObject.isSuccessful()).thenReturn(true);
        eventViewModel.addEvent(event, mockOnCompleteListenerObject);

        verify(mockEventRepository, times(1)).addEvent(any(), any());
        verify(mockOnCompleteListenerObject).onComplete(mockTaskObject);
    }

    @Test
    public void testUpdateEvent() {
        when(mockTaskObject.isSuccessful()).thenReturn(true);
        eventViewModel.updateEvent(event, mockOnCompleteListenerObject);

        verify(mockEventRepository, times(1)).updateEvent(any(), any());
        verify(mockOnCompleteListenerObject).onComplete(mockTaskObject);
    }

    @Test
    public void testDeleteEvent() {
        // Create a mock Task<Void>
        Task<Void> mockVoidTask = mock(Task.class);

        // Create a mock listener specifically for void tasks
        OnCompleteListener<Void> mockOnCompleteListenerVoid = mock(OnCompleteListener.class);

        // Simulate a successful deletion
        when(mockVoidTask.isSuccessful()).thenReturn(true);

        // When the repository's deleteEvent is called, trigger the listener
        doAnswer(invocation -> {
            OnCompleteListener<Void> listener = invocation.getArgument(1);
            listener.onComplete(mockVoidTask); // Simulate the onComplete callback
            return null;
        }).when(mockEventRepository).deleteEvent(any(), any(OnCompleteListener.class));

        // Invoke the deleteEvent method
        eventViewModel.deleteEvent(event, mockOnCompleteListenerVoid);

        // Verify that deleteEvent was called once on the repository
        verify(mockEventRepository, times(1)).deleteEvent(eq(event.getEventId()), any(OnCompleteListener.class));

        // Verify that the onComplete method of the listener is called with the correct task type
        verify(mockOnCompleteListenerVoid).onComplete(mockVoidTask);
    }

    @Test
    public void testSelectRandomParticipant() {
        Event selectedEvent = eventViewModel.selectRandomParticipant(event);
        assertNull(selectedEvent);  // This is just a placeholder for actual random participant selection logic
    }

    @Test
    public void testHasAvailableSpots() {
        boolean availableSpots = eventViewModel.hasAvailableSpots(event);
        assertFalse(availableSpots);  // No available spots in this test case
    }
}

/**
 * Code Sources
 *
 * ChatGPT:
 * - "How do I structure Mockito test methods to handle different scenarios?"
 * - "What are best practices for handling Task completion in Firebase testing?"
 * - "Mockito best practices for verifying repository interactions."
 *
 * Stack Overflow:
 * - "How to mock Firestore queries and Task objects in JUnit tests." - https://stackoverflow.com/questions/57276718/how-to-mock-firestore-queries-for-unit-tests
 * - "JUnit vs AssertJ: Choosing assert methods in Android tests." - https://stackoverflow.com/questions/43988538/assertj-vs-junit-assertions-for-unit-testing-in-java
 *
 * Firebase Documentation:
 * - [Firestore Testing and Debugging](https://firebase.google.com/docs/firestore/extend-with-functions)
 *
 * Mockito Documentation:
 * - [ArgumentMatchers in Mockito](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/ArgumentMatchers.html)
 * - [Verification in Mockito](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#verification)
 */
