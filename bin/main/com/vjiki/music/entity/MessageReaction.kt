package com.vjiki.music.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "message_reactions", schema = "music")
@IdClass(MessageReactionId::class)
data class MessageReaction(
    @Id
    @Column(name = "message_id", nullable = false)
    val messageId: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", insertable = false, updatable = false)
    val message: Message? = null,

    @Id
    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    val user: User? = null,

    @Id
    @Column(name = "emoji", nullable = false, length = 20)
    val emoji: String,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: OffsetDateTime? = null
)

data class MessageReactionId(
    val messageId: UUID = UUID(0, 0),
    val userId: UUID = UUID(0, 0),
    val emoji: String = ""
) : java.io.Serializable
