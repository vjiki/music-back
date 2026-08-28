package com.vjiki.music.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vjiki.music.dto.PlaylistResponse;
import com.vjiki.music.dto.PlaylistWithSongsResponse;
import com.vjiki.music.service.PlaylistService;

@RestController
@RequestMapping("/api/v1/playlists")
public class PlaylistController {

    private final PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PlaylistResponse>> getPlaylistsByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(playlistService.getPlaylistsByUserId(userId));
    }

    @GetMapping("/{playlistId}")
    public ResponseEntity<PlaylistWithSongsResponse> getPlaylistWithSongs(@PathVariable UUID playlistId) {
        return ResponseEntity.ok(playlistService.getPlaylistWithSongs(playlistId));
    }
}
