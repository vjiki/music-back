package com.vjiki.music.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime
import java.util.UUID

data class MessageReactionResponse(
    val messageId: UUID,
    val userId: UUID,
    val emoji: String,
    @JsonProperty("created_at")
    val createdAt: OffsetDateTime?
)
