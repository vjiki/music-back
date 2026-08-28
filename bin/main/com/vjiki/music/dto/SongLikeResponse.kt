package com.vjiki.music.dto

data class SongLikeResponse(
    val isLiked: Boolean,
    val isDisliked: Boolean,
    val likesCount: Long,
    val dislikesCount: Long
)

