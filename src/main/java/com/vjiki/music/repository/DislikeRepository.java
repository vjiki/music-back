package com.vjiki.music.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vjiki.music.entity.Dislike;

@Repository
public interface DislikeRepository extends JpaRepository<Dislike, UUID> {

    @Query("SELECT COUNT(d) > 0 FROM Dislike d WHERE d.user.id = :userId AND d.song.id = :songId AND d.revokedAt IS NULL")
    boolean existsByUserIdAndSongIdAndRevokedAtIsNull(@Param("userId") UUID userId, @Param("songId") UUID songId);

    @Query("SELECT d FROM Dislike d WHERE d.user.id = :userId AND d.song.id = :songId AND d.revokedAt IS NULL")
    Optional<Dislike> findByUserIdAndSongIdAndRevokedAtIsNull(@Param("userId") UUID userId, @Param("songId") UUID songId);

    @Query("SELECT COUNT(d) FROM Dislike d WHERE d.song.id = :songId AND d.revokedAt IS NULL")
    long countBySongIdAndRevokedAtIsNull(@Param("songId") UUID songId);

    @Query("""
            SELECT d.song.id
            FROM Dislike d
            WHERE d.user.id = :userId
              AND d.song.id IN :songIds
              AND d.revokedAt IS NULL
            """)
    List<UUID> findActiveDislikedSongIds(
            @Param("userId") UUID userId,
            @Param("songIds") Collection<UUID> songIds);

    @Query("""
            SELECT d.song.id AS songId, COUNT(d) AS cnt
            FROM Dislike d
            WHERE d.song.id IN :songIds
              AND d.revokedAt IS NULL
            GROUP BY d.song.id
            """)
    List<SongIdCountProjection> countActiveDislikesBySongIds(
            @Param("songIds") Collection<UUID> songIds);

    @Modifying
    @Query(value = "INSERT INTO music.dislikes (user_id, song_id, created_by) VALUES (:userId, :songId, :createdBy)",
            nativeQuery = true)
    void insertDislike(
            @Param("userId") UUID userId,
            @Param("songId") UUID songId,
            @Param("createdBy") String createdBy);

    default void insertDislike(UUID userId, UUID songId) {
        insertDislike(userId, songId, "system");
    }
}
