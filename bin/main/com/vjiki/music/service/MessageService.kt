package com.vjiki.music.service

import com.vjiki.music.dto.CreateMessageRequest
import com.vjiki.music.dto.MessageReactionRequest
import com.vjiki.music.dto.MessageReactionResponse
import com.vjiki.music.dto.MessageResponse
import com.vjiki.music.pagination.CursorPageResponse
import java.util.UUID

interface MessageService {
    fun getMessagesByChatId(chatId: UUID, userId1: UUID, userId2: UUID): List<MessageResponse>
    fun getMessagesPage(chatId: UUID, limit: Int, cursor: String?): CursorPageResponse<MessageResponse>
    fun createMessage(request: CreateMessageRequest): MessageResponse
    fun deleteMessage(messageId: UUID)
    fun getMessageReactions(messageId: UUID): List<MessageReactionResponse>
    fun addReaction(request: MessageReactionRequest)
    fun removeReaction(messageId: UUID, userId: UUID, emoji: String)
    fun markAsRead(messageId: UUID, userId: UUID)
}

