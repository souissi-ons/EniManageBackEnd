package tn.enicarthage.enimanage;

import org.junit.Test;
import tn.enicarthage.enimanage.Model.*;

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
}
