package com.vjiki.music.service

import com.vjiki.music.dto.PlaylistResponse
import com.vjiki.music.dto.PlaylistWithSongsResponse
import java.util.UUID

interface PlaylistService {
    fun getPlaylistsByUserId(userId: UUID): List<PlaylistResponse>
    fun getPlaylistWithSongs(playlistId: UUID): PlaylistWithSongsResponse
}

