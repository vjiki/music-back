package com.vjiki.music.entity

import java.io.Serializable
import java.util.UUID

data class UserFollowId(
    val followerId: UUID,
    val followedId: UUID
) : Serializable

