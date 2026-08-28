package com.vjiki.music.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vjiki.music.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Query("SELECT m FROM Message m LEFT JOIN FETCH m.sender WHERE m.chatId = :chatId AND (m.isDeleted = false OR m.isDeleted IS NULL) ORDER BY m.createdAt DESC")
    List<Message> findMessagesByChatId(@Param("chatId") UUID chatId);

    @Query("SELECT m FROM Message m LEFT JOIN FETCH m.sender WHERE m.chatId = :chatId AND EXISTS (SELECT cp FROM ChatParticipant cp WHERE cp.chatId = :chatId AND cp.userId = :userId1) AND EXISTS (SELECT cp FROM ChatParticipant cp WHERE cp.chatId = :chatId AND cp.userId = :userId2) AND (m.isDeleted = false OR m.isDeleted IS NULL) ORDER BY m.createdAt ASC")
    List<Message> findMessagesByChatIdAndUsers(
            @Param("chatId") UUID chatId,
            @Param("userId1") UUID userId1,
            @Param("userId2") UUID userId2);

    @Query(value = """
            SELECT *
            FROM music.messages m
            WHERE m.chat_id = :chatId
              AND (m.is_deleted = false OR m.is_deleted IS NULL)
            ORDER BY m.created_at DESC, m.id DESC
            """, nativeQuery = true)
    List<Message> findMessagesPageFirst(
            @Param("chatId") UUID chatId,
            Pageable pageable);

    @Query(value = """
            SELECT *
            FROM music.messages m
            WHERE m.chat_id = :chatId
              AND (m.is_deleted = false OR m.is_deleted IS NULL)
              AND (
                m.created_at < :cursorCreatedAt
                OR (m.created_at = :cursorCreatedAt AND m.id < :cursorId)
              )
            ORDER BY m.created_at DESC, m.id DESC
            """, nativeQuery = true)
    List<Message> findMessagesPageAfter(
            @Param("chatId") UUID chatId,
            @Param("cursorCreatedAt") OffsetDateTime cursorCreatedAt,
            @Param("cursorId") UUID cursorId,
            Pageable pageable);

    @Query("""
            SELECT m FROM Message m
            LEFT JOIN FETCH m.sender
            WHERE m.id = :id
            """)
    Optional<Message> findByIdWithSender(@Param("id") UUID id);

    @Modifying
    @Query(value = """
            INSERT INTO music.messages (id, chat_id, sender_id, reply_to_id, message_type, content, song_id, attachment_count, is_edited, is_deleted, created_at, updated_at, version)
            VALUES (:id, :chatId, :senderId, :replyToId, :messageType, :content, :songId, :attachmentCount, false, false, NOW(), NOW(), 0)
            """, nativeQuery = true)
    void insertMessage(
            @Param("id") UUID id,
            @Param("chatId") UUID chatId,
            @Param("senderId") UUID senderId,
            @Param("replyToId") UUID replyToId,
            @Param("messageType") String messageType,
            @Param("content") String content,
            @Param("songId") UUID songId,
            @Param("attachmentCount") int attachmentCount);

    @Modifying
    @Query(value = """
            UPDATE music.messages
            SET is_deleted = true, deleted_at = NOW(), updated_at = NOW()
            WHERE id = :messageId AND is_deleted = false
            """, nativeQuery = true)
    void deleteMessage(@Param("messageId") UUID messageId);
}
