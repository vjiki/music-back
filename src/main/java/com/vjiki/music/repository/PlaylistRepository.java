package com.vjiki.music.repository;

import com.vjiki.music.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
}

