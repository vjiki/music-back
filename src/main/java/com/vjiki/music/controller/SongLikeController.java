package com.vjiki.music.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vjiki.music.dto.SongLikeRequest;
import com.vjiki.music.service.SongLikeService;

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
}
