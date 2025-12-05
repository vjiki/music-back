package com.vjiki.music.dto

import java.time.OffsetDateTime
import java.util.UUID

data class ParticipantResponse(
    val userId: UUID,
    val userEmail: String,
    val userNickname: String,
    val userAvatarUrl: String?,
    val role: String,
    val joinedAt: OffsetDateTime?,
    val isMuted: Boolean
)

