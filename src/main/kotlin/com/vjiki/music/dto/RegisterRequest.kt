package com.vjiki.music.dto

data class RegisterRequest(
    val email: String,
    val password: String? = null,
    val nickname: String? = null,
    val avatarUrl: String? = null
)


