package com.vjiki.music.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "track_comment", schema = "music")
data class TrackComment(
    @Id
    @GeneratedValue
    @Column(name = "id")
    val id: UUID = UUID.randomUUID(),

    @Column(name = "track_id", nullable = false)
    val trackId: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_id", insertable = false, updatable = false)
    val track: Song? = null,

    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    val user: User? = null,

    @Column(name = "parent_id")
    val parentId: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    val parent: TrackComment? = null,

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    val content: String,

    @Column(name = "status", nullable = false)
    val status: String = "ACTIVE",

    @Column(name = "likes_count", nullable = false)
    var likesCount: Int = 0,

    @Column(name = "replies_count", nullable = false)
    var repliesCount: Int = 0,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: OffsetDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: OffsetDateTime? = null
)
