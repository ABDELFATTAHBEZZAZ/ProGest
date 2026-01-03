package com.gestion.dto;

import com.gestion.entity.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private Long id;
    private String senderUsername;
    private Long senderId;
    private String recipientUsername;
    private Long recipientId;
    private Long clientId;
    private Long devisId;
    private Long factureId;
    private String roomId;
    private MessageType type;
    private String content;
    private String audioPath;
    private String imagePath;
    private LocalDateTime timestamp;
    private boolean isRead;
}
