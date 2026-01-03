package com.gestion.websocket;

import com.gestion.repository.UserRepository;
import com.gestion.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final UserService userService;

    // Track active sessions
    private final Map<String, String> sessionUsers = new ConcurrentHashMap<>();

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info("Nouvelle connexion WebSocket");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String) headerAccessor.getSessionAttributes().get("username");
        String sessionId = headerAccessor.getSessionId();

        if (username != null) {
            log.info("Utilisateur déconnecté: {}", username);

            // Update user connection status
            userService.setUserConnected(username, false);

            // Remove from session tracking
            sessionUsers.remove(sessionId);

            // Notify others about user leaving
            Map<String, Object> message = Map.of(
                    "type", "LEAVE",
                    "username", username,
                    "content", username + " a quitté le chat");

            String roomId = (String) headerAccessor.getSessionAttributes().get("roomId");
            if (roomId != null) {
                messagingTemplate.convertAndSend("/topic/room/" + roomId, message);
            }

            // Broadcast updated online users list
            broadcastOnlineUsers();
        }
    }

    public void registerUser(String sessionId, String username) {
        sessionUsers.put(sessionId, username);
        userService.setUserConnected(username, true);
        broadcastOnlineUsers();
    }

    private void broadcastOnlineUsers() {
        var connectedUsers = userService.getConnectedUsers();
        messagingTemplate.convertAndSend("/topic/users.online", connectedUsers);
    }
}
