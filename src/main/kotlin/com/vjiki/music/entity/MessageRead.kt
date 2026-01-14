package com.vjiki.music.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "message_reads", schema = "music")
@IdClass(MessageReadId::class)
data class MessageRead(
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

    @CreationTimestamp
    @Column(name = "read_at", nullable = false, updatable = false)
    val readAt: OffsetDateTime? = null
)

data class MessageReadId(
    val messageId: UUID = UUID(0, 0),
    val userId: UUID = UUID(0, 0)
) : java.io.Serializable
