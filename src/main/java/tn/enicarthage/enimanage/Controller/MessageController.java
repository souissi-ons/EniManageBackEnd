package tn.enicarthage.enimanage.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.enicarthage.enimanage.Model.Message;
import tn.enicarthage.enimanage.repository.MessageRepository;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "http://localhost:4200")
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping
    public ResponseEntity<List<Message>> getAllMessages() {
        return ResponseEntity.ok(messageRepository.findAll());
    }

    // Ajouter un nouveau message
    @PostMapping
    public ResponseEntity<Message> addMessage(@RequestBody Message message) {
        // Validation
        if (message.getUserId() == null || message.getSender() == null || message.getContent() == null) {
            return ResponseEntity.badRequest().build();
        }

        // Set timestamp if not provided
        if (message.getTimestamp() == null) {
            message.setTimestamp(LocalDateTime.now());
        }

        // Save to database
        Message savedMessage = messageRepository.save(message);
        return ResponseEntity.ok(savedMessage);
    }
}