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

import com.vjiki.music.entity.Like;

@Repository
public interface LikeRepository extends JpaRepository<Like, UUID> {

    @Query("SELECT COUNT(l) > 0 FROM Like l WHERE l.user.id = :userId AND l.song.id = :songId AND l.revokedAt IS NULL")
    boolean existsByUserIdAndSongIdAndRevokedAtIsNull(@Param("userId") UUID userId, @Param("songId") UUID songId);

    @Query("SELECT l FROM Like l WHERE l.user.id = :userId AND l.song.id = :songId AND l.revokedAt IS NULL")
    Optional<Like> findByUserIdAndSongIdAndRevokedAtIsNull(@Param("userId") UUID userId, @Param("songId") UUID songId);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.song.id = :songId AND l.revokedAt IS NULL")
    long countBySongIdAndRevokedAtIsNull(@Param("songId") UUID songId);

    @Query("""
            SELECT l.song.id
            FROM Like l
            WHERE l.user.id = :userId
              AND l.song.id IN :songIds
              AND l.revokedAt IS NULL
            """)
    List<UUID> findActiveLikedSongIds(
            @Param("userId") UUID userId,
            @Param("songIds") Collection<UUID> songIds);

    @Query("""
            SELECT l.song.id AS songId, COUNT(l) AS cnt
            FROM Like l
            WHERE l.song.id IN :songIds
              AND l.revokedAt IS NULL
            GROUP BY l.song.id
            """)
    List<SongIdCountProjection> countActiveLikesBySongIds(
            @Param("songIds") Collection<UUID> songIds);

    @Modifying
    @Query(value = "INSERT INTO music.likes (user_id, song_id, created_by) VALUES (:userId, :songId, :createdBy)",
            nativeQuery = true)
    void insertLike(
            @Param("userId") UUID userId,
            @Param("songId") UUID songId,
            @Param("createdBy") String createdBy);

    default void insertLike(UUID userId, UUID songId) {
        insertLike(userId, songId, "system");
    }
}
