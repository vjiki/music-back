package com.vjiki.music.service;

import com.vjiki.music.dto.PlaylistResponse;
import com.vjiki.music.dto.PlaylistWithSongsResponse;

import java.util.List;
import java.util.UUID;

public interface PlaylistService {
    List<PlaylistResponse> getPlaylistsByUserId(UUID userId);
    PlaylistWithSongsResponse getPlaylistWithSongs(UUID playlistId);
}

