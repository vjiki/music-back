package com.vjiki.music.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "stories", schema = "music")
data class Story(
    @Id
    @GeneratedValue
    @Column(name = "id")
    val id: UUID = UUID.randomUUID(),

    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    val user: User? = null,

    @Column(name = "image_url", columnDefinition = "TEXT")
    val imageUrl: String? = null,

    @Column(name = "preview_url", columnDefinition = "TEXT")
    val previewUrl: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "story_type", nullable = false)
    val storyType: StoryType = StoryType.IMAGE,

    @Column(name = "song_id")
    val songId: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id", insertable = false, updatable = false)
    val song: Song? = null,

    @Column(name = "caption", columnDefinition = "TEXT")
    val caption: String? = null,

    @Column(name = "location")
    val location: String? = null,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,

    @Column(name = "is_deleted", nullable = false)
    var isDeleted: Boolean = false,

    @Column(name = "views_count", nullable = false)
    var viewsCount: Int = 0,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: OffsetDateTime? = null,

    @Column(name = "expires_at")
    val expiresAt: OffsetDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    var updatedAt: OffsetDateTime? = null,

    @Version
    @Column(name = "version", nullable = false)
    var version: Int = 0
)

