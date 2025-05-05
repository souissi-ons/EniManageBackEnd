package tn.enicarthage.enimanage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.enicarthage.enimanage.DTO.*;
import tn.enicarthage.enimanage.Model.*;
import tn.enicarthage.enimanage.repository.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
        private final EventRepository eventRepository;
        private final UserRepository userRepository;
        private final ParticipantEventRepository participantEventRepository;
        private final FeedbackRepository feedbackRepository;
        private final SalleRepository salleRepository;
        private final EventResourceRepository eventResourceRepository;
        private final ResourceRepository resourceRepository;

        public List<Event> getAllEvents() {
                return eventRepository.findAll();
        }

        public Optional<Event> getEventById(Long id) {
                return eventRepository.findById(id);
        }
        public Event checkAvailability(Event event) {
                List<Event> events = getAllEvents();
                List<Salle> allSalles = salleRepository.findAll(); // Assume this gives all salles
                Salle chosenSalle = event.getSalle();
                boolean isOccupied = false;

                for (Event e : events) {
                        if (e.getSalle().getId().equals(chosenSalle.getId())) {
                                boolean overlaps = event.getDateStart().before(e.getDateEnd()) &&
                                        event.getDateEnd().after(e.getDateStart());
                                if (overlaps) {
                                        isOccupied = true;
                                        break;
                                }
                        }
                }

                if (isOccupied) {
                        List<Salle> available = new ArrayList<>();
                        for (Salle salle : allSalles) {
                                boolean availableSalle = true;
                                for (Event e : events) {
                                        if (e.getSalle().getId().equals(salle.getId())) {
                                                boolean overlaps = event.getDateStart().before(e.getDateEnd()) &&
                                                        event.getDateEnd().after(e.getDateStart());
                                                if (overlaps) {
                                                        availableSalle = false;
                                                        break;
                                                }
                                        }
                                }
                                if (availableSalle) {
                                        available.add(salle);
                                }
                        }

                        // Try to find salle with enough capacity
                        for (Salle s : available) {
                                if (s.getCapacity() >= event.getCapacity()) {
                                        event.setSalle(s);
                                        return event;
                                }
                        }

                        // If none found, choose salle with max capacity
                        Salle maxSalle = available.stream()
                                .max(Comparator.comparing(Salle::getCapacity))
                                .orElse(null);

                        if (maxSalle != null) {
                                event.setSalle(maxSalle);
                        } else {
                                throw new IllegalStateException("No available salle for the given event timing");
                        }
                }

                return event;
        }
        public List<EventResource> ValidResources(Event event, List<EventResource> requestedResources) {
                List<EventResource> validResources = new ArrayList<>();
                List<EventResource> allTakenResources = eventResourceRepository.findAll();

                Date eventStart = event.getDateStart();
                Date eventEnd = event.getDateEnd();

                for (EventResource requested : requestedResources) {
                        Long resourceId = requested.getResource().getId();
                        int requestedQty = requested.getQuantity();

                        int totalQty = resourceRepository.findById(resourceId)
                                .orElseThrow().getQuantity();

                        int takenQty = allTakenResources.stream()
                                .filter(er -> er.getResource().getId().equals(resourceId))
                                .filter(er -> {
                                        Event e = er.getEvent();
                                        return eventStart.before(e.getDateEnd()) && eventEnd.after(e.getDateStart());
                                })
                                .mapToInt(EventResource::getQuantity)
                                .sum();

                        int availableQty = totalQty - takenQty;

                        if (availableQty >= requestedQty) {
                                validResources.add(requested); // full quantity available
                        } else if (availableQty > 0) {
                                requested.setQuantity(availableQty); // set to max available
                                validResources.add(requested);
                        }
                        // else skip adding this resource
                }

                return validResources;
        }
        public Event createEvent(EventDTO eventDTO) {
                User creator = userRepository.findById(eventDTO.getCreatorId())
                                .orElseThrow(() -> new RuntimeException("User not found"));
                Salle salle = salleRepository.findById(eventDTO.getSalleId())
                                .orElseThrow(() -> new RuntimeException("Salle not found"));

                Event event = Event.builder()
                                .title(eventDTO.getTitle())
                                .description(eventDTO.getDescription())
                                .dateStart(eventDTO.getDateStart())
                                .dateEnd(eventDTO.getDateEnd())
                                .isPrivate(eventDTO.isPrivate())
                                .capacity(eventDTO.getCapacity())
                                .status(eventDTO.getStatus())
                                .creator(creator)
                                .salle(salle)
                                .imageUrl(eventDTO.getImageUrl())
                                .resources(new ArrayList<>())
                                .build();
                event=checkAvailability(event);
                Event savedEvent = eventRepository.save(event);

                // Handle resources
                if (eventDTO.getResources() != null) {
                        List<EventResource> eventResources = new ArrayList<>();
                        for (var resourceDTO : eventDTO.getResources()) {
                                Resource resource = resourceRepository.findById(resourceDTO.getResourceId())
                                                .orElseThrow(() -> new RuntimeException("Resource not found: " + resourceDTO.getResourceId()));
                                EventResource eventResource = EventResource.builder()
                                                .event(savedEvent)
                                                .resource(resource)
                                                .quantity(resourceDTO.getQuantity())
                                                .build();
                                eventResources.add(eventResource);
                        }
                        eventResources=ValidResources(event,eventResources);
                        eventResourceRepository.saveAll(eventResources);
                        savedEvent.setResources(eventResources);
                }
                return savedEvent;
        }

        public Event updateEvent(Long id, EventDTO eventDTO) {
                Event existingEvent = eventRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Event not found"));
                User creator = userRepository.findById(eventDTO.getCreatorId())
                                .orElseThrow(() -> new RuntimeException("User not found"));
                Salle salle = salleRepository.findById(eventDTO.getSalleId())
                                .orElseThrow(() -> new RuntimeException("Salle not found"));

                existingEvent.setTitle(eventDTO.getTitle());
                existingEvent.setDescription(eventDTO.getDescription());
                existingEvent.setDateStart(eventDTO.getDateStart());
                existingEvent.setDateEnd(eventDTO.getDateEnd());
                existingEvent.setPrivate(eventDTO.isPrivate());
                existingEvent.setCapacity(eventDTO.getCapacity());
                existingEvent.setStatus(eventDTO.getStatus());
                existingEvent.setCreator(creator);
                existingEvent.setSalle(salle);
                if (eventDTO.getImageUrl() != null) {
                        existingEvent.setImageUrl(eventDTO.getImageUrl());
                }

                // Update resources
                if (eventDTO.getResources() != null) {
                        // Remove old resources
                        eventResourceRepository.deleteByEventId(existingEvent.getId());
                        List<EventResource> eventResources = new ArrayList<>();
                        for (var resourceDTO : eventDTO.getResources()) {
                                Resource resource = resourceRepository.findById(resourceDTO.getResourceId())
                                                .orElseThrow(() -> new RuntimeException("Resource not found: " + resourceDTO.getResourceId()));
                                EventResource eventResource = EventResource.builder()
                                                .event(existingEvent)
                                                .resource(resource)
                                                .quantity(resourceDTO.getQuantity())
                                                .build();
                                eventResources.add(eventResource);
                        }
                        eventResourceRepository.saveAll(eventResources);
                        existingEvent.setResources(eventResources);
                }

                return eventRepository.save(existingEvent);
        }

        public ParticipantDTO attendEvent(Long eventId, Long userId) {
                Event event = eventRepository.findById(eventId)
                                .orElseThrow(() -> new RuntimeException("Event not found"));
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                if (new Date().after(event.getDateStart())) {
                        throw new RuntimeException("Event has already started");
                }

                if (participantEventRepository.existsByEventAndUser(event, user)) {
                        throw new RuntimeException("User is already registered for this event");
                }

                ParticipantEvent participant = ParticipantEvent.builder()
                                .event(event)
                                .user(user)
                                .inscriptionDate(new Date())
                                .build();

                participantEventRepository.save(participant);

                return ParticipantDTO.builder()
                                .eventId(eventId)
                                .userId(userId)
                                .inscriptionDate(new Date())
                                .build();
        }
        public List<Event> getEventsByStatus(EventStatus status) {
                return eventRepository.findByStatus(status);
        }

        public Event updateEventStatus(Long id, EventStatus status) {
                Event event = eventRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Event not found"));
                event.setStatus(status);
                return eventRepository.save(event);
        }
        public List<AddEventDTO> getAllEventsWithDetails() {
                List<Event> events = eventRepository.findAll();
                return events.stream().map(event -> {
                        // Map all necessary fields including creator, salle, and resources
                        List<EventResourceDetailsDTO> resources = eventResourceRepository.findByEventId(event.getId()).stream()
                                .map(resource -> {
                                        Resource res = resource.getResource();
                                        return EventResourceDetailsDTO.builder()
                                                .id(resource.getId())
                                                .name(res.getName())
                                                .quantity(resource.getQuantity())
                                                .build();
                                })
                                .collect(Collectors.toList());

                        return AddEventDTO.builder()
                                .id(event.getId())
                                .title(event.getTitle())
                                .description(event.getDescription())
                                .dateStart(event.getDateStart())
                                .dateEnd(event.getDateEnd())
                                .isPrivate(event.isPrivate())
                                .capacity(event.getCapacity())
                                .status(event.getStatus())
                                .creatorName(event.getCreator().getName())
                                .phone(event.getCreator().getPhoneNumber())
                                .email(event.getCreator().getEmail())
                                .salleId(event.getSalle().getId())
                                .imageUrl(event.getImageUrl())
                                .logoUrl(event.getImageUrl())
                                .resources(resources)
                                .build();
                }).collect(Collectors.toList());
        }
        public FeedbackDTO addFeedback(FeedbackDTO feedbackDTO) {
                Event event = eventRepository.findById(feedbackDTO.getEventId())
                                .orElseThrow(() -> new RuntimeException("Event not found"));
                User user = userRepository.findById(feedbackDTO.getUserId())
                                .orElseThrow(() -> new RuntimeException("User not found"));

                if (new Date().before(event.getDateEnd())) {
                        throw new RuntimeException("Event is not finished yet");
                }

                Feedback feedback = Feedback.builder()
                                .event(event)
                                .user(user)
                                .comment(feedbackDTO.getComment())
                                .creationDate(new Date())
                                .build();

                feedbackRepository.save(feedback);

                return FeedbackDTO.builder()
                                .eventId(feedbackDTO.getEventId())
                                .userId(feedbackDTO.getUserId())
                                .comment(feedbackDTO.getComment())
                                .creationDate(new Date())
                                .build();
        }

        public List<ParticipantDTO> getEventParticipants(Long eventId) {
                return participantEventRepository.findByEventId(eventId).stream()
                                .map(p -> ParticipantDTO.builder()
                                                .id(p.getId())
                                                .eventId(p.getEvent().getId())
                                                .userId(p.getUser().getId())
                                                .inscriptionDate(p.getInscriptionDate())
                                                .build())
                                .collect(Collectors.toList());
        }

        public List<FeedbackDTO> getEventFeedbacks(Long eventId) {
                return feedbackRepository.findByEventId(eventId).stream()
                                .map(f -> FeedbackDTO.builder()
                                                .id(f.getId())
                                                .eventId(f.getEvent().getId())
                                                .userId(f.getUser().getId())
                                                .comment(f.getComment())
                                                .creationDate(f.getCreationDate())
                                                .build())
                                .collect(Collectors.toList());
        }

        public List<EventResourceDTO> getEventResources(Long eventId) {
                // Implémentez la logique pour récupérer les ressources de l'événement
                // Par exemple :
                return eventResourceRepository.findByEventId(eventId)
                        .stream()
                        .map(resource -> EventResourceDTO.builder()
                                .resourceId(resource.getId())
                                .resourceName(resource.getResource().getName())
                                .quantity(resource.getQuantity())
                                .build())
                        .collect(Collectors.toList());
        }
}