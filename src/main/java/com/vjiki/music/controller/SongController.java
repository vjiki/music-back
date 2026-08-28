package com.vjiki.music.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vjiki.music.dto.SongResponse;
import com.vjiki.music.pagination.CursorPageResponse;
import com.vjiki.music.service.SongService;

@RestController
@RequestMapping("/api/v1/songs")
public class SongController {

    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<SongResponse>> getSongs(@PathVariable UUID userId) {
        return ResponseEntity.ok(songService.getSongs(userId));
    }

    /**
     * Cursor-based pagination for FE (music-ios / music-ui).
     *
     * Example:
     * GET /api/v1/songs/{userId}/page?limit=20
     * GET /api/v1/songs/{userId}/page?limit=20&cursor=&lt;nextCursor&gt;
     */
    @GetMapping("/{userId}/page")
    public ResponseEntity<CursorPageResponse<SongResponse>> getSongsPage(
            @PathVariable UUID userId,
            @RequestParam(required = false, defaultValue = "20") int limit,
            @RequestParam(required = false) String cursor) {
        return ResponseEntity.ok(songService.getSongsPage(userId, limit, cursor));
    }
}
