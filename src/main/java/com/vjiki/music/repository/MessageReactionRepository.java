package com.vjiki.music.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vjiki.music.entity.MessageReaction;
import com.vjiki.music.entity.MessageReactionId;

@Repository
public interface MessageReactionRepository extends JpaRepository<MessageReaction, MessageReactionId> {

    @Query("""
            SELECT r FROM MessageReaction r
            WHERE r.messageId = :messageId
            """)
    List<MessageReaction> findByMessageId(@Param("messageId") UUID messageId);

    @Query("""
            SELECT r FROM MessageReaction r
            WHERE r.messageId IN :messageIds
            """)
    List<MessageReaction> findByMessageIds(@Param("messageIds") List<UUID> messageIds);

    @Modifying
    @Query(value = """
            INSERT INTO music.message_reactions (message_id, user_id, emoji, created_at)
            VALUES (:messageId, :userId, :emoji, NOW())
            ON CONFLICT (message_id, user_id, emoji) DO NOTHING
            """, nativeQuery = true)
    void insertReaction(
            @Param("messageId") UUID messageId,
            @Param("userId") UUID userId,
            @Param("emoji") String emoji);

    @Modifying
    @Query(value = """
            DELETE FROM music.message_reactions
            WHERE message_id = :messageId AND user_id = :userId AND emoji = :emoji
            """, nativeQuery = true)
    void deleteReaction(
            @Param("messageId") UUID messageId,
            @Param("userId") UUID userId,
            @Param("emoji") String emoji);
}
