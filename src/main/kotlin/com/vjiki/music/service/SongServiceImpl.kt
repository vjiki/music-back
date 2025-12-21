package com.vjiki.music.service

import com.vjiki.music.dto.SongResponse
import com.vjiki.music.mapper.SongMapper.toResponse
import com.vjiki.music.repository.DislikeRepository
import com.vjiki.music.repository.LikeRepository
import com.vjiki.music.repository.SongRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SongServiceImpl(
    private val songRepository: SongRepository,
    private val likeRepository: LikeRepository,
    private val dislikeRepository: DislikeRepository
) : SongService {

    override fun getSongs(userId: UUID): List<SongResponse> {
        val songs = songRepository.findAllActive()
        return songs.map { song ->
            val isLiked = likeRepository.existsByUserIdAndSongIdAndRevokedAtIsNull(userId, song.id)
            val isDisliked = dislikeRepository.existsByUserIdAndSongIdAndRevokedAtIsNull(userId, song.id)
            val likesCount = likeRepository.countBySongIdAndRevokedAtIsNull(song.id)
            val dislikesCount = dislikeRepository.countBySongIdAndRevokedAtIsNull(song.id)
            
            song.toResponse(
                isLiked = isLiked,
                isDisliked = isDisliked,
                likesCount = likesCount,
                dislikesCount = dislikesCount
            )
        }
    }
}

