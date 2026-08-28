package com.vjiki.music.mapper

import com.vjiki.music.dto.CommentResponse
import com.vjiki.music.entity.TrackComment
import org.springframework.stereotype.Component

@Component
object CommentMapper {
    fun TrackComment.toResponse(isLiked: Boolean = false, replies: List<CommentResponse> = emptyList()): CommentResponse {
        return CommentResponse(
            id = id,
            trackId = trackId,
            userId = userId,
            userNickname = user?.nickname,
            userAvatarUrl = user?.avatarUrl,
            parentId = parentId,
            content = content,
            status = status,
            likesCount = likesCount,
            repliesCount = repliesCount,
            isLiked = isLiked,
            createdAt = createdAt,
            updatedAt = updatedAt,
            replies = replies
        )
    }
}
