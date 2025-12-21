package com.vjiki.music.service

import com.vjiki.music.dto.MessageResponse
import java.util.UUID

interface MessageService {
    fun getMessagesByChatId(chatId: UUID, userId1: UUID, userId2: UUID): List<MessageResponse>
}

