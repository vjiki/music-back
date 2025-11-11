package com.vjiki.music.repository;

import com.vjiki.music.entity.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface StoryRepository extends JpaRepository<Story, UUID> {
    
    @Query("SELECT s FROM Story s " +
           "LEFT JOIN FETCH s.user " +
           "LEFT JOIN FETCH s.song " +
           "WHERE s.userId = :userId " +
           "AND s.isActive = true " +
           "AND s.isDeleted = false " +
           "AND (s.expiresAt IS NULL OR s.expiresAt > :now) " +
           "ORDER BY s.createdAt DESC")
    List<Story> findActiveStoriesByUserId(@Param("userId") UUID userId, @Param("now") OffsetDateTime now);
    
    @Query("SELECT s FROM Story s " +
           "LEFT JOIN FETCH s.user " +
           "LEFT JOIN FETCH s.song " +
           "WHERE s.isActive = true " +
           "AND s.isDeleted = false " +
           "AND (s.expiresAt IS NULL OR s.expiresAt > :now) " +
           "ORDER BY s.createdAt DESC")
    List<Story> findAllActiveStories(@Param("now") OffsetDateTime now);
}

