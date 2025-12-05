package com.vjiki.music.service

import com.vjiki.music.dto.ChatResponse
import java.util.UUID

interface ChatService {
    fun getChatsByUserId(userId: UUID): List<ChatResponse>
}

