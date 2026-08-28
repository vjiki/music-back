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
import org.springframework.web.bind.annotation.RestController;

import com.vjiki.music.dto.ChatListItemResponse;
import com.vjiki.music.dto.ChatResponse;
import com.vjiki.music.dto.CreateChatRequest;
import com.vjiki.music.service.ChatListService;
import com.vjiki.music.service.ChatService;

/**
 * Instagram-style chat controller
 * Provides endpoints for managing chats
 */
@RestController
@RequestMapping("/api/v1/chats")
public class ChatController {

    private final ChatListService chatListService;
    private final ChatService chatService;

    public ChatController(ChatListService chatListService, ChatService chatService) {
        this.chatListService = chatListService;
        this.chatService = chatService;
    }

    /**
     * Get all chats for a user in Instagram-style format
     * Returns chat list with last message preview, participants, etc.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ChatListItemResponse>> getChatsForUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(chatListService.getChatListForUser(userId));
    }

    /**
     * Get a chat by ID
     * GET /api/v1/chats/{chatId}
     */
    @GetMapping("/{chatId}")
    public ResponseEntity<ChatResponse> getChatById(@PathVariable UUID chatId) {
        return ResponseEntity.ok(chatService.getChatById(chatId));
    }

    /**
     * Create a new chat
     * POST /api/v1/chats
     */
    @PostMapping
    public ResponseEntity<ChatResponse> createChat(@RequestBody CreateChatRequest request) {
        return ResponseEntity.ok(chatService.createChat(request));
    }

    /**
     * Delete a chat (hard delete - cascade deletes participants and messages)
     * DELETE /api/v1/chats/{chatId}
     */
    @DeleteMapping("/{chatId}")
    public ResponseEntity<Void> deleteChat(@PathVariable UUID chatId) {
        chatService.deleteChat(chatId);
        return ResponseEntity.ok().build();
    }
}
