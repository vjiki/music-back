package com.vjiki.music.controller

import com.vjiki.music.dto.CommentRequest
import com.vjiki.music.dto.CommentReactionRequest
import com.vjiki.music.dto.CommentResponse
import com.vjiki.music.pagination.CursorPageResponse
import com.vjiki.music.service.CommentService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/comments")
class CommentController(
    private val commentService: CommentService
) {

    /**
     * Add a comment to a track
     * POST /api/v1/comments
     */
    @PostMapping
    fun addComment(@RequestBody request: CommentRequest): ResponseEntity<CommentResponse> {
        val comment = commentService.addComment(request)
        return ResponseEntity.ok(comment)
    }

    /**
     * Get all comments for a track
     * GET /api/v1/comments/track/{trackId}?userId={userId}
     * userId is optional - if provided, will include isLiked information
     */
    @GetMapping("/track/{trackId}")
    fun getCommentsByTrackId(
        @PathVariable trackId: UUID,
        @RequestParam(required = false) userId: UUID?
    ): ResponseEntity<List<CommentResponse>> {
        val comments = commentService.getCommentsByTrackId(trackId, userId)
        return ResponseEntity.ok(comments)
    }

    /**
     * Get comments for a track with cursor-based pagination
     * GET /api/v1/comments/track/{trackId}/page?userId={userId}&limit=20&cursor={cursor}
     * userId is optional - if provided, will include isLiked information
     * limit is optional (default 20, clamped to 1-100)
     * cursor is optional - opaque string from previous page's nextCursor
     */
    @GetMapping("/track/{trackId}/page")
    fun getCommentsPage(
        @PathVariable trackId: UUID,
        @RequestParam(required = false) userId: UUID?,
        @RequestParam(required = false, defaultValue = "20") limit: Int,
        @RequestParam(required = false) cursor: String?
    ): ResponseEntity<CursorPageResponse<CommentResponse>> {
        val page = commentService.getCommentsPage(trackId, userId, limit, cursor)
        return ResponseEntity.ok(page)
    }

    /**
     * Add a reaction (like) to a comment
     * POST /api/v1/comments/reactions
     */
    @PostMapping("/reactions")
    fun addReaction(@RequestBody request: CommentReactionRequest): ResponseEntity<Void> {
        commentService.addReaction(request)
        return ResponseEntity.ok().build()
    }

    /**
     * Remove a reaction (unlike) from a comment
     * DELETE /api/v1/comments/{commentId}/reactions/{userId}
     */
    @DeleteMapping("/{commentId}/reactions/{userId}")
    fun removeReaction(
        @PathVariable commentId: UUID,
        @PathVariable userId: UUID
    ): ResponseEntity<Void> {
        commentService.removeReaction(commentId, userId)
        return ResponseEntity.ok().build()
    }

    /**
     * Delete a comment (soft delete)
     * DELETE /api/v1/comments/{commentId}
     */
    @DeleteMapping("/{commentId}")
    fun deleteComment(@PathVariable commentId: UUID): ResponseEntity<Void> {
        commentService.deleteComment(commentId)
        return ResponseEntity.ok().build()
    }
}
