package com.vjiki.music.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vjiki.music.entity.TrackTag;
import com.vjiki.music.entity.TrackTagId;

@Repository
public interface TrackTagRepository extends JpaRepository<TrackTag, TrackTagId> {

    interface TrackTagProjection {
        UUID getTrackId();

        String getName();

        Double getWeight();
    }

    interface TrackTagsJsonProjection {
        UUID getTrackId();

        String getTags();
    }

    @Query(value = """
            SELECT
                tt.track_id AS trackId,
                t.name      AS name,
                tt.weight   AS weight
            FROM music.track_tag tt
            JOIN music.tag t ON t.id = tt.tag_id
            WHERE tt.track_id IN (:trackIds)
            ORDER BY tt.track_id, tt.weight DESC
            """, nativeQuery = true)
    List<TrackTagProjection> findTagsByTrackIds(@Param("trackIds") Collection<UUID> trackIds);

    @Query(value = """
            SELECT
                tt.track_id AS trackId,
                COALESCE(
                    jsonb_agg(
                        jsonb_build_object('name', t.name, 'weight', tt.weight)
                        ORDER BY tt.weight DESC
                    ),
                    '[]'::jsonb
                )::text AS tags
            FROM music.track_tag tt
            JOIN music.tag t ON t.id = tt.tag_id
            WHERE tt.track_id IN (:trackIds)
            GROUP BY tt.track_id
            """, nativeQuery = true)
    List<TrackTagsJsonProjection> findTagsJsonByTrackIds(@Param("trackIds") Collection<UUID> trackIds);

    @Modifying
    @Query(value = """
            INSERT INTO music.track_tag (track_id, tag_id, weight, source)
            VALUES (:trackId, :tagId, :weight, :source)
            ON CONFLICT (track_id, tag_id) DO UPDATE
            SET weight = EXCLUDED.weight,
                source = EXCLUDED.source
            """, nativeQuery = true)
    void upsertTrackTag(
            @Param("trackId") UUID trackId,
            @Param("tagId") UUID tagId,
            @Param("weight") Double weight,
            @Param("source") String source);
}
