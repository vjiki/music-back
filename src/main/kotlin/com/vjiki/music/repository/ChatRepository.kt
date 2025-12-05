package com.vjiki.music.repository

import com.vjiki.music.entity.Chat
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ChatRepository : JpaRepository<Chat, UUID> {

    @Query("SELECT DISTINCT c FROM Chat c INNER JOIN ChatParticipant cp ON cp.chatId = c.id WHERE cp.userId = :userId ORDER BY c.updatedAt DESC")
    fun findChatsByUserId(@Param("userId") userId: UUID): List<Chat>
}

