package com.vjiki.music.dto

import java.util.UUID

data class ParticipantSummaryResponse(
    val userId: UUID,
    val userNickname: String?,
    val userAvatarUrl: String?
)

