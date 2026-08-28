package com.vjiki.music.controller

import com.vjiki.music.dto.BandResponse
import com.vjiki.music.pagination.CursorPageResponse
import com.vjiki.music.service.BandService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/bands")
class BandController(
    private val bandService: BandService
) {
    /**
     * Cursor-based pagination for FE (music-ios / music-ui).
     *
     * Example:
     * GET /api/v1/bands/page?userId=<uuid>&name=<bandName>&limit=20
     * GET /api/v1/bands/page?userId=<uuid>&name=<bandName>&limit=20&cursor=<nextCursor>
     */
    @GetMapping("/page")
    fun getBandsPage(
        @RequestParam userId: UUID,
        @RequestParam name: String,
        @RequestParam(required = false, defaultValue = "20") limit: Int,
        @RequestParam(required = false) cursor: String?
    ): ResponseEntity<CursorPageResponse<BandResponse>> {
        return ResponseEntity.ok(bandService.getBandsPage(userId = userId, name = name, limit = limit, cursor = cursor))
    }
}


