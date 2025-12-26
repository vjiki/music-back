package com.vjiki.music.repository

import java.util.UUID

/**
 * Spring Data projection for queries like:
 *  SELECT x.song.id AS songId, COUNT(x) AS cnt ...
 */
interface SongIdCountProjection {
    val songId: UUID
    val cnt: Long
}


