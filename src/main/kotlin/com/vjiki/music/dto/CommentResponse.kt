package com.vjiki.music.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime
import java.util.UUID

data class CommentResponse(
    val id: UUID,
    @JsonProperty("track_id")
    val trackId: UUID,
    @JsonProperty("user_id")
    val userId: UUID,
    @JsonProperty("user_nickname")
    val userNickname: String?,
    @JsonProperty("user_avatar_url")
    val userAvatarUrl: String?,
    @JsonProperty("parent_id")
    val parentId: UUID?,
    val content: String,
    val status: String,
    @JsonProperty("likes_count")
    val likesCount: Int,
    @JsonProperty("replies_count")
    val repliesCount: Int,
    @JsonProperty("is_liked")
    val isLiked: Boolean = false,
    @JsonProperty("created_at")
    val createdAt: OffsetDateTime?,
    @JsonProperty("updated_at")
    val updatedAt: OffsetDateTime?,
    val replies: List<CommentResponse> = emptyList()
)
