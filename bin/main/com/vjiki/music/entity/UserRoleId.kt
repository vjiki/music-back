package com.vjiki.music.entity

import java.io.Serializable
import java.util.UUID

data class UserRoleId(
    val userId: UUID = UUID(0, 0),
    val role: String = ""
) : Serializable


