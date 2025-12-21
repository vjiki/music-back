package com.vjiki.music.mapper

import com.vjiki.music.dto.UserResponse
import com.vjiki.music.entity.User
import org.springframework.stereotype.Component

@Component
object UserMapper {
    fun User.toResponse(): UserResponse {
        return UserResponse(
            id = id,
            email = email,
            nickname = nickname,
            avatarUrl = avatarUrl,
            accessLevel = accessLevel.name,
            isActive = isActive,
            isVerified = isVerified,
            lastLoginAt = lastLoginAt,
            createdAt = createdAt
        )
    }
}

