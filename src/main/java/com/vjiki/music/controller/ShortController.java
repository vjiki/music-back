package com.vjiki.music.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vjiki.music.dto.ShortResponse;
import com.vjiki.music.pagination.CursorPageResponse;
import com.vjiki.music.service.ShortService;

@RestController
@RequestMapping("/api/v1/shorts")
public class ShortController {

    private final ShortService shortService;

    public ShortController(ShortService shortService) {
        this.shortService = shortService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<ShortResponse>> getShorts(@PathVariable UUID userId) {
        return ResponseEntity.ok(shortService.getShorts(userId));
    }

    /**
     * Cursor-based pagination for FE
     *
     * Example:
     * GET /api/v1/shorts/{userId}/page?limit=20
     * GET /api/v1/shorts/{userId}/page?limit=20&cursor=&lt;nextCursor&gt;
     */
    @GetMapping("/{userId}/page")
    public ResponseEntity<CursorPageResponse<ShortResponse>> getShortsPage(
            @PathVariable UUID userId,
            @RequestParam(required = false, defaultValue = "20") int limit,
            @RequestParam(required = false) String cursor) {
        return ResponseEntity.ok(shortService.getShortsPage(userId, limit, cursor));
    }
}
