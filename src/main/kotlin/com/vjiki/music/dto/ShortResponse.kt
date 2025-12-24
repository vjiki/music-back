package com.vjiki.music.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ShortResponse(
    val id: String,
    val artist: String?,
    @JsonProperty("audio_url")
    val audioUrl: String?,
    val cover: String?,
    val title: String?,
    @JsonProperty("video_urls")
    val videoUrls: Map<String, String>?,
    val type: String,
    val isLiked: Boolean = false,
    val isDisliked: Boolean = false,
    val likesCount: Long = 0L,
    val dislikesCount: Long = 0L
)

