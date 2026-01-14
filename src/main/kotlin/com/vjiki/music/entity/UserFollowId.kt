package com.vjiki.music.entity

import java.io.Serializable
import java.util.UUID

data class UserFollowId(
    val followerId: UUID = UUID(0, 0),
    val followedId: UUID = UUID(0, 0)
) : Serializable

