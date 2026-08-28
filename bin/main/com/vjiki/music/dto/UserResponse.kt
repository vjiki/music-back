package com.vjiki.music.dto

import java.time.OffsetDateTime
import java.util.UUID

data class UserResponse(
    val id: UUID,
    val email: String,
    val nickname: String,
    val avatarUrl: String?,
    val accessLevel: String,
    val isActive: Boolean,
    val isVerified: Boolean,
    val lastLoginAt: OffsetDateTime?,
    val createdAt: OffsetDateTime?
)

