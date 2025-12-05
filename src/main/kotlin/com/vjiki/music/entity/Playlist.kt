package com.vjiki.music.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "playlists", schema = "music")
data class Playlist(
    @Id
    @GeneratedValue
    @Column(name = "id")
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "description", columnDefinition = "text")
    val description: String? = null,

    @Column(name = "cover_url", columnDefinition = "text")
    val coverUrl: String? = null,

    @Column(name = "type", nullable = false)
    val type: String = "CUSTOM",

    @Column(name = "is_public", nullable = false)
    val isPublic: Boolean = false,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: OffsetDateTime? = null,

    @Column(name = "created_by", nullable = false, updatable = false)
    val createdBy: String = "system",

    @UpdateTimestamp
    @Column(name = "modified_at", nullable = false)
    var modifiedAt: OffsetDateTime? = null,

    @Column(name = "modified_by", nullable = false)
    var modifiedBy: String = "system",

    @Version
    @Column(name = "version", nullable = false)
    var version: Int = 0
)

