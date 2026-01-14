package com.vjiki.music.repository

import com.vjiki.music.entity.Chat
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ChatRepository : JpaRepository<Chat, UUID> {

    @Query("SELECT DISTINCT c FROM Chat c INNER JOIN ChatParticipant cp ON cp.chatId = c.id WHERE cp.userId = :userId ORDER BY c.updatedAt DESC")
    fun findChatsByUserId(@Param("userId") userId: UUID): List<Chat>

    @Query(
        """
        SELECT c FROM Chat c 
        LEFT JOIN FETCH c.participants 
        WHERE c.id = :id
        """
    )
    fun findByIdWithParticipants(@Param("id") id: UUID): Optional<Chat>

    @Modifying
    @Query(
        value = """
            INSERT INTO music.chats (id, type, title, description, avatar_url, owner_id, is_encrypted, is_archived, is_muted, created_at, updated_at, version)
            VALUES (:id, :type, :title, :description, :avatarUrl, :ownerId, :isEncrypted, false, false, NOW(), NOW(), 0)
        """,
        nativeQuery = true
    )
    fun insertChat(
        @Param("id") id: UUID,
        @Param("type") type: String,
        @Param("title") title: String?,
        @Param("description") description: String?,
        @Param("avatarUrl") avatarUrl: String?,
        @Param("ownerId") ownerId: UUID?,
        @Param("isEncrypted") isEncrypted: Boolean
    )

    @Modifying
    @Query(
        value = """
            UPDATE music.chats 
            SET updated_at = NOW()
            WHERE id = :chatId
        """,
        nativeQuery = true
    )
    fun updateChatTimestamp(@Param("chatId") chatId: UUID)

    @Modifying
    @Query(
        value = """
            DELETE FROM music.chats 
            WHERE id = :chatId
        """,
        nativeQuery = true
    )
    fun deleteChat(@Param("chatId") chatId: UUID)
}

