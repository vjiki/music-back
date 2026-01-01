package com.vjiki.music.entity

import java.io.Serializable
import java.util.UUID

data class TrackTagId(
    val trackId: UUID,
    val tagId: UUID
) : Serializable


