package com.vjiki.music.service;

import java.util.List;
import java.util.UUID;

import com.vjiki.music.dto.PlaylistResponse;
import com.vjiki.music.dto.PlaylistWithSongsResponse;

public interface PlaylistService {

    List<PlaylistResponse> getPlaylistsByUserId(UUID userId);

    PlaylistWithSongsResponse getPlaylistWithSongs(UUID playlistId);
}
