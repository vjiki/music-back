package com.vjiki.music.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vjiki.music.dto.CreateMessageRequest;
import com.vjiki.music.dto.MessageReactionRequest;
import com.vjiki.music.dto.MessageReactionResponse;
import com.vjiki.music.dto.MessageResponse;
import com.vjiki.music.pagination.CursorPageResponse;
import com.vjiki.music.service.MessageService;

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
        return ResponseEntity.ok(messageService.getMessagesByChatId(chatId, userId1, userId2));
    }

    /**
     * Get messages in a chat with cursor-based pagination
     * GET /api/v1/messages/chat/{chatId}/page?limit=20&cursor={cursor}
     */
    @GetMapping("/chat/{chatId}/page")
    public ResponseEntity<CursorPageResponse<MessageResponse>> getMessagesPage(
            @PathVariable UUID chatId,
            @RequestParam(required = false, defaultValue = "20") int limit,
            @RequestParam(required = false) String cursor) {
        return ResponseEntity.ok(messageService.getMessagesPage(chatId, limit, cursor));
    }

    /**
     * Create a new message
     * POST /api/v1/messages
     */
    @PostMapping
    public ResponseEntity<MessageResponse> createMessage(@RequestBody CreateMessageRequest request) {
        return ResponseEntity.ok(messageService.createMessage(request));
    }

    /**
     * Delete a message (soft delete)
     * DELETE /api/v1/messages/{messageId}
     */
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable UUID messageId) {
        messageService.deleteMessage(messageId);
        return ResponseEntity.ok().build();
    }

    /**
     * Get reactions for a message
     * GET /api/v1/messages/{messageId}/reactions
     */
    @GetMapping("/{messageId}/reactions")
    public ResponseEntity<List<MessageReactionResponse>> getMessageReactions(@PathVariable UUID messageId) {
        return ResponseEntity.ok(messageService.getMessageReactions(messageId));
    }

    /**
     * Add a reaction to a message
     * POST /api/v1/messages/reactions
     */
    @PostMapping("/reactions")
    public ResponseEntity<Void> addReaction(@RequestBody MessageReactionRequest request) {
        messageService.addReaction(request);
        return ResponseEntity.ok().build();
    }

    /**
     * Remove a reaction from a message
     * DELETE /api/v1/messages/{messageId}/reactions/{userId}/{emoji}
     */
    @DeleteMapping("/{messageId}/reactions/{userId}/{emoji}")
    public ResponseEntity<Void> removeReaction(
            @PathVariable UUID messageId,
            @PathVariable UUID userId,
            @PathVariable String emoji) {
        messageService.removeReaction(messageId, userId, emoji);
        return ResponseEntity.ok().build();
    }

    /**
     * Mark a message as read
     * POST /api/v1/messages/{messageId}/read/{userId}
     */
    @PostMapping("/{messageId}/read/{userId}")
    public ResponseEntity<Void> markAsRead(
            @PathVariable UUID messageId,
            @PathVariable UUID userId) {
        messageService.markAsRead(messageId, userId);
        return ResponseEntity.ok().build();
    }
}
