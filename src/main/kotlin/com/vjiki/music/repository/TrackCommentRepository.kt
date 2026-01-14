package com.vjiki.music.repository

import com.vjiki.music.entity.TrackComment
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.*

@Repository
interface TrackCommentRepository : JpaRepository<TrackComment, UUID> {

    @Query(
        """
        SELECT c FROM TrackComment c 
        LEFT JOIN FETCH c.user
        WHERE c.trackId = :trackId 
        AND c.parentId IS NULL 
        AND c.status = 'ACTIVE'
        ORDER BY c.createdAt DESC
        """
    )
    fun findTopLevelCommentsByTrackId(@Param("trackId") trackId: UUID): List<TrackComment>

    @Query(
        """
        SELECT c FROM TrackComment c 
        LEFT JOIN FETCH c.user
        WHERE c.parentId = :parentId 
        AND c.status = 'ACTIVE'
        ORDER BY c.createdAt ASC
        """
    )
    fun findRepliesByParentId(@Param("parentId") parentId: UUID): List<TrackComment>

    @Query(
        """
        SELECT c FROM TrackComment c 
        WHERE c.trackId = :trackId 
        AND c.status = 'ACTIVE'
        """
    )
    fun findAllActiveCommentsByTrackId(@Param("trackId") trackId: UUID): List<TrackComment>

    @Modifying
    @Query(
        value = """
            UPDATE music.track_comment 
            SET replies_count = replies_count + 1 
            WHERE id = :parentId
        """,
        nativeQuery = true
    )
    fun incrementRepliesCount(@Param("parentId") parentId: UUID)

    @Modifying
    @Query(
        value = """
            UPDATE music.track_comment 
            SET replies_count = replies_count - 1 
            WHERE id = :parentId AND replies_count > 0
        """,
        nativeQuery = true
    )
    fun decrementRepliesCount(@Param("parentId") parentId: UUID)

    @Modifying
    @Query(
        value = """
            UPDATE music.track_comment 
            SET status = 'DELETED', updated_at = NOW()
            WHERE id = :commentId AND status = 'ACTIVE'
        """,
        nativeQuery = true
    )
    fun deleteComment(@Param("commentId") commentId: UUID)

    @Modifying
    @Query(
        value = """
            UPDATE music.track_comment 
            SET likes_count = (
                SELECT COUNT(*) 
                FROM music.track_comment_reaction 
                WHERE comment_id = :commentId
            )
            WHERE id = :commentId
        """,
        nativeQuery = true
    )
    fun updateLikesCount(@Param("commentId") commentId: UUID)

    @Modifying
    @Query(
        value = """
            INSERT INTO music.track_comment (id, track_id, user_id, parent_id, content, status, likes_count, replies_count, created_at)
            VALUES (:id, :trackId, :userId, :parentId, :content, :status, 0, 0, NOW())
        """,
        nativeQuery = true
    )
    fun insertComment(
        @Param("id") id: UUID,
        @Param("trackId") trackId: UUID,
        @Param("userId") userId: UUID,
        @Param("parentId") parentId: UUID?,
        @Param("content") content: String,
        @Param("status") status: String = "ACTIVE"
    )

    fun existsByIdAndStatus(id: UUID, status: String): Boolean

    @Query(
        """
        SELECT c FROM TrackComment c 
        LEFT JOIN FETCH c.user
        WHERE c.id = :id
        """
    )
    fun findByIdWithUser(@Param("id") id: UUID): Optional<TrackComment>

    @Query(
        value = """
            SELECT *
            FROM music.track_comment c
            WHERE c.track_id = :trackId
              AND c.parent_id IS NULL
              AND c.status = 'ACTIVE'
            ORDER BY c.created_at DESC, c.id DESC
        """,
        nativeQuery = true
    )
    fun findTopLevelCommentsPageFirst(
        @Param("trackId") trackId: UUID,
        pageable: Pageable
    ): List<TrackComment>

    @Query(
        value = """
            SELECT *
            FROM music.track_comment c
            WHERE c.track_id = :trackId
              AND c.parent_id IS NULL
              AND c.status = 'ACTIVE'
              AND (
                c.created_at < :cursorCreatedAt
                OR (c.created_at = :cursorCreatedAt AND c.id < :cursorId)
              )
            ORDER BY c.created_at DESC, c.id DESC
        """,
        nativeQuery = true
    )
    fun findTopLevelCommentsPageAfter(
        @Param("trackId") trackId: UUID,
        @Param("cursorCreatedAt") cursorCreatedAt: OffsetDateTime,
        @Param("cursorId") cursorId: UUID,
        pageable: Pageable
    ): List<TrackComment>
}
