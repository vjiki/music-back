package com.vjiki.music.dto

import java.time.OffsetDateTime
import java.util.UUID

data class PlaylistResponse(
    val id: UUID,
    val userId: UUID,
    val userName: String?,
    val userNickname: String?,
    val name: String,
    val description: String?,
    val coverUrl: String?,
    val type: String,
    val isPublic: Boolean,
    val createdAt: OffsetDateTime?,
    val modifiedAt: OffsetDateTime?
)

