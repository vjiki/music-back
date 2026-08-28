package com.vjiki.music.mapper

import com.vjiki.music.dto.ShortResponse
import com.vjiki.music.entity.Song
import org.springframework.stereotype.Component

@Component
object ShortMapper {
    fun Song.toShortResponse(
        isLiked: Boolean = false,
        isDisliked: Boolean = false,
        likesCount: Long = this.likesCount,
        dislikesCount: Long = this.dislikesCount
    ): ShortResponse {
        return ShortResponse(
            id = id.toString(),
            artist = extractArtist(artists),
            audioUrl = extractUrl(audioUrls),
            cover = extractUrl(coverUrls),
            title = title,
            videoUrl = extractUrl(videoUrls),
            type = type,
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

