package com.vjiki.music.service

import com.vjiki.music.dto.SongResponse
import com.vjiki.music.mapper.SongMapper.toResponse
import com.vjiki.music.pagination.CreatedAtIdCursorCodec
import com.vjiki.music.pagination.CursorPageResponse
import com.vjiki.music.repository.DislikeRepository
import com.vjiki.music.repository.LikeRepository
import com.vjiki.music.repository.SongRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SongServiceImpl(
    private val songRepository: SongRepository,
    private val likeRepository: LikeRepository,
    private val dislikeRepository: DislikeRepository,
    private val tagLookupService: TagLookupService
) : SongService {

    override fun getSongs(userId: UUID): List<SongResponse> {
        val songs = songRepository.findAllActive()
        if (songs.isEmpty()) return emptyList()

        val songIds = songs.map { it.id }
        val tagsBySongId = tagLookupService.getTagsByTrackIds(songIds)
        val likedIds = likeRepository.findActiveLikedSongIds(userId, songIds).toSet()
        val dislikedIds = dislikeRepository.findActiveDislikedSongIds(userId, songIds).toSet()
        val likesCounts = likeRepository.countActiveLikesBySongIds(songIds).associate { it.songId to it.cnt }
        val dislikesCounts = dislikeRepository.countActiveDislikesBySongIds(songIds).associate { it.songId to it.cnt }

        return songs.map { song ->
            song.toResponse(
                isLiked = likedIds.contains(song.id),
                isDisliked = dislikedIds.contains(song.id),
                likesCount = likesCounts[song.id] ?: 0L,
                dislikesCount = dislikesCounts[song.id] ?: 0L
            ).copy(tags = tagsBySongId[song.id] ?: emptyList())
        }
    }

    override fun getSongsPage(userId: UUID, limit: Int, cursor: String?): CursorPageResponse<SongResponse> {
        val safeLimit = limit.coerceIn(1, 100)
        val pageable = PageRequest.of(0, safeLimit + 1)

        val decoded = CreatedAtIdCursorCodec.decodeOrBadRequest(cursor)
        val songs = if (decoded == null) {
            songRepository.findActiveSongsPageFirst(pageable)
        } else {
            songRepository.findActiveSongsPageAfter(
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

        return CursorPageResponse(
            items = items,
            nextCursor = nextCursor,
            hasNext = hasNext
        )
    }
}

