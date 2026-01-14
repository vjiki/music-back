package com.vjiki.music.entity

import java.io.Serializable
import java.util.UUID

data class ChatParticipantId(
    val chatId: UUID = UUID(0, 0),
    val userId: UUID = UUID(0, 0)
) : Serializable

