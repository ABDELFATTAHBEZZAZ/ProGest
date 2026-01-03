package com.gestion.controller;

import com.gestion.dto.ChatMessage;
import com.gestion.service.FileStorageService;
import com.gestion.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final FileStorageService fileStorageService;

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<ChatMessage>> getMessagesByRoom(@PathVariable String roomId) {
        return ResponseEntity.ok(messageService.getMessagesByRoom(roomId));
    }

    @GetMapping("/conversation")
    public ResponseEntity<List<ChatMessage>> getConversation(
            @RequestParam Long userId1,
            @RequestParam Long userId2) {
        return ResponseEntity.ok(messageService.getConversation(userId1, userId2));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<ChatMessage>> getMessagesByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(messageService.getMessagesByClient(clientId));
    }

    @GetMapping("/devis/{devisId}")
    public ResponseEntity<List<ChatMessage>> getMessagesByDevis(@PathVariable Long devisId) {
        return ResponseEntity.ok(messageService.getMessagesByDevis(devisId));
    }

    @GetMapping("/facture/{factureId}")
    public ResponseEntity<List<ChatMessage>> getMessagesByFacture(@PathVariable Long factureId) {
        return ResponseEntity.ok(messageService.getMessagesByFacture(factureId));
    }

    @GetMapping("/unread/{userId}")
    public ResponseEntity<Long> getUnreadCount(@PathVariable Long userId) {
        return ResponseEntity.ok(messageService.getUnreadCount(userId));
    }

    @PostMapping("/{messageId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long messageId) {
        messageService.markAsRead(messageId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/read-all/{userId}")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long userId) {
        messageService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/upload/audio")
    public ResponseEntity<Map<String, String>> uploadAudio(@RequestParam("file") MultipartFile file) {
        String path = fileStorageService.storeAudio(file);
        return ResponseEntity.ok(Map.of("path", path));
    }

    @PostMapping("/upload/image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        String path = fileStorageService.storeImage(file);
        return ResponseEntity.ok(Map.of("path", path));
    }
}
