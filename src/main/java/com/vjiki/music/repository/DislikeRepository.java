package com.vjiki.music.repository;

import com.vjiki.music.entity.Dislike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DislikeRepository extends JpaRepository<Dislike, UUID> {

    @Query("SELECT COUNT(d) > 0 FROM Dislike d WHERE d.user.id = :userId AND d.song.id = :songId AND d.revokedAt IS NULL")
    boolean existsByUserIdAndSongIdAndRevokedAtIsNull(@Param("userId") UUID userId, @Param("songId") UUID songId);

    @Query("SELECT d FROM Dislike d WHERE d.user.id = :userId AND d.song.id = :songId AND d.revokedAt IS NULL")
    Optional<Dislike> findByUserIdAndSongIdAndRevokedAtIsNull(@Param("userId") UUID userId, @Param("songId") UUID songId);

    @Query("SELECT COUNT(d) FROM Dislike d WHERE d.song.id = :songId AND d.revokedAt IS NULL")
    long countBySongIdAndRevokedAtIsNull(@Param("songId") UUID songId);
}

