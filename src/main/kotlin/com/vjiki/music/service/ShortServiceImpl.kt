package com.vjiki.music.service

import com.vjiki.music.dto.ShortResponse
import com.vjiki.music.mapper.ShortMapper.toShortResponse
import com.vjiki.music.repository.DislikeRepository
import com.vjiki.music.repository.LikeRepository
import com.vjiki.music.repository.SongRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ShortServiceImpl(
    private val songRepository: SongRepository,
    private val likeRepository: LikeRepository,
    private val dislikeRepository: DislikeRepository
) : ShortService {

    override fun getShorts(userId: UUID): List<ShortResponse> {
        val songs = songRepository.findAllShorts()
        return songs.map { song ->
            val isLiked = likeRepository.existsByUserIdAndSongIdAndRevokedAtIsNull(userId, song.id)
            val isDisliked = dislikeRepository.existsByUserIdAndSongIdAndRevokedAtIsNull(userId, song.id)
            val likesCount = likeRepository.countBySongIdAndRevokedAtIsNull(song.id)
            val dislikesCount = dislikeRepository.countBySongIdAndRevokedAtIsNull(song.id)
            
            song.toShortResponse(
                isLiked = isLiked,
                isDisliked = isDisliked,
                likesCount = likesCount,
                dislikesCount = dislikesCount
            )
        }
    }
}

