package com.vjiki.music.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vjiki.music.dto.BandResponse;
import com.vjiki.music.pagination.CursorPageResponse;
import com.vjiki.music.service.BandService;

@RestController
@RequestMapping("/api/v1/bands")
public class BandController {

    private final BandService bandService;

    public BandController(BandService bandService) {
        this.bandService = bandService;
    }

    /**
     * Cursor-based pagination for FE (music-ios / music-ui).
     *
     * Example:
     * GET /api/v1/bands/page?userId=&lt;uuid&gt;&name=&lt;bandName&gt;&limit=20
     * GET /api/v1/bands/page?userId=&lt;uuid&gt;&name=&lt;bandName&gt;&limit=20&cursor=&lt;nextCursor&gt;
     */
    @GetMapping("/page")
    public ResponseEntity<CursorPageResponse<BandResponse>> getBandsPage(
            @RequestParam UUID userId,
            @RequestParam String name,
            @RequestParam(required = false, defaultValue = "20") int limit,
            @RequestParam(required = false) String cursor) {
        return ResponseEntity.ok(bandService.getBandsPage(userId, name, limit, cursor));
    }
}
