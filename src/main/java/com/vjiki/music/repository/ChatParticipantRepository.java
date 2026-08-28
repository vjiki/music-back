package com.vjiki.music.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vjiki.music.entity.ChatParticipant;
import com.vjiki.music.entity.ChatParticipantId;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, ChatParticipantId> {

    @Query("SELECT cp FROM ChatParticipant cp LEFT JOIN FETCH cp.user WHERE cp.chatId = :chatId")
    List<ChatParticipant> findByChatId(@Param("chatId") UUID chatId);

    @Query(value = "SELECT * FROM music.chat_participants WHERE user_id = :userId", nativeQuery = true)
    List<ChatParticipant> findByUserId(@Param("userId") UUID userId);

    @Query(value = "SELECT * FROM music.chat_participants WHERE chat_id = :chatId AND user_id = :userId", nativeQuery = true)
    Optional<ChatParticipant> findByChatIdAndUserId(@Param("chatId") UUID chatId, @Param("userId") UUID userId);

    @Query(value = "SELECT * FROM music.chat_participants WHERE chat_id = :chatId AND user_id IN (:userIds)", nativeQuery = true)
    List<ChatParticipant> findByChatIdAndUserIds(@Param("chatId") UUID chatId, @Param("userIds") List<UUID> userIds);

    @Modifying
    @Query(value = """
            INSERT INTO music.chat_participants (chat_id, user_id, role, joined_at, is_muted)
            VALUES (:chatId, :userId, :role, NOW(), false)
            ON CONFLICT (chat_id, user_id) DO NOTHING
            """, nativeQuery = true)
    void insertParticipant(
            @Param("chatId") UUID chatId,
            @Param("userId") UUID userId,
            @Param("role") String role);

    default void insertParticipant(UUID chatId, UUID userId) {
        insertParticipant(chatId, userId, "MEMBER");
    }

    @Modifying
    @Query(value = """
            UPDATE music.chat_participants
            SET last_read_message_id = :messageId
            WHERE chat_id = :chatId AND user_id = :userId
            """, nativeQuery = true)
    void updateLastReadMessageId(
            @Param("chatId") UUID chatId,
            @Param("userId") UUID userId,
            @Param("messageId") UUID messageId);
}
