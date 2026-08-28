package com.vjiki.music.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "track_tag", schema = "music")
@IdClass(TrackTagId::class)
data class TrackTag(
    @Id
    @Column(name = "track_id", nullable = false)
    val trackId: UUID,

    @Id
    @Column(name = "tag_id", nullable = false)
    val tagId: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_id", insertable = false, updatable = false)
    val track: Song? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", insertable = false, updatable = false)
    val tag: Tag? = null,

    @Column(name = "weight", nullable = false)
    val weight: Double = 1.0,

    @Column(name = "source", nullable = false)
    val source: String,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: OffsetDateTime? = null
)


