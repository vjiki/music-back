package com.vjiki.music.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vjiki.music.entity.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {

    @Query(value = """
            SELECT *
            FROM music.tag
            WHERE name = :name
              AND type = :type
            LIMIT 1
            """, nativeQuery = true)
    Tag findOneByNameAndType(@Param("name") String name, @Param("type") String type);
}
