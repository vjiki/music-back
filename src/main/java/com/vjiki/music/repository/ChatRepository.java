package com.vjiki.music.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vjiki.music.entity.Chat;

@Repository
public interface ChatRepository extends JpaRepository<Chat, UUID> {

    @Query("SELECT DISTINCT c FROM Chat c INNER JOIN ChatParticipant cp ON cp.chatId = c.id WHERE cp.userId = :userId ORDER BY c.updatedAt DESC")
    List<Chat> findChatsByUserId(@Param("userId") UUID userId);

    @Query("""
            SELECT c FROM Chat c
            LEFT JOIN FETCH c.participants
            WHERE c.id = :id
            """)
    Optional<Chat> findByIdWithParticipants(@Param("id") UUID id);

    @Modifying
    @Query(value = """
            INSERT INTO music.chats (id, type, title, description, avatar_url, owner_id, is_encrypted, is_archived, is_muted, created_at, updated_at, version)
            VALUES (:id, :type, :title, :description, :avatarUrl, :ownerId, :isEncrypted, false, false, NOW(), NOW(), 0)
            """, nativeQuery = true)
    void insertChat(
            @Param("id") UUID id,
            @Param("type") String type,
            @Param("title") String title,
            @Param("description") String description,
            @Param("avatarUrl") String avatarUrl,
            @Param("ownerId") UUID ownerId,
            @Param("isEncrypted") boolean isEncrypted);

    @Modifying
    @Query(value = """
            UPDATE music.chats
            SET updated_at = NOW()
            WHERE id = :chatId
            """, nativeQuery = true)
    void updateChatTimestamp(@Param("chatId") UUID chatId);

    @Modifying
    @Query(value = """
            DELETE FROM music.chats
            WHERE id = :chatId
            """, nativeQuery = true)
    void deleteChat(@Param("chatId") UUID chatId);
}
