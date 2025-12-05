package com.vjiki.music.mapper

import com.vjiki.music.dto.PlaylistResponse
import com.vjiki.music.entity.Playlist
import org.springframework.stereotype.Component

@Component
object PlaylistMapper {
    fun Playlist.toResponse(): PlaylistResponse {
        return PlaylistResponse(
            id = id,
            userId = user.id,
            userName = user.email,
            userNickname = user.nickname,
            name = name,
            description = description,
            coverUrl = coverUrl,
            type = type,
            isPublic = isPublic,
            createdAt = createdAt,
            modifiedAt = modifiedAt
        )
    }
}

