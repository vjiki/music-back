package com.vjiki.music.controller;

import com.vjiki.music.dto.PlaylistResponse;
import com.vjiki.music.dto.PlaylistWithSongsResponse;
import com.vjiki.music.service.PlaylistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/playlists")
public class PlaylistController {

    private final PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PlaylistResponse>> getPlaylistsByUserId(@PathVariable UUID userId) {
        List<PlaylistResponse> playlists = playlistService.getPlaylistsByUserId(userId);
        return ResponseEntity.ok(playlists);
    }

    @GetMapping("/{playlistId}")
    public ResponseEntity<PlaylistWithSongsResponse> getPlaylistWithSongs(@PathVariable UUID playlistId) {
        PlaylistWithSongsResponse playlist = playlistService.getPlaylistWithSongs(playlistId);
        return ResponseEntity.ok(playlist);
    }
}

