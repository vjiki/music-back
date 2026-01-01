package com.vjiki.music.service

import com.vjiki.music.dto.PlaylistResponse
import com.vjiki.music.dto.PlaylistSongResponse
import com.vjiki.music.dto.PlaylistWithSongsResponse
import com.vjiki.music.mapper.PlaylistMapper.toResponse
import com.vjiki.music.mapper.PlaylistSongMapper.toResponse as playlistSongToResponse
import com.vjiki.music.repository.PlaylistRepository
import com.vjiki.music.repository.PlaylistSongRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class PlaylistServiceImpl(
    private val playlistRepository: PlaylistRepository,
    private val playlistSongRepository: PlaylistSongRepository,
    private val tagLookupService: TagLookupService
) : PlaylistService {

    override fun getPlaylistsByUserId(userId: UUID): List<PlaylistResponse> {
        return playlistRepository.findByUserIdWithUser(userId).map { it.toResponse() }
    }

    override fun getPlaylistWithSongs(playlistId: UUID): PlaylistWithSongsResponse {
        val playlist = playlistRepository.findById(playlistId)
            .orElseThrow { IllegalArgumentException("Playlist not found: $playlistId") }

        val playlistSongs = playlistSongRepository.findByPlaylistIdWithSong(playlistId)
        val songs = playlistSongs.map { it.playlistSongToResponse() }

        val songIds = playlistSongs.map { it.song.id }
        val tagsBySongId = tagLookupService.getTagsByTrackIds(songIds)
        val songsWithTags = songs.map { ps ->
            ps.copy(tags = tagsBySongId[ps.songId] ?: emptyList())
        }

        val playlistResponse = playlist.toResponse()

        return PlaylistWithSongsResponse(
            id = playlistResponse.id,
            userId = playlistResponse.userId,
            userName = playlistResponse.userName,
            userNickname = playlistResponse.userNickname,
            name = playlistResponse.name,
            description = playlistResponse.description,
            coverUrl = playlistResponse.coverUrl,
            type = playlistResponse.type,
            isPublic = playlistResponse.isPublic,
            createdAt = playlistResponse.createdAt,
            modifiedAt = playlistResponse.modifiedAt,
            songs = songsWithTags
        )
    }
}

