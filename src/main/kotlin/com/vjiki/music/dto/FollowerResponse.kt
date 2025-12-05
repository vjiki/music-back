package com.vjiki.music.dto

import java.time.OffsetDateTime
import java.util.UUID

data class FollowerResponse(
    val followerId: UUID,
    val followerEmail: String,
    val followerNickname: String,
    val followerAvatarUrl: String?,
    val followedAt: OffsetDateTime?
)

