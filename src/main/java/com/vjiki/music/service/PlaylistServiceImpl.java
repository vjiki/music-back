package com.vjiki.music.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.vjiki.music.dto.PlaylistResponse;
import com.vjiki.music.dto.PlaylistSongResponse;
import com.vjiki.music.dto.PlaylistWithSongsResponse;
import com.vjiki.music.dto.TagResponse;
import com.vjiki.music.entity.Playlist;
import com.vjiki.music.entity.PlaylistSong;
import com.vjiki.music.mapper.PlaylistMapper;
import com.vjiki.music.mapper.PlaylistSongMapper;
import com.vjiki.music.repository.PlaylistRepository;
import com.vjiki.music.repository.PlaylistSongRepository;

@Service
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistSongRepository playlistSongRepository;
    private final TagLookupService tagLookupService;

    public PlaylistServiceImpl(PlaylistRepository playlistRepository,
                               PlaylistSongRepository playlistSongRepository,
                               TagLookupService tagLookupService) {
        this.playlistRepository = playlistRepository;
        this.playlistSongRepository = playlistSongRepository;
        this.tagLookupService = tagLookupService;
    }

    @Override
    public List<PlaylistResponse> getPlaylistsByUserId(UUID userId) {
        return playlistRepository.findByUserIdWithUser(userId).stream()
                .map(PlaylistMapper::toResponse)
                .toList();
    }

    @Override
    public PlaylistWithSongsResponse getPlaylistWithSongs(UUID playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("Playlist not found: " + playlistId));

        List<PlaylistSong> playlistSongs = playlistSongRepository.findByPlaylistIdWithSong(playlistId);

        List<UUID> songIds = playlistSongs.stream()
                .map(ps -> ps.getSong() == null ? null : ps.getSong().getId())
                .filter(java.util.Objects::nonNull)
                .toList();
        Map<UUID, List<TagResponse>> tagsBySongId = tagLookupService.getTagsByTrackIds(songIds);

        List<PlaylistSongResponse> songsWithTags = new ArrayList<>(playlistSongs.size());
        for (PlaylistSong playlistSong : playlistSongs) {
            PlaylistSongResponse response = PlaylistSongMapper.toResponse(playlistSong);
            response.setTags(tagsBySongId.getOrDefault(response.getSongId(), List.of()));
            songsWithTags.add(response);
        }

        PlaylistResponse playlistResponse = PlaylistMapper.toResponse(playlist);

        return PlaylistWithSongsResponse.builder()
                .id(playlistResponse.getId())
                .userId(playlistResponse.getUserId())
                .userName(playlistResponse.getUserName())
                .userNickname(playlistResponse.getUserNickname())
                .name(playlistResponse.getName())
                .description(playlistResponse.getDescription())
                .coverUrl(playlistResponse.getCoverUrl())
                .type(playlistResponse.getType())
                .isPublic(playlistResponse.getIsPublic())
                .createdAt(playlistResponse.getCreatedAt())
                .modifiedAt(playlistResponse.getModifiedAt())
                .songs(songsWithTags)
                .build();
    }
}
