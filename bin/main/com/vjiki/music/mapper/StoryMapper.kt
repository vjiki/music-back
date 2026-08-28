package com.vjiki.music.mapper

import com.vjiki.music.dto.StoryResponse
import com.vjiki.music.entity.Story
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
object StoryMapper {
    fun Story.toResponse(): StoryResponse {
        val now = OffsetDateTime.now()
        val isExpired = expiresAt?.isBefore(now) ?: false

        return StoryResponse(
            id = id,
            userId = userId,
            userNickname = user?.nickname,
            userAvatarUrl = user?.avatarUrl,
            imageUrl = imageUrl,
            previewUrl = previewUrl,
            storyType = storyType.name,
            songId = songId,
            songTitle = song?.title,
            songArtist = extractArtist(song?.artists),
            caption = caption,
            location = location,
            viewsCount = viewsCount,
            createdAt = createdAt,
            expiresAt = expiresAt,
            isExpired = isExpired
        )
    }

    private fun extractArtist(artists: Map<String, List<String>>?): String? {
        return artists?.get("default")?.firstOrNull()
    }
}

