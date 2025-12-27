package com.vjiki.music.dto

import java.util.UUID

data class AuthExistsResponse(
    val exists: Boolean,
    val userId: UUID? = null
)


