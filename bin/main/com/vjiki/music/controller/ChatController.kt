package com.vjiki.music.controller

import com.vjiki.music.dto.ChatListItemResponse
import com.vjiki.music.dto.ChatResponse
import com.vjiki.music.dto.CreateChatRequest
import com.vjiki.music.service.ChatListService
import com.vjiki.music.service.ChatService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * Instagram-style chat controller
 * Provides endpoints for managing chats
 */
@RestController
@RequestMapping("/api/v1/chats")
class ChatController(
    private val chatListService: ChatListService,
    private val chatService: ChatService
) {

    /**
     * Get all chats for a user in Instagram-style format
     * Returns chat list with last message preview, participants, etc.
     */
    @GetMapping("/user/{userId}")
    fun getChatsForUser(@PathVariable userId: UUID): ResponseEntity<List<ChatListItemResponse>> {
        val chats = chatListService.getChatListForUser(userId)
        return ResponseEntity.ok(chats)
    }

    /**
     * Get a chat by ID
     * GET /api/v1/chats/{chatId}
     */
    @GetMapping("/{chatId}")
    fun getChatById(@PathVariable chatId: UUID): ResponseEntity<ChatResponse> {
        val chat = chatService.getChatById(chatId)
        return ResponseEntity.ok(chat)
    }

    /**
     * Create a new chat
     * POST /api/v1/chats
     */
    @PostMapping
    fun createChat(@RequestBody request: CreateChatRequest): ResponseEntity<ChatResponse> {
        val chat = chatService.createChat(request)
        return ResponseEntity.ok(chat)
    }

    /**
     * Delete a chat (hard delete - cascade deletes participants and messages)
     * DELETE /api/v1/chats/{chatId}
     */
    @DeleteMapping("/{chatId}")
    fun deleteChat(@PathVariable chatId: UUID): ResponseEntity<Void> {
        chatService.deleteChat(chatId)
        return ResponseEntity.ok().build()
    }
}

