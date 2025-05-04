package tn.enicarthage.enimanage.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import tn.enicarthage.enimanage.Model.Message;
import tn.enicarthage.enimanage.repository.MessageRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final CopyOnWriteArraySet<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    private final ObjectMapper objectMapper;
    private final MessageRepository messageRepository;

    public ChatWebSocketHandler(ObjectMapper objectMapper, MessageRepository messageRepository) {
        this.objectMapper = objectMapper;
        this.messageRepository = messageRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        log.info("WebSocket connection established: {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("Message received: {}", payload);

        try {
            // Parse the message
            Message chatMessage = objectMapper.readValue(payload, Message.class);

            // Save to database first
            if (chatMessage.getTimestamp() == null) {
                chatMessage.setTimestamp(LocalDateTime.now());
            }
            Message savedMessage = messageRepository.save(chatMessage);

            // Convert saved message back to JSON with any DB-generated fields
            String savedMessageJson = objectMapper.writeValueAsString(savedMessage);

            // Broadcast to all sessions including sender
            broadcastToAllSessions(savedMessageJson);

            log.info("Message processed and broadcast successfully");
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage(), e);
            // Send error back to sender
            session.sendMessage(new TextMessage("{\"error\":\"Error processing message\"}"));
        }
    }

    private void broadcastToAllSessions(String jsonMessage) {
        log.info("Broadcasting message to {} sessions", sessions.size());

        sessions.forEach(session -> {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(jsonMessage));
                    log.debug("Message sent to session: {}", session.getId());
                } catch (IOException e) {
                    log.error("Error sending message to session {}: {}", session.getId(), e.getMessage(), e);
                    sessions.remove(session);
                }
            } else {
                log.warn("Session {} is closed, removing from sessions list", session.getId());
                sessions.remove(session);
            }
        });
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        sessions.remove(session);
        log.info("WebSocket connection closed: {}", session.getId());
    }
}