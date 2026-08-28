package com.vjiki.music.entity

import java.io.Serializable
import java.util.UUID

data class TrackTagId(
    val trackId: UUID = UUID(0, 0),
    val tagId: UUID = UUID(0, 0)
) : Serializable


