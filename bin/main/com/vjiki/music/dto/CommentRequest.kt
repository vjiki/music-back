package com.vjiki.music.dto

import java.util.UUID

data class CommentRequest(
    val trackId: UUID,
    val userId: UUID,
    val content: String,
    val parentId: UUID? = null
)
