package com.vjiki.music.repository;

import com.vjiki.music.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    
    @Query("SELECT m FROM Message m " +
           "LEFT JOIN FETCH m.sender " +
           "WHERE m.chatId = :chatId " +
           "AND (m.isDeleted = false OR m.isDeleted IS NULL) " +
           "ORDER BY m.createdAt DESC")
    List<Message> findMessagesByChatId(@Param("chatId") UUID chatId);
    
    @Query("SELECT m FROM Message m " +
           "LEFT JOIN FETCH m.sender " +
           "WHERE m.chatId = :chatId " +
           "AND EXISTS (SELECT cp FROM ChatParticipant cp WHERE cp.chatId = :chatId AND cp.userId = :userId1) " +
           "AND EXISTS (SELECT cp FROM ChatParticipant cp WHERE cp.chatId = :chatId AND cp.userId = :userId2) " +
           "AND (m.isDeleted = false OR m.isDeleted IS NULL) " +
           "ORDER BY m.createdAt ASC")
    List<Message> findMessagesByChatIdAndUsers(@Param("chatId") UUID chatId, 
                                                @Param("userId1") UUID userId1, 
                                                @Param("userId2") UUID userId2);
}

