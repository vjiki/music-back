package com.vjiki.music.repository;

import com.vjiki.music.entity.ChatParticipant;
import com.vjiki.music.entity.ChatParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, ChatParticipantId> {
    
    @Query("SELECT cp FROM ChatParticipant cp " +
           "LEFT JOIN FETCH cp.user " +
           "WHERE cp.chatId = :chatId")
    List<ChatParticipant> findByChatId(@Param("chatId") UUID chatId);
    
    @Query(value = "SELECT * FROM music.chat_participants WHERE user_id = :userId", nativeQuery = true)
    List<ChatParticipant> findByUserId(@Param("userId") UUID userId);
    
    @Query(value = "SELECT * FROM music.chat_participants WHERE chat_id = :chatId AND user_id = :userId", nativeQuery = true)
    Optional<ChatParticipant> findByChatIdAndUserId(@Param("chatId") UUID chatId, @Param("userId") UUID userId);
    
    @Query(value = "SELECT * FROM music.chat_participants WHERE chat_id = :chatId AND user_id IN (:userIds)", nativeQuery = true)
    List<ChatParticipant> findByChatIdAndUserIds(@Param("chatId") UUID chatId, @Param("userIds") List<UUID> userIds);
}

