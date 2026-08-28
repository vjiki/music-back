package com.vjiki.music.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vjiki.music.entity.Playlist;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, UUID> {

    @Query("SELECT p FROM Playlist p WHERE p.user.id = :userId ORDER BY p.createdAt DESC")
    List<Playlist> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT p FROM Playlist p LEFT JOIN FETCH p.user WHERE p.user.id = :userId ORDER BY p.createdAt DESC")
    List<Playlist> findByUserIdWithUser(@Param("userId") UUID userId);

    @Query("SELECT p FROM Playlist p WHERE p.user.id = :userId AND p.type = :type")
    Optional<Playlist> findByUserIdAndType(@Param("userId") UUID userId, @Param("type") String type);

    @Query("SELECT p FROM Playlist p WHERE p.user.id = :userId AND p.name = :name")
    Optional<Playlist> findByUserIdAndName(@Param("userId") UUID userId, @Param("name") String name);

    @Modifying
    @Query(value = """
            INSERT INTO music.playlists (user_id, name, description, cover_url, type, is_public, created_by, modified_by)
            SELECT :userId, :name, :description, :coverUrl, :type, :isPublic, :createdBy, :modifiedBy
            WHERE NOT EXISTS (
                SELECT 1
                FROM music.playlists p
                WHERE p.user_id = :userId
                  AND p.name = :name
            )
            """, nativeQuery = true)
    int insertPlaylistIfMissing(
            @Param("userId") UUID userId,
            @Param("name") String name,
            @Param("description") String description,
            @Param("coverUrl") String coverUrl,
            @Param("type") String type,
            @Param("isPublic") boolean isPublic,
            @Param("createdBy") String createdBy,
            @Param("modifiedBy") String modifiedBy);

    default int insertPlaylistIfMissing(
            UUID userId,
            String name,
            String description,
            String coverUrl,
            String type,
            boolean isPublic) {
        return insertPlaylistIfMissing(userId, name, description, coverUrl, type, isPublic, "system", "system");
    }
}
