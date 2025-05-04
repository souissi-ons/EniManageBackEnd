// EventService.java
package tn.enicarthage.enimanage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.enicarthage.enimanage.DTO.EventDTO;
import tn.enicarthage.enimanage.DTO.FeedbackDTO;
import tn.enicarthage.enimanage.DTO.ParticipantDTO;
import tn.enicarthage.enimanage.Model.*;
import tn.enicarthage.enimanage.repository.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
        private final EventRepository eventRepository;
        private final UserRepository userRepository;
        private final ParticipantEventRepository participantEventRepository;
        private final FeedbackRepository feedbackRepository;
        private final SalleRepository salleRepository;

        public List<Event> getAllEvents() {
                return eventRepository.findAll();
        }

        public Optional<Event> getEventById(Long id) {
                return eventRepository.findById(id);
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
                                .build();

                return eventRepository.save(event);
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
}