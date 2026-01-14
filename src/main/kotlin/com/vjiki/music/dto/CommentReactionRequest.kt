package com.vjiki.music.dto

import java.util.UUID

data class CommentReactionRequest(
    val commentId: UUID,
    val userId: UUID,
    val reaction: String = "LIKE"
)
