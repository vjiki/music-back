package com.vjiki.music.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "user_follows", schema = "music")
@IdClass(UserFollowId::class)
data class UserFollow(
    @Id
    @Column(name = "follower_id", nullable = false)
    val followerId: UUID,

    @Id
    @Column(name = "followed_id", nullable = false)
    val followedId: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", insertable = false, updatable = false)
    val follower: User? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followed_id", insertable = false, updatable = false)
    val followed: User? = null,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: OffsetDateTime? = null
)

