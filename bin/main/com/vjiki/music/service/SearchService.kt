package com.vjiki.music.service

import com.vjiki.music.dto.SongResponse
import com.vjiki.music.mapper.SongMapper.toResponse
import com.vjiki.music.pagination.CreatedAtIdCursorCodec
import com.vjiki.music.pagination.CursorPageResponse
import com.vjiki.music.repository.DislikeRepository
import com.vjiki.music.repository.LikeRepository
import com.vjiki.music.repository.SongRepository
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Service
class SearchService(
    private val songRepository: SongRepository,
    private val likeRepository: LikeRepository,
    private val dislikeRepository: DislikeRepository,
    private val tagLookupService: TagLookupService
) {
    fun searchSongs(userId: UUID, q: String, limit: Int, cursor: String?): CursorPageResponse<SongResponse> {
        val query = q.trim()
        if (query.isBlank()) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "q is required")

        val safeLimit = limit.coerceIn(1, 100)
        val pageable = PageRequest.of(0, safeLimit + 1)

        val decoded = CreatedAtIdCursorCodec.decodeOrBadRequest(cursor)
        val songs = if (decoded == null) {
            songRepository.searchSongsFirst(q = query, pageable = pageable)
        } else {
            songRepository.searchSongsAfter(
                q = query,
                cursorCreatedAt = decoded.createdAt,
                cursorId = decoded.id,
                pageable = pageable
            )
        }

        val hasNext = songs.size > safeLimit
        val slice = if (hasNext) songs.take(safeLimit) else songs

        val songIds = slice.map { it.id }
        val tagsBySongId = tagLookupService.getTagsByTrackIds(songIds)
        val likedIds = if (songIds.isEmpty()) emptySet() else likeRepository.findActiveLikedSongIds(userId, songIds).toSet()
        val dislikedIds = if (songIds.isEmpty()) emptySet() else dislikeRepository.findActiveDislikedSongIds(userId, songIds).toSet()
        val likesCounts = if (songIds.isEmpty()) emptyMap() else likeRepository.countActiveLikesBySongIds(songIds).associate { it.songId to it.cnt }
        val dislikesCounts = if (songIds.isEmpty()) emptyMap() else dislikeRepository.countActiveDislikesBySongIds(songIds).associate { it.songId to it.cnt }

        val items = slice.map { song ->
            song.toResponse(
                isLiked = likedIds.contains(song.id),
                isDisliked = dislikedIds.contains(song.id),
                likesCount = likesCounts[song.id] ?: 0L,
                dislikesCount = dislikesCounts[song.id] ?: 0L
            ).copy(tags = tagsBySongId[song.id] ?: emptyList())
        }

        val last = slice.lastOrNull()
        val nextCursor = if (hasNext && last?.createdAt != null) {
            CreatedAtIdCursorCodec.encode(last.createdAt, last.id)
        } else {
            null
        }

        return CursorPageResponse(items = items, nextCursor = nextCursor, hasNext = hasNext)
    }
}


