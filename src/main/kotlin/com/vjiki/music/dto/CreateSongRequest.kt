package com.vjiki.music.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateSongRequest(
    val title: String,
    val artists: List<String>,
    @JsonProperty("audio_url")
    val audioUrl: String,
    val cover: String? = null,
    @JsonProperty("video_url")
    val videoUrl: String? = null,
    /**
     * SONG or SHORT (stored as plain string in DB column `songs.type`)
     */
    val type: String = "SONG",
    val active: Boolean = true,
    val tags: List<CreateSongTagRequest> = emptyList()
)

data class CreateSongTagRequest(
    val name: String,
    val weight: Double = 1.0,
    /**
     * Tag type stored in `tag.type` (e.g. GENRE / MOOD / DEFAULT)
     */
    val type: String = "DEFAULT",
    /**
     * Stored in `track_tag.source` (e.g. MANUAL / ML / IMPORT)
     */
    val source: String = "MANUAL"
)


