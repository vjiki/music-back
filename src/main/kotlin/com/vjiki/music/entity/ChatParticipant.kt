package com.vjiki.music.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "chat_participants", schema = "music")
@IdClass(ChatParticipantId::class)
data class ChatParticipant(
    @Id
    @Column(name = "chat_id", nullable = false)
    val chatId: UUID,

    @Id
    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", insertable = false, updatable = false)
    val chat: Chat? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    val user: User? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    val role: ParticipantRole = ParticipantRole.MEMBER,

    @CreationTimestamp
    @Column(name = "joined_at", nullable = false, updatable = false)
    val joinedAt: OffsetDateTime? = null,

    @Column(name = "is_muted", nullable = false)
    val isMuted: Boolean = false,

    @Column(name = "last_read_message_id")
    val lastReadMessageId: UUID? = null
)

