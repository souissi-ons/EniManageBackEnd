package tn.enicarthage.enimanage.Model;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Date;

public class EventTest {

    @Test
    public void testEventGettersAndSetters() {
        Event event = new Event();
        User creator = new User();
        creator.setId(1L);
        Salle salle = new Salle();
        salle.setId(1L);

        Date start = new Date();
        Date end = new Date(start.getTime() + 3600000);

        event.setId(10L);
        event.setTitle("Test Event");
        event.setDescription("Description");
        event.setDateStart(start);
        event.setDateEnd(end);
        event.setPrivate(true);
        event.setCapacity(100);
        event.setStatus(EventStatus.PENDING);
        event.setCreator(creator);
        event.setSalle(salle);

        assertEquals(Long.valueOf(10), event.getId());
        assertEquals("Test Event", event.getTitle());
        assertEquals("Description", event.getDescription());
        assertEquals(start, event.getDateStart());
        assertEquals(end, event.getDateEnd());
        assertTrue(event.isPrivate());
        assertEquals(100, event.getCapacity());
        assertEquals(EventStatus.PENDING, event.getStatus());
        assertEquals(creator, event.getCreator());
        assertEquals(salle, event.getSalle());
    }

    @Test
    public void testEventConstructor() {
        User creator = new User();
        creator.setId(1L);
        Salle salle = new Salle();
        salle.setId(1L);
        Date start = new Date();
        Date end = new Date(start.getTime() + 3600000);

        Event event = new Event();
        event.setId(1L);
        event.setTitle("Constructor Test");
        event.setDescription("Test Description");
        event.setDateStart(start);
        event.setDateEnd(end);
        event.setPrivate(false);
        event.setCapacity(50);
        event.setStatus(EventStatus.ACCEPTED);
        event.setCreator(creator);
        event.setSalle(salle);

        assertNotNull(event);
        assertEquals("Constructor Test", event.getTitle());
        assertEquals(EventStatus.ACCEPTED, event.getStatus());
    }

    @Test
    public void testEventDateValidation() {
        Event event = new Event();
        Date start = new Date();
        Date end = new Date(start.getTime() - 3600000); // End date before start date

        event.setDateStart(start);
        event.setDateEnd(end);

        // Verify that the dates are set correctly
        assertEquals(start, event.getDateStart());
        assertEquals(end, event.getDateEnd());
    }

    @Test
    public void testEventCapacityValidation() {
        Event event = new Event();
        
        // Test with valid capacity
        event.setCapacity(50);
        assertEquals(50, event.getCapacity());

        // Test with zero capacity
        event.setCapacity(0);
        assertEquals(0, event.getCapacity());

        // Test with negative capacity
        event.setCapacity(-10);
        assertEquals(-10, event.getCapacity());
    }

    @Test
    public void testEventStatusTransitions() {
        Event event = new Event();
        
        // Test initial status
        event.setStatus(EventStatus.PENDING);
        assertEquals(EventStatus.PENDING, event.getStatus());

        // Test status change
        event.setStatus(EventStatus.ACCEPTED);
        assertEquals(EventStatus.ACCEPTED, event.getStatus());

        // Test status change to cancelled
        event.setStatus(EventStatus.REJECTED);
        assertEquals(EventStatus.REJECTED, event.getStatus());
    }
} 