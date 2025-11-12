package com.vjiki.music.service;

import com.vjiki.music.dto.PlaylistResponse;
import com.vjiki.music.dto.PlaylistSongResponse;
import com.vjiki.music.dto.PlaylistWithSongsResponse;
import com.vjiki.music.entity.Playlist;
import com.vjiki.music.mapper.PlaylistMapper;
import com.vjiki.music.mapper.PlaylistSongMapper;
import com.vjiki.music.repository.PlaylistRepository;
import com.vjiki.music.repository.PlaylistSongRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistSongRepository playlistSongRepository;
    private final PlaylistMapper playlistMapper;
    private final PlaylistSongMapper playlistSongMapper;

    public PlaylistServiceImpl(
            PlaylistRepository playlistRepository,
            PlaylistSongRepository playlistSongRepository,
            PlaylistMapper playlistMapper,
            PlaylistSongMapper playlistSongMapper) {
        this.playlistRepository = playlistRepository;
        this.playlistSongRepository = playlistSongRepository;
        this.playlistMapper = playlistMapper;
        this.playlistSongMapper = playlistSongMapper;
    }

    @Override
    public List<PlaylistResponse> getPlaylistsByUserId(UUID userId) {
        List<Playlist> playlists = playlistRepository.findByUserIdWithUser(userId);
        return playlists.stream()
                .map(playlistMapper::toResponse)
                .toList();
    }

    @Override
    public PlaylistWithSongsResponse getPlaylistWithSongs(UUID playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("Playlist not found: " + playlistId));

        List<PlaylistSongResponse> songs = playlistSongRepository.findByPlaylistIdWithSong(playlistId)
                .stream()
                .map(playlistSongMapper::toResponse)
                .toList();

        PlaylistResponse playlistResponse = playlistMapper.toResponse(playlist);

        return new PlaylistWithSongsResponse(
                playlistResponse.getId(),
                playlistResponse.getUserId(),
                playlistResponse.getUserName(),
                playlistResponse.getUserNickname(),
                playlistResponse.getName(),
                playlistResponse.getDescription(),
                playlistResponse.getCoverUrl(),
                playlistResponse.getType(),
                playlistResponse.getIsPublic(),
                playlistResponse.getCreatedAt(),
                playlistResponse.getModifiedAt(),
                songs
        );
    }
}

