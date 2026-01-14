package com.vjiki.music.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "track_comment_reaction", schema = "music")
@IdClass(TrackCommentReactionId::class)
data class TrackCommentReaction(
    @Id
    @Column(name = "comment_id", nullable = false)
    val commentId: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", insertable = false, updatable = false)
    val comment: TrackComment? = null,

    @Id
    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    val user: User? = null,

    @Column(name = "reaction", nullable = false)
    val reaction: String = "LIKE",

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: OffsetDateTime? = null
)

data class TrackCommentReactionId(
    val commentId: UUID,
    val userId: UUID
) : java.io.Serializable
