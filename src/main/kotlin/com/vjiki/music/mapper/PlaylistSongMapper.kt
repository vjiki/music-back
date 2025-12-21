package com.vjiki.music.mapper

import com.vjiki.music.dto.PlaylistSongResponse
import com.vjiki.music.entity.PlaylistSong
import org.springframework.stereotype.Component

@Component
object PlaylistSongMapper {
    fun PlaylistSong.toResponse(): PlaylistSongResponse {
        return PlaylistSongResponse(
            id = id,
            playlistId = playlist.id,
            songId = song.id,
            songTitle = song.title,
            songArtist = extractArtist(song.artists),
            songAudioUrl = extractUrl(song.audioUrls),
            songCoverUrl = extractUrl(song.coverUrls),
            position = position,
            addedAt = addedAt,
            addedBy = addedBy?.id
        )
    }

    private fun extractArtist(artists: Map<String, List<String>>?): String? {
        return artists?.get("default")?.firstOrNull()
    }

    private fun extractUrl(urls: Map<String, String>?): String? {
        return urls?.get("default")
    }
}

