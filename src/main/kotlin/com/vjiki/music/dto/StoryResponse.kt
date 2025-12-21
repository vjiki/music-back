package com.vjiki.music.dto

import java.time.OffsetDateTime
import java.util.UUID

/**
 * Instagram-style story response
 * Optimized for mobile story UI
 */
data class StoryResponse(
    val id: UUID,
    val userId: UUID,
    val userNickname: String?,
    val userAvatarUrl: String?,
    val imageUrl: String?,
    val previewUrl: String?,
    val storyType: String,
    val songId: UUID?,
    val songTitle: String?,
    val songArtist: String?,
    val caption: String?,
    val location: String?,
    val viewsCount: Int,
    val createdAt: OffsetDateTime?,
    val expiresAt: OffsetDateTime?,
    val isExpired: Boolean
)

