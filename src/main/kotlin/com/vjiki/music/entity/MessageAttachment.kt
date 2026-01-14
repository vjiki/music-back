package com.vjiki.music.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "message_attachments", schema = "music")
data class MessageAttachment(
    @Id
    @GeneratedValue
    @Column(name = "id")
    val id: UUID = UUID.randomUUID(),

    @Column(name = "message_id", nullable = false)
    val messageId: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", insertable = false, updatable = false)
    val message: Message? = null,

    @Column(name = "attachment_type", nullable = false, length = 30)
    val attachmentType: String,

    @Column(name = "url", nullable = false, columnDefinition = "TEXT")
    val url: String,

    @Column(name = "thumbnail_url", columnDefinition = "TEXT")
    val thumbnailUrl: String? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    val metadata: Map<String, Any>? = null,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: OffsetDateTime? = null
)
