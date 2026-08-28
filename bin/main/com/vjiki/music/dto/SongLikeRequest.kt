package com.vjiki.music.dto

import java.util.UUID

data class SongLikeRequest(
    val userId: UUID,
    val songId: UUID
)

