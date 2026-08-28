package com.vjiki.music.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vjiki.music.dto.SongResponse;
import com.vjiki.music.pagination.CursorPageResponse;
import com.vjiki.music.service.SearchService;

@RestController
@RequestMapping("/api/v1/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * Search songs by keyword across:
     * - songs.title
     * - songs.artists (jsonb default array)
     * - tag.name (via track_tag)
     *
     * Example:
     * GET /api/v1/search/songs/{userId}?q=chill&limit=20&cursor=...
     */
    @GetMapping("/songs/{userId}")
    public ResponseEntity<CursorPageResponse<SongResponse>> searchSongs(
            @PathVariable UUID userId,
            @RequestParam String q,
            @RequestParam(required = false, defaultValue = "20") int limit,
            @RequestParam(required = false) String cursor) {
        return ResponseEntity.ok(searchService.searchSongs(userId, q, limit, cursor));
    }
}
