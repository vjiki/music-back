package com.vjiki.music.controller

import com.vjiki.music.dto.MessageResponse
import com.vjiki.music.service.MessageService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * Instagram-style message controller
 * Provides endpoints for managing messages in chats
 */
@RestController
@RequestMapping("/api/v1/messages")
class MessageController(
    private val messageService: MessageService
) {

    /**
     * Get messages in a chat between two users
     * Returns all messages in chronological order (oldest first)
     */
    @GetMapping("/chat/{chatId}")
    fun getMessagesInChat(
        @PathVariable chatId: UUID,
        @RequestParam userId1: UUID,
        @RequestParam userId2: UUID
    ): ResponseEntity<List<MessageResponse>> {
        val messages = messageService.getMessagesByChatId(chatId, userId1, userId2)
        return ResponseEntity.ok(messages)
    }
}

