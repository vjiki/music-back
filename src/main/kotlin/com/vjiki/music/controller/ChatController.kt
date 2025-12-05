package com.vjiki.music.controller

import com.vjiki.music.dto.ChatListItemResponse
import com.vjiki.music.service.ChatListService
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
    private val chatListService: ChatListService
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
}

