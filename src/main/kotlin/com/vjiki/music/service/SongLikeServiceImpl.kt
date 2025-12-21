package com.vjiki.music.service

import com.vjiki.music.dto.SongLikeResponse
import com.vjiki.music.entity.Dislike
import com.vjiki.music.entity.Like
import com.vjiki.music.entity.Playlist
import com.vjiki.music.entity.PlaylistSong
import com.vjiki.music.entity.Song
import com.vjiki.music.entity.User
import com.vjiki.music.repository.DislikeRepository
import com.vjiki.music.repository.LikeRepository
import com.vjiki.music.repository.PlaylistRepository
import com.vjiki.music.repository.PlaylistSongRepository
import com.vjiki.music.repository.SongRepository
import com.vjiki.music.repository.UserRepository
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.UUID

@Service
class SongLikeServiceImpl(
    private val likeRepository: LikeRepository,
    private val dislikeRepository: DislikeRepository,
    private val songRepository: SongRepository,
    private val userRepository: UserRepository,
    private val playlistRepository: PlaylistRepository,
    private val playlistSongRepository: PlaylistSongRepository,
    private val entityManager: EntityManager
) : SongLikeService {

    companion object {
        private const val DEFAULT_LIKES_PLAYLIST_NAME = "DEFAULT_LIKES"
        private const val DEFAULT_DISLIKES_PLAYLIST_NAME = "DEFAULT_DISLIKES"
    }

    @Transactional
    override fun likeSong(userId: UUID, songId: UUID) {
        val existingDislike = dislikeRepository.findByUserIdAndSongIdAndRevokedAtIsNull(userId, songId)
        if (existingDislike.isPresent) {
            val dislike = existingDislike.get()
            dislike.revokedAt = OffsetDateTime.now()
            dislikeRepository.save(dislike)
            removeSongFromPlaylist(userId, songId, DEFAULT_DISLIKES_PLAYLIST_NAME)
        }

        // Use native SQL query to insert directly into database
        // This bypasses JPA entity issues and lets PostgreSQL generate the UUID and timestamps
        likeRepository.insertLike(userId, songId, "system")
        
        // Use native SQL query to add song to playlist
        playlistSongRepository.addSongToPlaylistIfNotExists(userId, songId, DEFAULT_LIKES_PLAYLIST_NAME)
        updateSongCounts(songId)
    }

    @Transactional
    override fun dislikeSong(userId: UUID, songId: UUID) {
        val existingLike = likeRepository.findByUserIdAndSongIdAndRevokedAtIsNull(userId, songId)
        if (existingLike.isPresent) {
            val like = existingLike.get()
            like.revokedAt = OffsetDateTime.now()
            likeRepository.save(like)
            removeSongFromPlaylist(userId, songId, DEFAULT_LIKES_PLAYLIST_NAME)
        }

        // Use native SQL query to insert directly into database
        // This bypasses JPA entity issues and lets PostgreSQL generate the UUID and timestamps
        dislikeRepository.insertDislike(userId, songId, "system")
        
        // Use native SQL query to add song to playlist
        playlistSongRepository.addSongToPlaylistIfNotExists(userId, songId, DEFAULT_DISLIKES_PLAYLIST_NAME)
        updateSongCounts(songId)
    }

    override fun getLikeDislikeInfo(userId: UUID, songId: UUID): SongLikeResponse {
        val isLiked = likeRepository.existsByUserIdAndSongIdAndRevokedAtIsNull(userId, songId)
        val isDisliked = dislikeRepository.existsByUserIdAndSongIdAndRevokedAtIsNull(userId, songId)
        val likesCount = likeRepository.countBySongIdAndRevokedAtIsNull(songId)
        val dislikesCount = dislikeRepository.countBySongIdAndRevokedAtIsNull(songId)

        return SongLikeResponse(isLiked, isDisliked, likesCount, dislikesCount)
    }

    private fun updateSongCounts(songId: UUID) {
        songRepository.findById(songId).ifPresent { song ->
            val likesCount = likeRepository.countBySongIdAndRevokedAtIsNull(songId)
            val dislikesCount = dislikeRepository.countBySongIdAndRevokedAtIsNull(songId)
            song.likesCount = likesCount
            song.dislikesCount = dislikesCount
            songRepository.save(song)
        }
    }

    private fun findOrCreateDefaultPlaylist(user: User, playlistName: String): Playlist {
        return playlistRepository.findByUserIdAndName(user.id, playlistName)
            .orElseGet {
                val playlist = Playlist(
                    user = user,
                    name = playlistName,
                    type = "DEFAULT",
                    isPublic = false,
                    createdBy = "system",
                    modifiedBy = "system"
                )
                playlistRepository.save(playlist)
            }
    }

    private fun addSongToPlaylist(user: User, song: Song, playlistName: String) {
        val playlist = findOrCreateDefaultPlaylist(user, playlistName)

        if (playlistSongRepository.findByPlaylistIdAndSongId(playlist.id, song.id).isPresent) {
            return // Already in playlist
        }

        val maxPosition = playlistSongRepository.findByPlaylistIdWithSong(playlist.id)
            .map { it.position }
            .maxOrNull() ?: -1

        val playlistSong = PlaylistSong(
            playlist = playlist,
            song = song,
            position = maxPosition + 1,
            addedBy = user
        )
        playlistSongRepository.save(playlistSong)
    }

    private fun removeSongFromPlaylist(userId: UUID, songId: UUID, playlistName: String) {
        playlistRepository.findByUserIdAndName(userId, playlistName).ifPresent { playlist ->
            playlistSongRepository.deleteByPlaylistIdAndSongId(playlist.id, songId)
        }
    }
}

