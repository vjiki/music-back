package com.vjiki.music.dto

import java.util.UUID

data class CreateChatRequest(
    val type: String = "DIRECT",
    val title: String? = null,
    val description: String? = null,
    val avatarUrl: String? = null,
    val ownerId: UUID? = null,
    val participantIds: List<UUID>,
    val isEncrypted: Boolean = false
)
