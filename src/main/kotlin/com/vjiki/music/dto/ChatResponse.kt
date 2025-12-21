package com.vjiki.music.dto

import java.time.OffsetDateTime
import java.util.UUID

data class ChatResponse(
    val id: UUID,
    val type: String,
    val title: String?,
    val description: String?,
    val avatarUrl: String?,
    val ownerId: UUID?,
    val ownerNickname: String?,
    val isEncrypted: Boolean,
    val isArchived: Boolean,
    val isMuted: Boolean,
    val createdAt: OffsetDateTime?,
    val updatedAt: OffsetDateTime?,
    val participants: List<ParticipantResponse>
)

