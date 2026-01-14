package com.vjiki.music.repository

import com.vjiki.music.entity.TrackCommentReaction
import com.vjiki.music.entity.TrackCommentReactionId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TrackCommentReactionRepository : JpaRepository<TrackCommentReaction, TrackCommentReactionId> {

    fun existsByCommentIdAndUserId(commentId: UUID, userId: UUID): Boolean

    @Query(
        """
        SELECT COUNT(r) FROM TrackCommentReaction r 
        WHERE r.commentId = :commentId
        """
    )
    fun countByCommentId(@Param("commentId") commentId: UUID): Long

    @Query(
        """
        SELECT r.commentId AS commentId, COUNT(r) AS cnt 
        FROM TrackCommentReaction r 
        WHERE r.commentId IN :commentIds
        GROUP BY r.commentId
        """
    )
    fun countReactionsByCommentIds(@Param("commentIds") commentIds: List<UUID>): List<ReactionCountProjection>

    @Query(
        """
        SELECT r.commentId FROM TrackCommentReaction r 
        WHERE r.userId = :userId AND r.commentId IN :commentIds
        """
    )
    fun findLikedCommentIdsByUser(@Param("userId") userId: UUID, @Param("commentIds") commentIds: List<UUID>): List<UUID>

    @Modifying
    @Query(
        value = """
            INSERT INTO music.track_comment_reaction (comment_id, user_id, reaction, created_at)
            VALUES (:commentId, :userId, :reaction, NOW())
            ON CONFLICT (comment_id, user_id) DO NOTHING
        """,
        nativeQuery = true
    )
    fun insertReaction(
        @Param("commentId") commentId: UUID,
        @Param("userId") userId: UUID,
        @Param("reaction") reaction: String = "LIKE"
    )

    @Modifying
    @Query(
        value = """
            DELETE FROM music.track_comment_reaction
            WHERE comment_id = :commentId AND user_id = :userId
        """,
        nativeQuery = true
    )
    fun deleteReaction(
        @Param("commentId") commentId: UUID,
        @Param("userId") userId: UUID
    )

    interface ReactionCountProjection {
        val commentId: UUID
        val cnt: Long
    }
}
