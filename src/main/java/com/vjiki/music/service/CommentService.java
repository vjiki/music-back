package com.vjiki.music.service;

import java.util.List;
import java.util.UUID;

import com.vjiki.music.dto.CommentReactionRequest;
import com.vjiki.music.dto.CommentRequest;
import com.vjiki.music.dto.CommentResponse;
import com.vjiki.music.pagination.CursorPageResponse;

public interface CommentService {

    CommentResponse addComment(CommentRequest request);

    List<CommentResponse> getCommentsByTrackId(UUID trackId, UUID userId);

    CursorPageResponse<CommentResponse> getCommentsPage(UUID trackId, UUID userId, int limit, String cursor);

    void addReaction(CommentReactionRequest request);

    void removeReaction(UUID commentId, UUID userId);

    void deleteComment(UUID commentId);
}
