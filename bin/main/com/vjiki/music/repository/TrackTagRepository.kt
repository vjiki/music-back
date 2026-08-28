package com.vjiki.music.repository

import com.vjiki.music.entity.TrackTag
import com.vjiki.music.entity.TrackTagId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface TrackTagRepository : JpaRepository<TrackTag, TrackTagId> {

    interface TrackTagProjection {
        val trackId: UUID
        val name: String
        val weight: Double
    }

    interface TrackTagsJsonProjection {
        val trackId: UUID
        val tags: String
    }

    @Query(
        value = """
            SELECT
                tt.track_id AS trackId,
                t.name      AS name,
                tt.weight   AS weight
            FROM music.track_tag tt
            JOIN music.tag t ON t.id = tt.tag_id
            WHERE tt.track_id IN (:trackIds)
            ORDER BY tt.track_id, tt.weight DESC
        """,
        nativeQuery = true
    )
    fun findTagsByTrackIds(@Param("trackIds") trackIds: Collection<UUID>): List<TrackTagProjection>

    @Query(
        value = """
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
        """,
        nativeQuery = true
    )
    fun findTagsJsonByTrackIds(@Param("trackIds") trackIds: Collection<UUID>): List<TrackTagsJsonProjection>

    @Query(
        value = """
            INSERT INTO music.track_tag (track_id, tag_id, weight, source)
            VALUES (:trackId, :tagId, :weight, :source)
            ON CONFLICT (track_id, tag_id) DO UPDATE
            SET weight = EXCLUDED.weight,
                source = EXCLUDED.source
        """,
        nativeQuery = true
    )
    fun upsertTrackTag(
        @Param("trackId") trackId: UUID,
        @Param("tagId") tagId: UUID,
        @Param("weight") weight: Double,
        @Param("source") source: String
    )
}


