package com.vjiki.music.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vjiki.music.entity.MessageRead;
import com.vjiki.music.entity.MessageReadId;

@Repository
public interface MessageReadRepository extends JpaRepository<MessageRead, MessageReadId> {

    @Modifying
    @Query(value = """
            INSERT INTO music.message_reads (message_id, user_id, read_at)
            VALUES (:messageId, :userId, NOW())
            ON CONFLICT (message_id, user_id) DO UPDATE SET read_at = NOW()
            """, nativeQuery = true)
    void markAsRead(
            @Param("messageId") UUID messageId,
            @Param("userId") UUID userId);

    boolean existsByMessageIdAndUserId(UUID messageId, UUID userId);
}
