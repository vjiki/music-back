package com.vjiki.music.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "artist", schema = "music")
data class Artist(
    @Id
    @Column(name = "id")
    val id: UUID = UUID.randomUUID(),

    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "sort_name", nullable = false)
    val sortName: String,

    @Column(name = "country_code")
    val countryCode: String? = null,

    @Column(name = "is_band", nullable = false)
    val isBand: Boolean = false,

    @Column(name = "debut_year")
    val debutYear: Short? = null,

    @Column(name = "popularity", nullable = false)
    val popularity: Int = 0,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null,

    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "cover_url", nullable = false, columnDefinition = "jsonb")
    val coverUrl: Map<String, String?> = mapOf("default" to null)
)


