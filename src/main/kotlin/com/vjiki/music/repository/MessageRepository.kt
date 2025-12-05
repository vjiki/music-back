package com.vjiki.music.repository

import com.vjiki.music.entity.Message
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
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
}

