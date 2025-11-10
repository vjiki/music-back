package com.vjiki.music.controller;

import com.vjiki.music.dto.SongResponse;
import com.vjiki.music.service.SongService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/songs")
public class SongController {

    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<SongResponse>> getSongs(@PathVariable String userId) {
        List<SongResponse> songs = songService.getSongs(userId);
        return ResponseEntity.ok(songs);
    }
}

