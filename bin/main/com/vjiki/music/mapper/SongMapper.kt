package com.vjiki.music.mapper

import com.vjiki.music.dto.SongResponse
import com.vjiki.music.entity.Song
import org.springframework.stereotype.Component
import java.util.UUID

@Component
object SongMapper {
    fun Song.toResponse(
        isLiked: Boolean = false,
        isDisliked: Boolean = false,
        likesCount: Long = this.likesCount,
        dislikesCount: Long = this.dislikesCount
    ): SongResponse {
        return SongResponse(
            id = id.toString(),
            artist = extractArtist(artists),
            audioUrl = extractUrl(audioUrls),
            cover = extractUrl(coverUrls),
            title = title,
            videoUrl = extractUrl(videoUrls),
            isLiked = isLiked,
            isDisliked = isDisliked,
            likesCount = likesCount,
            dislikesCount = dislikesCount
        )
    }

    private fun extractArtist(artists: Map<String, List<String>>?): String? {
        return artists?.get("default")?.firstOrNull()
    }

    private fun extractUrl(urls: Map<String, String>?): String? {
        return urls?.get("default")
    }
}

