package com.vjiki.music.controller;

import com.vjiki.music.dto.MessageResponse;
import com.vjiki.music.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Instagram-style message controller
 * Provides endpoints for managing messages in chats
 */
@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Get messages in a chat between two users
     * Returns all messages in chronological order (oldest first)
     */
    @GetMapping("/chat/{chatId}")
    public ResponseEntity<List<MessageResponse>> getMessagesInChat(
            @PathVariable UUID chatId,
            @RequestParam UUID userId1,
            @RequestParam UUID userId2) {
        List<MessageResponse> messages = messageService.getMessagesByChatId(chatId, userId1, userId2);
        return ResponseEntity.ok(messages);
    }
}

