package com.vjiki.music.entity

import java.io.Serializable
import java.util.UUID

data class ChatParticipantId(
    val chatId: UUID,
    val userId: UUID
) : Serializable

