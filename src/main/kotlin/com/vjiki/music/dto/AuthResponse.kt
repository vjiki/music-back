package com.vjiki.music.dto

import java.util.UUID

data class AuthResponse(
    val authenticated: Boolean,
    val userId: UUID?,
    val message: String
)

