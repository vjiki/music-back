package com.vjiki.music.security

import java.util.UUID

data class UserPrincipal(
    val userId: UUID,
    val email: String?,
    val firebaseUid: String?
)


