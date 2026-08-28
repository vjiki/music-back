package com.vjiki.music.service

import com.vjiki.music.dto.CommentRequest
import com.vjiki.music.dto.CommentReactionRequest
import com.vjiki.music.dto.CommentResponse
import com.vjiki.music.entity.TrackComment
import com.vjiki.music.mapper.CommentMapper.toResponse
import com.vjiki.music.pagination.CreatedAtIdCursorCodec
import com.vjiki.music.pagination.CursorPageResponse
import com.vjiki.music.repository.SongRepository
import com.vjiki.music.repository.TrackCommentReactionRepository
import com.vjiki.music.repository.TrackCommentRepository
import com.vjiki.music.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class CommentServiceImpl(
    private val trackCommentRepository: TrackCommentRepository,
    private val trackCommentReactionRepository: TrackCommentReactionRepository,
    private val songRepository: SongRepository,
    private val userRepository: UserRepository
) : CommentService {

    @Transactional
    override fun addComment(request: CommentRequest): CommentResponse {
        // Validate track exists
        if (!songRepository.existsById(request.trackId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Track not found")
        }

        // Validate user exists
        if (!userRepository.existsById(request.userId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        }

        // If parentId is provided, validate parent comment exists and belongs to the same track
        if (request.parentId != null) {
            val parentComment = trackCommentRepository.findById(request.parentId)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Parent comment not found") }
            
            if (parentComment.trackId != request.trackId) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Parent comment does not belong to this track")
            }

            if (parentComment.status != "ACTIVE") {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot reply to inactive comment")
            }

            // Increment parent's replies count
            trackCommentRepository.incrementRepliesCount(request.parentId)
        }

        // Generate UUID for the new comment
        val commentId = UUID.randomUUID()
        
        // Insert comment using native query (bypasses JPA entity issues)
        trackCommentRepository.insertComment(
            id = commentId,
            trackId = request.trackId,
            userId = request.userId,
            parentId = request.parentId,
            content = request.content,
            status = "ACTIVE"
        )
        
        // Fetch with user relation for response
        val commentWithUser = trackCommentRepository.findByIdWithUser(commentId)
            .orElseThrow { ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve saved comment") }

        return commentWithUser.toResponse(isLiked = false)
    }

    override fun getCommentsByTrackId(trackId: UUID, userId: UUID?): List<CommentResponse> {
        // Get all top-level comments (parentId IS NULL)
        val topLevelComments = trackCommentRepository.findTopLevelCommentsByTrackId(trackId)

        if (topLevelComments.isEmpty()) {
            return emptyList()
        }

        // Get all comment IDs (top-level + replies)
        val allCommentIds = mutableListOf<UUID>()
        topLevelComments.forEach { comment ->
            allCommentIds.add(comment.id)
        }

        // Get all replies for top-level comments
        val allReplies = mutableListOf<TrackComment>()
        topLevelComments.forEach { comment ->
            val replies = trackCommentRepository.findRepliesByParentId(comment.id)
            allReplies.addAll(replies)
            replies.forEach { reply -> allCommentIds.add(reply.id) }
        }

        // Get likes count for all comments
        val likesCounts = if (allCommentIds.isNotEmpty()) {
            trackCommentReactionRepository.countReactionsByCommentIds(allCommentIds)
                .associate { it.commentId to it.cnt.toInt() }
        } else {
            emptyMap()
        }

        // Get liked comment IDs for the user (if userId provided)
        val likedCommentIds = if (userId != null && allCommentIds.isNotEmpty()) {
            trackCommentReactionRepository.findLikedCommentIdsByUser(userId, allCommentIds).toSet()
        } else {
            emptySet()
        }

        // Build replies map
        val repliesMap = allReplies.groupBy { it.parentId }

        // Build response with nested replies
        return topLevelComments.map { comment ->
            val commentLikesCount = likesCounts[comment.id] ?: comment.likesCount
            val isLiked = likedCommentIds.contains(comment.id)
            
            val replies = (repliesMap[comment.id] ?: emptyList()).map { reply ->
                val replyLikesCount = likesCounts[reply.id] ?: reply.likesCount
                val replyIsLiked = likedCommentIds.contains(reply.id)
                reply.toResponse(isLiked = replyIsLiked).copy(likesCount = replyLikesCount)
            }

            comment.toResponse(isLiked = isLiked, replies = replies).copy(likesCount = commentLikesCount)
        }
    }

    override fun getCommentsPage(trackId: UUID, userId: UUID?, limit: Int, cursor: String?): CursorPageResponse<CommentResponse> {
        val safeLimit = limit.coerceIn(1, 100)
        val pageable = PageRequest.of(0, safeLimit + 1)

        val decoded = CreatedAtIdCursorCodec.decodeOrBadRequest(cursor)
        val topLevelComments = if (decoded == null) {
            trackCommentRepository.findTopLevelCommentsPageFirst(trackId, pageable)
        } else {
            trackCommentRepository.findTopLevelCommentsPageAfter(
                trackId = trackId,
                cursorCreatedAt = decoded.createdAt,
                cursorId = decoded.id,
                pageable = pageable
            )
        }

        if (topLevelComments.isEmpty()) {
            return CursorPageResponse(
                items = emptyList(),
                nextCursor = null,
                hasNext = false
            )
        }

        val hasNext = topLevelComments.size > safeLimit
        val slice = if (hasNext) topLevelComments.take(safeLimit) else topLevelComments

        // Get all comment IDs (top-level + replies)
        val allCommentIds = mutableListOf<UUID>()
        slice.forEach { comment ->
            allCommentIds.add(comment.id)
        }

        // Get all replies for top-level comments in this page
        val allReplies = mutableListOf<TrackComment>()
        slice.forEach { comment ->
            val replies = trackCommentRepository.findRepliesByParentId(comment.id)
            allReplies.addAll(replies)
            replies.forEach { reply -> allCommentIds.add(reply.id) }
        }

        // Get likes count for all comments
        val likesCounts = if (allCommentIds.isNotEmpty()) {
            trackCommentReactionRepository.countReactionsByCommentIds(allCommentIds)
                .associate { it.commentId to it.cnt.toInt() }
        } else {
            emptyMap()
        }

        // Get liked comment IDs for the user (if userId provided)
        val likedCommentIds = if (userId != null && allCommentIds.isNotEmpty()) {
            trackCommentReactionRepository.findLikedCommentIdsByUser(userId, allCommentIds).toSet()
        } else {
            emptySet()
        }

        // Build replies map
        val repliesMap = allReplies.groupBy { it.parentId }

        // Build response with nested replies
        val items = slice.map { comment ->
            val commentLikesCount = likesCounts[comment.id] ?: comment.likesCount
            val isLiked = likedCommentIds.contains(comment.id)
            
            val replies = (repliesMap[comment.id] ?: emptyList()).map { reply ->
                val replyLikesCount = likesCounts[reply.id] ?: reply.likesCount
                val replyIsLiked = likedCommentIds.contains(reply.id)
                reply.toResponse(isLiked = replyIsLiked).copy(likesCount = replyLikesCount)
            }

            comment.toResponse(isLiked = isLiked, replies = replies).copy(likesCount = commentLikesCount)
        }

        val last = slice.lastOrNull()
        val nextCursor = if (hasNext && last?.createdAt != null) {
            CreatedAtIdCursorCodec.encode(last.createdAt, last.id)
        } else {
            null
        }

        return CursorPageResponse(
            items = items,
            nextCursor = nextCursor,
            hasNext = hasNext
        )
    }

    @Transactional
    override fun addReaction(request: CommentReactionRequest) {
        // Validate comment exists
        if (!trackCommentRepository.existsById(request.commentId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found")
        }

        // Validate user exists
        if (!userRepository.existsById(request.userId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        }

        // Insert reaction using native query
        trackCommentReactionRepository.insertReaction(
            commentId = request.commentId,
            userId = request.userId,
            reaction = request.reaction
        )

        // Update likes count in track_comment table
        trackCommentRepository.updateLikesCount(request.commentId)
    }

    @Transactional
    override fun removeReaction(commentId: UUID, userId: UUID) {
        // Validate comment exists
        if (!trackCommentRepository.existsById(commentId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found")
        }

        // Validate user exists
        if (!userRepository.existsById(userId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        }

        // Delete reaction using native query
        trackCommentReactionRepository.deleteReaction(
            commentId = commentId,
            userId = userId
        )

        // Update likes count in track_comment table
        trackCommentRepository.updateLikesCount(commentId)
    }

    @Transactional
    override fun deleteComment(commentId: UUID) {
        // Validate comment exists and is active
        val comment = trackCommentRepository.findById(commentId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found") }

        if (comment.status != "ACTIVE") {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment is already deleted")
        }

        // If this comment has a parent, decrement parent's replies count
        if (comment.parentId != null) {
            trackCommentRepository.decrementRepliesCount(comment.parentId)
        }

        // Soft delete the comment by setting status to DELETED
        trackCommentRepository.deleteComment(commentId)
    }
}
