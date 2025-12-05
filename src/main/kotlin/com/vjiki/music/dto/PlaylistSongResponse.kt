package com.vjiki.music.dto

import java.time.OffsetDateTime
import java.util.UUID

data class PlaylistSongResponse(
    val id: UUID,
    val playlistId: UUID,
    val songId: UUID,
    val songTitle: String?,
    val songArtist: String?,
    val songAudioUrl: String?,
    val songCoverUrl: String?,
    val position: Int,
    val addedAt: OffsetDateTime?,
    val addedBy: UUID?
)

