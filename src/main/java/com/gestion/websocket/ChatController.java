package com.gestion.websocket;

import com.gestion.dto.ChatMessage;
import com.gestion.entity.Message;
import com.gestion.entity.MessageType;
import com.gestion.service.MessageService;
import com.gestion.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final UserService userService;

    /**
     * Handle messages sent to a room (general chat, client-specific,
     * devis-specific, etc.)
     */
    @MessageMapping("/chat.sendMessage/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public ChatMessage sendMessage(@DestinationVariable String roomId, @Payload ChatMessage chatMessage) {
        chatMessage.setRoomId(roomId);
        if (chatMessage.getType() == null) {
            chatMessage.setType(MessageType.TEXT);
        }

        // Save message to database
        Message savedMessage = messageService.saveMessage(chatMessage);
        chatMessage.setId(savedMessage.getId());
        chatMessage.setTimestamp(savedMessage.getTimestamp());

        return chatMessage;
    }

    /**
     * Handle private messages between users
     */
    @MessageMapping("/chat.private")
    public void sendPrivateMessage(@Payload ChatMessage chatMessage) {
        if (chatMessage.getType() == null) {
            chatMessage.setType(MessageType.TEXT);
        }

        // Save message to database
        Message savedMessage = messageService.saveMessage(chatMessage);
        chatMessage.setId(savedMessage.getId());
        chatMessage.setTimestamp(savedMessage.getTimestamp());

        // Send to recipient
        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipientUsername(),
                "/queue/private",
                chatMessage);

        // Send confirmation to sender
        messagingTemplate.convertAndSendToUser(
                chatMessage.getSenderUsername(),
                "/queue/private",
                chatMessage);
    }

    /**
     * Handle user joining a room
     */
    @MessageMapping("/chat.addUser/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public ChatMessage addUser(@DestinationVariable String roomId,
            @Payload ChatMessage chatMessage,
            SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        String username = chatMessage.getSenderUsername();
        headerAccessor.getSessionAttributes().put("username", username);
        headerAccessor.getSessionAttributes().put("roomId", roomId);

        // Update user connected status
        userService.setUserConnected(username, true);

        chatMessage.setContent(username + " a rejoint le chat");
        chatMessage.setType(MessageType.TEXT);

        // Broadcast online users
        broadcastOnlineUsers();

        return chatMessage;
    }

    /**
     * Handle typing indicator
     */
    @MessageMapping("/chat.typing/{roomId}")
    @SendTo("/topic/room/{roomId}/typing")
    public ChatMessage userTyping(@DestinationVariable String roomId, @Payload ChatMessage chatMessage) {
        return chatMessage;
    }

    /**
     * Broadcast online users
     */
    public void broadcastOnlineUsers() {
        var connectedUsers = userService.getConnectedUsers();
        messagingTemplate.convertAndSend("/topic/users.online", connectedUsers);
    }
}
