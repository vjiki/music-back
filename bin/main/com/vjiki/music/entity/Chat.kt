package com.vjiki.music.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "chats", schema = "music")
data class Chat(
    @Id
    @GeneratedValue
    @Column(name = "id")
    val id: UUID = UUID.randomUUID(),

    @OneToMany(mappedBy = "chat", fetch = FetchType.LAZY)
    val participants: MutableList<ChatParticipant> = mutableListOf(),

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    val type: ChatType = ChatType.DIRECT,

    @Column(name = "title")
    val title: String? = null,

    @Column(name = "description", columnDefinition = "TEXT")
    val description: String? = null,

    @Column(name = "avatar_url")
    val avatarUrl: String? = null,

    @Column(name = "owner_id")
    val ownerId: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", insertable = false, updatable = false)
    val owner: User? = null,

    @Column(name = "is_encrypted")
    val isEncrypted: Boolean = false,

    @Column(name = "is_archived")
    val isArchived: Boolean = false,

    @Column(name = "is_muted")
    val isMuted: Boolean = false,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: OffsetDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    var updatedAt: OffsetDateTime? = null,

    @Version
    @Column(name = "version", nullable = false)
    var version: Int = 0
)

