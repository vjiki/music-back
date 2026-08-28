package com.vjiki.music.service

import com.vjiki.music.dto.CommentRequest
import com.vjiki.music.dto.CommentReactionRequest
import com.vjiki.music.dto.CommentResponse
import com.vjiki.music.pagination.CursorPageResponse
import java.util.UUID

interface CommentService {
    fun addComment(request: CommentRequest): CommentResponse
    fun getCommentsByTrackId(trackId: UUID, userId: UUID?): List<CommentResponse>
    fun getCommentsPage(trackId: UUID, userId: UUID?, limit: Int, cursor: String?): CursorPageResponse<CommentResponse>
    fun addReaction(request: CommentReactionRequest)
    fun removeReaction(commentId: UUID, userId: UUID)
    fun deleteComment(commentId: UUID)
}
