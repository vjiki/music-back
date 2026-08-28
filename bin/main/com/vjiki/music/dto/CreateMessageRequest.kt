package com.vjiki.music.dto

import java.util.UUID

data class CreateMessageRequest(
    val chatId: UUID,
    val senderId: UUID,
    val content: String? = null,
    val replyToId: UUID? = null,
    val messageType: String = "TEXT",
    val songId: UUID? = null,
    val attachmentCount: Int = 0
)
