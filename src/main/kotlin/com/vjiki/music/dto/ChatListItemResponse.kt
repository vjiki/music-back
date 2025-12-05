package com.vjiki.music.dto

import java.time.OffsetDateTime
import java.util.UUID

/**
 * Instagram-style chat list item response
 * Optimized for mobile chat list UI
 */
data class ChatListItemResponse(
    val chatId: UUID,
    val chatType: String,
    val title: String?,
    val avatarUrl: String?,
    val lastMessagePreview: String?,
    val lastMessageAt: OffsetDateTime?,
    val lastMessageSenderId: UUID?,
    val lastMessageSenderName: String?,
    val unreadCount: Int,
    val isMuted: Boolean,
    val updatedAt: OffsetDateTime?,
    val participants: List<ParticipantSummaryResponse>
)

