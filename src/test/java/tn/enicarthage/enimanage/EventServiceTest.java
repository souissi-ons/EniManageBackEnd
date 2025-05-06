package tn.enicarthage.enimanage;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import tn.enicarthage.enimanage.DTO.EventDTO;
import tn.enicarthage.enimanage.DTO.ParticipantDTO;
import tn.enicarthage.enimanage.Model.*;
import tn.enicarthage.enimanage.repository.*;
import tn.enicarthage.enimanage.service.EventService;

import java.util.*;

public class EventServiceTest {

    private EventService eventService;

    @Mock
    private EventRepository eventRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private SalleRepository salleRepository;
    
    @Mock
    private ParticipantEventRepository participantEventRepository;
    
    @Mock
    private FeedbackRepository feedbackRepository;
    
    @Mock
    private EventResourceRepository eventResourceRepository;
    
    @Mock
    private ResourceRepository resourceRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        eventService = new EventService(
            eventRepository,
            userRepository,
            participantEventRepository,
            feedbackRepository,
            salleRepository,
            eventResourceRepository,
            resourceRepository
        );

        // Initialisation des données de test
        User creator = new User();
        creator.setId(1L);
        creator.setName("Test User");
        when(userRepository.findById(1L)).thenReturn(Optional.of(creator));

        Salle salle = new Salle();
        salle.setId(1L);
        salle.setCapacity(100);
        when(salleRepository.findById(1L)).thenReturn(Optional.of(salle));
    }

    @Test
    public void testGetAllEvents() {
        // Préparation
        Event event = createTestEvent();
        when(eventRepository.findAll()).thenReturn(Collections.singletonList(event));

        // Exécution
        List<Event> events = eventService.getAllEvents();

        // Vérification
        assertEquals(1, events.size());
        assertEquals("Test Event", events.get(0).getTitle());
    }

    @Test
    public void testGetEventById() {
        // Préparation
        Event event = createTestEvent();
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        // Exécution
        Optional<Event> foundEvent = eventService.getEventById(1L);

        // Vérification
        assertTrue(foundEvent.isPresent());
        assertEquals("Test Event", foundEvent.get().getTitle());
    }

    @Test
    public void testGetEventByIdNotFound() {
        // Préparation
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        // Exécution
        Optional<Event> foundEvent = eventService.getEventById(999L);

        // Vérification
        assertFalse(foundEvent.isPresent());
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
        when(eventRepository.save(any(Event.class))).thenReturn(createdEvent);

        // Exécution
        Event result = eventService.createEvent(eventDTO);

        // Vérification
        assertNotNull(result);
        assertEquals("Test Event", result.getTitle());
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    public void testGetEventsByStatus() {
        // Préparation
        Event event = createTestEvent();
        when(eventRepository.findByStatus(EventStatus.PENDING)).thenReturn(Collections.singletonList(event));

        // Exécution
        List<Event> events = eventService.getEventsByStatus(EventStatus.PENDING);

        // Vérification
        assertEquals(1, events.size());
        assertEquals(EventStatus.PENDING, events.get(0).getStatus());
    }

    @Test
    public void testUpdateEventStatus() {
        // Préparation
        Event event = createTestEvent();
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        // Exécution
        Event updatedEvent = eventService.updateEventStatus(1L, EventStatus.ACCEPTED);

        // Vérification
        assertEquals(EventStatus.ACCEPTED, updatedEvent.getStatus());
        verify(eventRepository).save(any(Event.class));
    }

    @Test(expected = RuntimeException.class)
    public void testUpdateEventStatusNotFound() {
        // Préparation
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        // Exécution
        eventService.updateEventStatus(999L, EventStatus.ACCEPTED);
    }

    @Test(expected = RuntimeException.class)
    public void testAttendEventAlreadyRegistered() {
        // Préparation
        Event event = createTestEvent();
        User user = new User();
        user.setId(2L);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(participantEventRepository.existsByEventAndUser(event, user)).thenReturn(true);

        // Exécution
        eventService.attendEvent(1L, 2L);
    }

    private Event createTestEvent() {
        User creator = new User();
        creator.setId(1L);
        creator.setName("Test User");

        Salle salle = new Salle();
        salle.setId(1L);
        salle.setCapacity(100);

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