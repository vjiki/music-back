package com.vjiki.music.mapper

import com.vjiki.music.dto.FollowerResponse
import com.vjiki.music.entity.UserFollow
import org.springframework.stereotype.Component

@Component
object FollowerMapper {
    fun UserFollow.toResponse(): FollowerResponse {
        return FollowerResponse(
            followerId = followerId,
            followerEmail = follower?.email ?: "",
            followerNickname = follower?.nickname ?: "",
            followerAvatarUrl = follower?.avatarUrl,
            followedAt = createdAt
        )
    }
}

