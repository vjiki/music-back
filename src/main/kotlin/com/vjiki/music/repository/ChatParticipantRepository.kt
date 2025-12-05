package com.vjiki.music.repository

import com.vjiki.music.entity.ChatParticipant
import com.vjiki.music.entity.ChatParticipantId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ChatParticipantRepository : JpaRepository<ChatParticipant, ChatParticipantId> {

    @Query("SELECT cp FROM ChatParticipant cp LEFT JOIN FETCH cp.user WHERE cp.chatId = :chatId")
    fun findByChatId(@Param("chatId") chatId: UUID): List<ChatParticipant>

    @Query(value = "SELECT * FROM music.chat_participants WHERE user_id = :userId", nativeQuery = true)
    fun findByUserId(@Param("userId") userId: UUID): List<ChatParticipant>

    @Query(value = "SELECT * FROM music.chat_participants WHERE chat_id = :chatId AND user_id = :userId", nativeQuery = true)
    fun findByChatIdAndUserId(@Param("chatId") chatId: UUID, @Param("userId") userId: UUID): Optional<ChatParticipant>

    @Query(value = "SELECT * FROM music.chat_participants WHERE chat_id = :chatId AND user_id IN (:userIds)", nativeQuery = true)
    fun findByChatIdAndUserIds(@Param("chatId") chatId: UUID, @Param("userIds") userIds: List<UUID>): List<ChatParticipant>
}

