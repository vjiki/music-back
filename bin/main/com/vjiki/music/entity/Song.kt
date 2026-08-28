package com.vjiki.music.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.type.SqlTypes
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "songs", schema = "music")
data class Song(
    @Id
    @GeneratedValue
    @Column(name = "id")
    val id: UUID = UUID.randomUUID(),

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "artists", nullable = false, columnDefinition = "jsonb")
    val artists: Map<String, List<String>>,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "audio_urls", nullable = false, columnDefinition = "jsonb")
    val audioUrls: Map<String, String>,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "cover_urls", nullable = false, columnDefinition = "jsonb")
    val coverUrls: Map<String, String>,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "video_urls", nullable = false, columnDefinition = "jsonb")
    val videoUrls: Map<String, String> = emptyMap(),

    @Column(name = "title", nullable = false)
    val title: String,

    @Column(name = "likes_count")
    var likesCount: Long = 0L,

    @Column(name = "dislikes_count")
    var dislikesCount: Long = 0L,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: OffsetDateTime? = null,

    @Column(name = "created_by", nullable = false, updatable = false)
    val createdBy: String,

    @UpdateTimestamp
    @Column(name = "modified_at", nullable = false)
    var modifiedAt: OffsetDateTime? = null,

    @Column(name = "modified_by", nullable = false)
    var modifiedBy: String,

    @Version
    @Column(name = "version", nullable = false)
    var version: Int = 0,

    @Column(name = "active", nullable = false)
    var active: Boolean = false,

    @Column(name = "type", nullable = false)
    val type: String = "SONG"
)

