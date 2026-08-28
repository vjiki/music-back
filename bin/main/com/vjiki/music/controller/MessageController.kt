package com.vjiki.music.controller

import com.vjiki.music.dto.CreateMessageRequest
import com.vjiki.music.dto.MessageReactionRequest
import com.vjiki.music.dto.MessageReactionResponse
import com.vjiki.music.dto.MessageResponse
import com.vjiki.music.pagination.CursorPageResponse
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

    /**
     * Get messages in a chat with cursor-based pagination
     * GET /api/v1/messages/chat/{chatId}/page?limit=20&cursor={cursor}
     */
    @GetMapping("/chat/{chatId}/page")
    fun getMessagesPage(
        @PathVariable chatId: UUID,
        @RequestParam(required = false, defaultValue = "20") limit: Int,
        @RequestParam(required = false) cursor: String?
    ): ResponseEntity<CursorPageResponse<MessageResponse>> {
        val page = messageService.getMessagesPage(chatId, limit, cursor)
        return ResponseEntity.ok(page)
    }

    /**
     * Create a new message
     * POST /api/v1/messages
     */
    @PostMapping
    fun createMessage(@RequestBody request: CreateMessageRequest): ResponseEntity<MessageResponse> {
        val message = messageService.createMessage(request)
        return ResponseEntity.ok(message)
    }

    /**
     * Delete a message (soft delete)
     * DELETE /api/v1/messages/{messageId}
     */
    @DeleteMapping("/{messageId}")
    fun deleteMessage(@PathVariable messageId: UUID): ResponseEntity<Void> {
        messageService.deleteMessage(messageId)
        return ResponseEntity.ok().build()
    }

    /**
     * Get reactions for a message
     * GET /api/v1/messages/{messageId}/reactions
     */
    @GetMapping("/{messageId}/reactions")
    fun getMessageReactions(@PathVariable messageId: UUID): ResponseEntity<List<MessageReactionResponse>> {
        val reactions = messageService.getMessageReactions(messageId)
        return ResponseEntity.ok(reactions)
    }

    /**
     * Add a reaction to a message
     * POST /api/v1/messages/reactions
     */
    @PostMapping("/reactions")
    fun addReaction(@RequestBody request: MessageReactionRequest): ResponseEntity<Void> {
        messageService.addReaction(request)
        return ResponseEntity.ok().build()
    }

    /**
     * Remove a reaction from a message
     * DELETE /api/v1/messages/{messageId}/reactions/{userId}/{emoji}
     */
    @DeleteMapping("/{messageId}/reactions/{userId}/{emoji}")
    fun removeReaction(
        @PathVariable messageId: UUID,
        @PathVariable userId: UUID,
        @PathVariable emoji: String
    ): ResponseEntity<Void> {
        messageService.removeReaction(messageId, userId, emoji)
        return ResponseEntity.ok().build()
    }

    /**
     * Mark a message as read
     * POST /api/v1/messages/{messageId}/read/{userId}
     */
    @PostMapping("/{messageId}/read/{userId}")
    fun markAsRead(
        @PathVariable messageId: UUID,
        @PathVariable userId: UUID
    ): ResponseEntity<Void> {
        messageService.markAsRead(messageId, userId)
        return ResponseEntity.ok().build()
    }
}

