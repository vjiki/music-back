package com.vjiki.music.dto

import java.util.UUID

data class MessageReactionRequest(
    val messageId: UUID,
    val userId: UUID,
    val emoji: String
)
