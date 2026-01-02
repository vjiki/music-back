package com.vjiki.music.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.util.UUID

data class BandResponse(
    val id: UUID,
    val name: String,
    @JsonProperty("sort_name")
    val sortName: String,
    @JsonProperty("country_code")
    val countryCode: String?,
    @JsonProperty("is_band")
    val isBand: Boolean,
    @JsonProperty("debut_year")
    val debutYear: Short?,
    val popularity: Int,
    @JsonProperty("created_at")
    val createdAt: LocalDateTime?,
    @JsonProperty("updated_at")
    val updatedAt: LocalDateTime?,
    @JsonProperty("cover_url")
    val coverUrl: String?,
    val songs: List<SongResponse> = emptyList()
)


