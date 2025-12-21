package com.vjiki.music.service

import com.vjiki.music.dto.ChatListItemResponse
import java.util.UUID

interface ChatListService {
    fun getChatListForUser(userId: UUID): List<ChatListItemResponse>
}

