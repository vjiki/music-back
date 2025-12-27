package com.vjiki.music.entity

import java.io.Serializable
import java.util.UUID

data class UserRoleId(
    val userId: UUID,
    val role: String
) : Serializable


