package com.vjiki.music.repository

import com.vjiki.music.entity.MessageRead
import com.vjiki.music.entity.MessageReadId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MessageReadRepository : JpaRepository<MessageRead, MessageReadId> {

    @Modifying
    @Query(
        value = """
            INSERT INTO music.message_reads (message_id, user_id, read_at)
            VALUES (:messageId, :userId, NOW())
            ON CONFLICT (message_id, user_id) DO UPDATE SET read_at = NOW()
        """,
        nativeQuery = true
    )
    fun markAsRead(
        @Param("messageId") messageId: UUID,
        @Param("userId") userId: UUID
    )

    fun existsByMessageIdAndUserId(messageId: UUID, userId: UUID): Boolean
}
