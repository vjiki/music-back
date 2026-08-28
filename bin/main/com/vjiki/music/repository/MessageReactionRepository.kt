package com.vjiki.music.repository

import com.vjiki.music.entity.MessageReaction
import com.vjiki.music.entity.MessageReactionId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MessageReactionRepository : JpaRepository<MessageReaction, MessageReactionId> {

    @Query(
        """
        SELECT r FROM MessageReaction r 
        WHERE r.messageId = :messageId
        """
    )
    fun findByMessageId(@Param("messageId") messageId: UUID): List<MessageReaction>

    @Query(
        """
        SELECT r FROM MessageReaction r 
        WHERE r.messageId IN :messageIds
        """
    )
    fun findByMessageIds(@Param("messageIds") messageIds: List<UUID>): List<MessageReaction>

    @Modifying
    @Query(
        value = """
            INSERT INTO music.message_reactions (message_id, user_id, emoji, created_at)
            VALUES (:messageId, :userId, :emoji, NOW())
            ON CONFLICT (message_id, user_id, emoji) DO NOTHING
        """,
        nativeQuery = true
    )
    fun insertReaction(
        @Param("messageId") messageId: UUID,
        @Param("userId") userId: UUID,
        @Param("emoji") emoji: String
    )

    @Modifying
    @Query(
        value = """
            DELETE FROM music.message_reactions
            WHERE message_id = :messageId AND user_id = :userId AND emoji = :emoji
        """,
        nativeQuery = true
    )
    fun deleteReaction(
        @Param("messageId") messageId: UUID,
        @Param("userId") userId: UUID,
        @Param("emoji") emoji: String
    )
}
