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
}


