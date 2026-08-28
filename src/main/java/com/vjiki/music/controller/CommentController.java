package com.vjiki.music.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vjiki.music.dto.CommentReactionRequest;
import com.vjiki.music.dto.CommentRequest;
import com.vjiki.music.dto.CommentResponse;
import com.vjiki.music.pagination.CursorPageResponse;
import com.vjiki.music.service.CommentService;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Add a comment to a track
     * POST /api/v1/comments
     */
    @PostMapping
    public ResponseEntity<CommentResponse> addComment(@RequestBody CommentRequest request) {
        return ResponseEntity.ok(commentService.addComment(request));
    }

    /**
     * Get all comments for a track
     * GET /api/v1/comments/track/{trackId}?userId={userId}
     * userId is optional - if provided, will include isLiked information
     */
    @GetMapping("/track/{trackId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByTrackId(
            @PathVariable UUID trackId,
            @RequestParam(required = false) UUID userId) {
        return ResponseEntity.ok(commentService.getCommentsByTrackId(trackId, userId));
    }

    /**
     * Get comments for a track with cursor-based pagination
     * GET /api/v1/comments/track/{trackId}/page?userId={userId}&limit=20&cursor={cursor}
     * userId is optional - if provided, will include isLiked information
     * limit is optional (default 20, clamped to 1-100)
     * cursor is optional - opaque string from previous page's nextCursor
     */
    @GetMapping("/track/{trackId}/page")
    public ResponseEntity<CursorPageResponse<CommentResponse>> getCommentsPage(
            @PathVariable UUID trackId,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false, defaultValue = "20") int limit,
            @RequestParam(required = false) String cursor) {
        return ResponseEntity.ok(commentService.getCommentsPage(trackId, userId, limit, cursor));
    }

    /**
     * Add a reaction (like) to a comment
     * POST /api/v1/comments/reactions
     */
    @PostMapping("/reactions")
    public ResponseEntity<Void> addReaction(@RequestBody CommentReactionRequest request) {
        commentService.addReaction(request);
        return ResponseEntity.ok().build();
    }

    /**
     * Remove a reaction (unlike) from a comment
     * DELETE /api/v1/comments/{commentId}/reactions/{userId}
     */
    @DeleteMapping("/{commentId}/reactions/{userId}")
    public ResponseEntity<Void> removeReaction(
            @PathVariable UUID commentId,
            @PathVariable UUID userId) {
        commentService.removeReaction(commentId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Delete a comment (soft delete)
     * DELETE /api/v1/comments/{commentId}
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }
}
