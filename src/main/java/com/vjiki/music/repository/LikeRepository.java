package com.vjiki.music.repository;

import com.vjiki.music.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LikeRepository extends JpaRepository<Like, UUID> {

    @Query("SELECT COUNT(l) > 0 FROM Like l WHERE l.user.id = :userId AND l.song.id = :songId AND l.revokedAt IS NULL")
    boolean existsByUserIdAndSongIdAndRevokedAtIsNull(@Param("userId") UUID userId, @Param("songId") UUID songId);

    @Query("SELECT l FROM Like l WHERE l.user.id = :userId AND l.song.id = :songId AND l.revokedAt IS NULL")
    Optional<Like> findByUserIdAndSongIdAndRevokedAtIsNull(@Param("userId") UUID userId, @Param("songId") UUID songId);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.song.id = :songId AND l.revokedAt IS NULL")
    long countBySongIdAndRevokedAtIsNull(@Param("songId") UUID songId);
}

