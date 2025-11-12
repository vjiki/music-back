package com.vjiki.music.controller;

import com.vjiki.music.dto.SongLikeRequest;
import com.vjiki.music.dto.SongLikeResponse;
import com.vjiki.music.service.SongLikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/song-likes")
public class SongLikeController {

    private final SongLikeService songLikeService;

    public SongLikeController(SongLikeService songLikeService) {
        this.songLikeService = songLikeService;
    }

    @PostMapping("/like")
    public ResponseEntity<Void> likeSong(@RequestBody SongLikeRequest request) {
        songLikeService.likeSong(request.getUserId(), request.getSongId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/dislike")
    public ResponseEntity<Void> dislikeSong(@RequestBody SongLikeRequest request) {
        songLikeService.dislikeSong(request.getUserId(), request.getSongId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/song/{songId}/user/{userId}")
    public ResponseEntity<SongLikeResponse> getLikeDislikeInfo(
            @PathVariable UUID songId,
            @PathVariable UUID userId) {
        SongLikeResponse response = songLikeService.getLikeDislikeInfo(userId, songId);
        return ResponseEntity.ok(response);
    }
}

