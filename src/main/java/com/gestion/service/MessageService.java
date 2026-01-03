package com.gestion.service;

import com.gestion.dto.ChatMessage;
import com.gestion.entity.*;
import com.gestion.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserService userService;
    private final ClientService clientService;
    private final DevisService devisService;
    private final FactureService factureService;

    public List<ChatMessage> getMessagesByRoom(String roomId) {
        return messageRepository.findByRoomIdOrderByTimestampAsc(roomId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ChatMessage> getConversation(Long userId1, Long userId2) {
        return messageRepository.findConversation(userId1, userId2)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ChatMessage> getMessagesByClient(Long clientId) {
        return messageRepository.findByClientIdOrderByTimestampAsc(clientId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ChatMessage> getMessagesByDevis(Long devisId) {
        return messageRepository.findByDevisIdOrderByTimestampAsc(devisId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ChatMessage> getMessagesByFacture(Long factureId) {
        return messageRepository.findByFactureIdOrderByTimestampAsc(factureId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Long getUnreadCount(Long userId) {
        return messageRepository.countUnreadMessages(userId);
    }

    @Transactional
    public Message saveMessage(ChatMessage chatMessage) {
        User sender = userService.getUserById(chatMessage.getSenderId());

        Message message = Message.builder()
                .sender(sender)
                .type(chatMessage.getType() != null ? chatMessage.getType() : MessageType.TEXT)
                .content(chatMessage.getContent())
                .audioPath(chatMessage.getAudioPath())
                .imagePath(chatMessage.getImagePath())
                .roomId(chatMessage.getRoomId())
                .build();

        if (chatMessage.getRecipientId() != null) {
            message.setRecipient(userService.getUserById(chatMessage.getRecipientId()));
        }

        if (chatMessage.getClientId() != null) {
            message.setClient(clientService.getClientById(chatMessage.getClientId()));
        }

        if (chatMessage.getDevisId() != null) {
            message.setDevis(devisService.getDevisById(chatMessage.getDevisId()));
        }

        if (chatMessage.getFactureId() != null) {
            message.setFacture(factureService.getFactureById(chatMessage.getFactureId()));
        }

        return messageRepository.save(message);
    }

    @Transactional
    public void markAsRead(Long messageId) {
        messageRepository.findById(messageId).ifPresent(message -> {
            message.setRead(true);
            messageRepository.save(message);
        });
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<Message> unread = messageRepository.findByRecipientIdAndIsReadFalse(userId);
        unread.forEach(message -> message.setRead(true));
        messageRepository.saveAll(unread);
    }

    private ChatMessage toDTO(Message message) {
        return ChatMessage.builder()
                .id(message.getId())
                .senderId(message.getSender().getId())
                .senderUsername(message.getSender().getUsername())
                .recipientId(message.getRecipient() != null ? message.getRecipient().getId() : null)
                .recipientUsername(message.getRecipient() != null ? message.getRecipient().getUsername() : null)
                .clientId(message.getClient() != null ? message.getClient().getId() : null)
                .devisId(message.getDevis() != null ? message.getDevis().getId() : null)
                .factureId(message.getFacture() != null ? message.getFacture().getId() : null)
                .roomId(message.getRoomId())
                .type(message.getType())
                .content(message.getContent())
                .audioPath(message.getAudioPath())
                .imagePath(message.getImagePath())
                .timestamp(message.getTimestamp())
                .isRead(message.isRead())
                .build();
    }
}
