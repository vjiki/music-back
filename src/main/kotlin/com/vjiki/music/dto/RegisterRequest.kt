package com.vjiki.music.dto

data class RegisterRequest(
    val email: String,
    val password: String? = null,
    /**
     * LOCAL (default), GOOGLE, APPLE
     */
    val provider: String? = null,
    /**
     * Optional external provider id (for OAuth users)
     */
    val providerId: String? = null,
    val nickname: String? = null,
    val avatarUrl: String? = null
)


