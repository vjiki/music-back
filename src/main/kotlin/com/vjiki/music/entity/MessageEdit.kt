package com.vjiki.music.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "message_edits", schema = "music")
data class MessageEdit(
    @Id
    @GeneratedValue
    @Column(name = "id")
    val id: UUID = UUID.randomUUID(),

    @Column(name = "message_id", nullable = false)
    val messageId: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", insertable = false, updatable = false)
    val message: Message? = null,

    @Column(name = "old_content", columnDefinition = "TEXT")
    val oldContent: String? = null,

    @Column(name = "new_content", columnDefinition = "TEXT")
    val newContent: String? = null,

    @Column(name = "edited_by")
    val editedBy: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "edited_by", insertable = false, updatable = false)
    val editor: User? = null,

    @CreationTimestamp
    @Column(name = "edited_at", nullable = false, updatable = false)
    val editedAt: OffsetDateTime? = null
)
