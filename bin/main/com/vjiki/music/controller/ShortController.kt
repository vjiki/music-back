package com.vjiki.music.controller

import com.vjiki.music.dto.ShortResponse
import com.vjiki.music.pagination.CursorPageResponse
import com.vjiki.music.service.ShortService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/shorts")
class ShortController(
    private val shortService: ShortService
) {

    @GetMapping("/{userId}")
    fun getShorts(@PathVariable userId: UUID): ResponseEntity<List<ShortResponse>> {
        val shorts = shortService.getShorts(userId)
        return ResponseEntity.ok(shorts)
    }

    /**
     * Cursor-based pagination for FE
     *
     * Example:
     * GET /api/v1/shorts/{userId}/page?limit=20
     * GET /api/v1/shorts/{userId}/page?limit=20&cursor=<nextCursor>
     */
    @GetMapping("/{userId}/page")
    fun getShortsPage(
        @PathVariable userId: UUID,
        @RequestParam(required = false, defaultValue = "20") limit: Int,
        @RequestParam(required = false) cursor: String?
    ): ResponseEntity<CursorPageResponse<ShortResponse>> {
        val page = shortService.getShortsPage(userId = userId, limit = limit, cursor = cursor)
        return ResponseEntity.ok(page)
    }
}

