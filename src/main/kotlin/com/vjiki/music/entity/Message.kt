package com.vjiki.music.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "messages", schema = "music")
data class Message(
    @Id
    @GeneratedValue
    @Column(name = "id")
    val id: UUID = UUID.randomUUID(),

    @Column(name = "chat_id", nullable = false)
    val chatId: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", insertable = false, updatable = false)
    val chat: Chat? = null,

    @Column(name = "sender_id")
    val senderId: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", insertable = false, updatable = false)
    val sender: User? = null,

    @Column(name = "reply_to_id")
    val replyToId: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_to_id", insertable = false, updatable = false)
    val replyTo: Message? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type")
    val messageType: MessageType = MessageType.TEXT,

    @Column(name = "content", columnDefinition = "TEXT")
    val content: String? = null,

    @Column(name = "song_id")
    val songId: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id", insertable = false, updatable = false)
    val song: Song? = null,

    @Column(name = "attachment_count")
    val attachmentCount: Int = 0,

    @Column(name = "is_edited", nullable = false)
    var isEdited: Boolean = false,

    @Column(name = "is_deleted", nullable = false)
    var isDeleted: Boolean = false,

    @Column(name = "deleted_at")
    var deletedAt: OffsetDateTime? = null,

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

