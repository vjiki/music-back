package com.vjiki.music.service

import com.vjiki.music.dto.ChatResponse
import com.vjiki.music.dto.CreateChatRequest
import java.util.UUID

interface ChatService {
    fun getChatsByUserId(userId: UUID): List<ChatResponse>
    fun getChatById(chatId: UUID): ChatResponse
    fun createChat(request: CreateChatRequest): ChatResponse
    fun deleteChat(chatId: UUID)
}

