package com.vjiki.music.repository;

import com.vjiki.music.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SongRepository extends JpaRepository<Song, UUID> {
    
    @Query("SELECT s FROM Song s WHERE s.active = true ORDER BY s.createdAt DESC")
    List<Song> findAllActive();
}

