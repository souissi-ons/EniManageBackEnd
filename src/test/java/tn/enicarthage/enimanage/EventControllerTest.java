package tn.enicarthage.enimanage;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import tn.enicarthage.enimanage.Controller.EventController;
import tn.enicarthage.enimanage.DTO.EventDTO;
import tn.enicarthage.enimanage.Model.*;
import tn.enicarthage.enimanage.service.EventService;
import tn.enicarthage.enimanage.service.FileStorageService;

import org.springframework.http.ResponseEntity;
import java.util.*;

public class EventControllerTest {

    private EventController eventController;
    
    @Mock
    private EventService eventService;
    
    @Mock
    private FileStorageService fileStorageService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        eventController = new EventController(eventService, fileStorageService);
    }

    @Test
    public void testGetAllEvents() {
        // Préparation
        List<Event> events = new ArrayList<>();
        Event event = createTestEvent();
        events.add(event);
        
        when(eventService.getAllEvents()).thenReturn(events);

        // Exécution
        ResponseEntity<List<EventDTO>> response = eventController.getAllEvents();

        // Vérification
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Event", response.getBody().get(0).getTitle());
    }

    @Test
    public void testGetEventById() {
        // Préparation
        Event event = createTestEvent();
        when(eventService.getEventById(1L)).thenReturn(Optional.of(event));

        // Exécution
        ResponseEntity<EventDTO> response = eventController.getEventById(1L);

        // Vérification
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Test Event", response.getBody().getTitle());
    }

    @Test
    public void testGetEventByIdNotFound() {
        // Préparation
        when(eventService.getEventById(999L)).thenReturn(Optional.empty());

        // Exécution
        ResponseEntity<EventDTO> response = eventController.getEventById(999L);

        // Vérification
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testCreateEvent() {
        // Préparation
        EventDTO eventDTO = EventDTO.builder()
                .title("New Event")
                .description("Description")
                .dateStart(new Date())
                .dateEnd(new Date(System.currentTimeMillis() + 3600000))
                .isPrivate(false)
                .capacity(50)
                .status(EventStatus.PENDING)
                .creatorId(1L)
                .salleId(1L)
                .build();

        Event createdEvent = createTestEvent();
        when(eventService.createEvent(any(EventDTO.class))).thenReturn(createdEvent);

        // Exécution
        ResponseEntity<Event> response = eventController.createEvent(eventDTO, null);

        // Vérification
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Test Event", response.getBody().getTitle());
    }

    private Event createTestEvent() {
        User creator = new User();
        creator.setId(1L);

        Salle salle = new Salle();
        salle.setId(1L);

        Event event = new Event();
        event.setId(1L);
        event.setTitle("Test Event");
        event.setDescription("Description");
        event.setDateStart(new Date());
        event.setDateEnd(new Date(System.currentTimeMillis() + 3600000));
        event.setPrivate(false);
        event.setCapacity(50);
        event.setStatus(EventStatus.PENDING);
        event.setCreator(creator);
        event.setSalle(salle);

        return event;
    }
}