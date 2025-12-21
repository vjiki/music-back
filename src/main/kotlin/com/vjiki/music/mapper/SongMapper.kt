package com.vjiki.music.mapper

import com.vjiki.music.dto.SongResponse
import com.vjiki.music.entity.Song
import org.springframework.stereotype.Component

@Component
object SongMapper {
    fun Song.toResponse(): SongResponse {
        return SongResponse(
            id = id.toString(),
            artist = extractArtist(artists),
            audioUrl = extractUrl(audioUrls),
            cover = extractUrl(coverUrls),
            title = title
        )
    }

    private fun extractArtist(artists: Map<String, List<String>>?): String? {
        return artists?.get("default")?.firstOrNull()
    }

    private fun extractUrl(urls: Map<String, String>?): String? {
        return urls?.get("default")
    }
}

