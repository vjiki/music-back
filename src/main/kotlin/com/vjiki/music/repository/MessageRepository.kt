package com.vjiki.music.repository

import com.vjiki.music.entity.Message
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.*

@Repository
interface MessageRepository : JpaRepository<Message, UUID> {

    @Query("SELECT m FROM Message m LEFT JOIN FETCH m.sender WHERE m.chatId = :chatId AND (m.isDeleted = false OR m.isDeleted IS NULL) ORDER BY m.createdAt DESC")
    fun findMessagesByChatId(@Param("chatId") chatId: UUID): List<Message>

    @Query("SELECT m FROM Message m LEFT JOIN FETCH m.sender WHERE m.chatId = :chatId AND EXISTS (SELECT cp FROM ChatParticipant cp WHERE cp.chatId = :chatId AND cp.userId = :userId1) AND EXISTS (SELECT cp FROM ChatParticipant cp WHERE cp.chatId = :chatId AND cp.userId = :userId2) AND (m.isDeleted = false OR m.isDeleted IS NULL) ORDER BY m.createdAt ASC")
    fun findMessagesByChatIdAndUsers(
        @Param("chatId") chatId: UUID,
        @Param("userId1") userId1: UUID,
        @Param("userId2") userId2: UUID
    ): List<Message>

    @Query(
        value = """
            SELECT *
            FROM music.messages m
            WHERE m.chat_id = :chatId
              AND (m.is_deleted = false OR m.is_deleted IS NULL)
            ORDER BY m.created_at DESC, m.id DESC
        """,
        nativeQuery = true
    )
    fun findMessagesPageFirst(
        @Param("chatId") chatId: UUID,
        pageable: Pageable
    ): List<Message>

    @Query(
        value = """
            SELECT *
            FROM music.messages m
            WHERE m.chat_id = :chatId
              AND (m.is_deleted = false OR m.is_deleted IS NULL)
              AND (
                m.created_at < :cursorCreatedAt
                OR (m.created_at = :cursorCreatedAt AND m.id < :cursorId)
              )
            ORDER BY m.created_at DESC, m.id DESC
        """,
        nativeQuery = true
    )
    fun findMessagesPageAfter(
        @Param("chatId") chatId: UUID,
        @Param("cursorCreatedAt") cursorCreatedAt: OffsetDateTime,
        @Param("cursorId") cursorId: UUID,
        pageable: Pageable
    ): List<Message>

    @Query(
        """
        SELECT m FROM Message m 
        LEFT JOIN FETCH m.sender 
        WHERE m.id = :id
        """
    )
    fun findByIdWithSender(@Param("id") id: UUID): Optional<Message>

    @Modifying
    @Query(
        value = """
            INSERT INTO music.messages (id, chat_id, sender_id, reply_to_id, message_type, content, song_id, attachment_count, is_edited, is_deleted, created_at, updated_at, version)
            VALUES (:id, :chatId, :senderId, :replyToId, :messageType, :content, :songId, :attachmentCount, false, false, NOW(), NOW(), 0)
        """,
        nativeQuery = true
    )
    fun insertMessage(
        @Param("id") id: UUID,
        @Param("chatId") chatId: UUID,
        @Param("senderId") senderId: UUID?,
        @Param("replyToId") replyToId: UUID?,
        @Param("messageType") messageType: String,
        @Param("content") content: String?,
        @Param("songId") songId: UUID?,
        @Param("attachmentCount") attachmentCount: Int
    )

    @Modifying
    @Query(
        value = """
            UPDATE music.messages 
            SET is_deleted = true, deleted_at = NOW(), updated_at = NOW()
            WHERE id = :messageId AND is_deleted = false
        """,
        nativeQuery = true
    )
    fun deleteMessage(@Param("messageId") messageId: UUID)
}

