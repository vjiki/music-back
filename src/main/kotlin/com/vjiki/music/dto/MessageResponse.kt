package com.vjiki.music.dto

import java.time.OffsetDateTime
import java.util.UUID

data class MessageResponse(
    val id: UUID,
    val chatId: UUID,
    val senderId: UUID?,
    val senderEmail: String?,
    val senderNickname: String?,
    val senderAvatarUrl: String?,
    val replyToId: UUID?,
    val messageType: String,
    val content: String?,
    val songId: UUID?,
    val attachmentCount: Int,
    val isEdited: Boolean,
    val isDeleted: Boolean,
    val createdAt: OffsetDateTime?,
    val updatedAt: OffsetDateTime?
)

