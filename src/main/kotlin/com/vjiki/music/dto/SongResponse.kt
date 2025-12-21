package com.vjiki.music.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class SongResponse(
    val id: String,
    val artist: String?,
    @JsonProperty("audio_url")
    val audioUrl: String?,
    val cover: String?,
    val title: String?
)

