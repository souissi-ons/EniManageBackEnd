// EventController.java
package tn.enicarthage.enimanage.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import tn.enicarthage.enimanage.DTO.*;
import tn.enicarthage.enimanage.Model.Event;
import tn.enicarthage.enimanage.Model.EventStatus;
import tn.enicarthage.enimanage.service.EventService;
import tn.enicarthage.enimanage.service.FileStorageService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/events")
@RequiredArgsConstructor

public class EventController {
    private final EventService eventService;
    private final FileStorageService fileStorageService;

    @GetMapping
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        List<Event> events = eventService.getAllEvents();
        List<EventDTO> eventDTOs = events.stream()
                .map(event -> EventDTO.builder()
                        .id(event.getId())
                        .title(event.getTitle())
                        .description(event.getDescription())
                        .dateStart(event.getDateStart())
                        .dateEnd(event.getDateEnd())
                        .isPrivate(event.isPrivate())
                        .capacity(event.getCapacity())
                        .status(event.getStatus())
                        .creatorId(event.getCreator().getId())
                        .salleId(event.getSalle().getId())
                        .imageUrl(event.getImageUrl())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(eventDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable Long id) {
        Event event = eventService.getEventById(id).orElse(null);
        if (event == null) {
            return ResponseEntity.notFound().build();
        }
        EventDTO eventDTO = EventDTO.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .dateStart(event.getDateStart())
                .dateEnd(event.getDateEnd())
                .isPrivate(event.isPrivate())
                .capacity(event.getCapacity())
                .status(event.getStatus())
                .creatorId(event.getCreator().getId())
                .salleId(event.getSalle().getId())
                .imageUrl(event.getImageUrl())
                .build();
        return ResponseEntity.ok(eventDTO);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Event> createEvent(
            @RequestPart @Validated EventDTO eventDTO,
            @RequestPart(required = false) MultipartFile image) {
        if (image != null && !image.isEmpty()) {
            try {
                String fileName = fileStorageService.storeFile(image);
                eventDTO.setImageUrl(fileName);
            } catch (IOException e) {
                throw new RuntimeException("Erreur lors du traitement du fichier image", e);
            }
        }
        return ResponseEntity.ok(eventService.createEvent(eventDTO));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Event> updateEvent(
            @PathVariable Long id,
            @RequestPart @Validated EventDTO eventDTO,
            @RequestPart(required = false) MultipartFile image) {
        if (image != null && !image.isEmpty()) {
            try {
                String fileName = fileStorageService.storeFile(image);
                eventDTO.setImageUrl(fileName);
            } catch (IOException e) {
                throw new RuntimeException("Erreur lors du traitement du fichier image", e);
            }
        }
        return ResponseEntity.ok(eventService.updateEvent(id, eventDTO));
    }

    @PostMapping("/{eventId}/attend/{userId}")
    public ResponseEntity<ParticipantDTO> attendEvent(
            @PathVariable Long eventId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(eventService.attendEvent(eventId, userId));
    }
    @GetMapping("/pending")
    public ResponseEntity<List<EventDTO>> getPendingEvents() {
        List<Event> events = eventService.getEventsByStatus(EventStatus.PENDING);
        List<EventDTO> eventDTOs = events.stream()
                .map(this::convertToDTO)  // Using the method reference now
                .collect(Collectors.toList());
        return ResponseEntity.ok(eventDTOs);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Event> updateEventStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        EventStatus status = EventStatus.valueOf(request.get("status").toUpperCase());
        return ResponseEntity.ok(eventService.updateEventStatus(id, status));
    }
    @PostMapping("/feedback")
    public ResponseEntity<FeedbackDTO> addFeedback(@RequestBody FeedbackDTO feedbackDTO) {
        return ResponseEntity.ok(eventService.addFeedback(feedbackDTO));
    }
    // EventController.java
    private EventDTO convertToDTO(Event event) {
        return EventDTO.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .dateStart(event.getDateStart())
                .dateEnd(event.getDateEnd())
                .isPrivate(event.isPrivate())
                .capacity(event.getCapacity())
                .status(event.getStatus())
                .creatorId(event.getCreator().getId())
                .salleId(event.getSalle().getId())
                .imageUrl(event.getImageUrl())
                .build();
    }

    @GetMapping("/{eventId}/participants")
    public ResponseEntity<List<ParticipantDTO>> getEventParticipants(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.getEventParticipants(eventId));
    }

    @GetMapping("/{eventId}/feedbacks")
    public ResponseEntity<List<FeedbackDTO>> getEventFeedbacks(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.getEventFeedbacks(eventId));
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<byte[]> serveFile(@PathVariable String filename) {
        try {
            Path file = fileStorageService.load(filename);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(Files.readAllBytes(file));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image non trouvée");
        }
    }
    @GetMapping("/all-details")
    public ResponseEntity<List<AddEventDTO>> getAllEventsWithDetails() {
        return ResponseEntity.ok(eventService.getAllEventsWithDetails());
    }

    @GetMapping("/{eventId}/resources")
    public ResponseEntity<List<EventResourceDTO>> getEventResources(@PathVariable Long eventId) {
        List<EventResourceDTO> resources = eventService.getEventResources(eventId);
        return ResponseEntity.ok(resources);
    }
}