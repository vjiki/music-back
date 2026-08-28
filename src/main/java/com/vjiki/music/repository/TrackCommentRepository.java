package com.vjiki.music.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vjiki.music.entity.TrackComment;

@Repository
public interface TrackCommentRepository extends JpaRepository<TrackComment, UUID> {

    @Query("""
            SELECT c FROM TrackComment c
            LEFT JOIN FETCH c.user
            WHERE c.trackId = :trackId
            AND c.parentId IS NULL
            AND c.status = 'ACTIVE'
            ORDER BY c.createdAt DESC
            """)
    List<TrackComment> findTopLevelCommentsByTrackId(@Param("trackId") UUID trackId);

    @Query("""
            SELECT c FROM TrackComment c
            LEFT JOIN FETCH c.user
            WHERE c.parentId = :parentId
            AND c.status = 'ACTIVE'
            ORDER BY c.createdAt ASC
            """)
    List<TrackComment> findRepliesByParentId(@Param("parentId") UUID parentId);

    @Query("""
            SELECT c FROM TrackComment c
            WHERE c.trackId = :trackId
            AND c.status = 'ACTIVE'
            """)
    List<TrackComment> findAllActiveCommentsByTrackId(@Param("trackId") UUID trackId);

    @Modifying
    @Query(value = """
            UPDATE music.track_comment
            SET replies_count = replies_count + 1
            WHERE id = :parentId
            """, nativeQuery = true)
    void incrementRepliesCount(@Param("parentId") UUID parentId);

    @Modifying
    @Query(value = """
            UPDATE music.track_comment
            SET replies_count = replies_count - 1
            WHERE id = :parentId AND replies_count > 0
            """, nativeQuery = true)
    void decrementRepliesCount(@Param("parentId") UUID parentId);

    @Modifying
    @Query(value = """
            UPDATE music.track_comment
            SET status = 'DELETED', updated_at = NOW()
            WHERE id = :commentId AND status = 'ACTIVE'
            """, nativeQuery = true)
    void deleteComment(@Param("commentId") UUID commentId);

    @Modifying
    @Query(value = """
            UPDATE music.track_comment
            SET likes_count = (
                SELECT COUNT(*)
                FROM music.track_comment_reaction
                WHERE comment_id = :commentId
            )
            WHERE id = :commentId
            """, nativeQuery = true)
    void updateLikesCount(@Param("commentId") UUID commentId);

    @Modifying
    @Query(value = """
            INSERT INTO music.track_comment (id, track_id, user_id, parent_id, content, status, likes_count, replies_count, created_at)
            VALUES (:id, :trackId, :userId, :parentId, :content, :status, 0, 0, NOW())
            """, nativeQuery = true)
    void insertComment(
            @Param("id") UUID id,
            @Param("trackId") UUID trackId,
            @Param("userId") UUID userId,
            @Param("parentId") UUID parentId,
            @Param("content") String content,
            @Param("status") String status);

    default void insertComment(UUID id, UUID trackId, UUID userId, UUID parentId, String content) {
        insertComment(id, trackId, userId, parentId, content, "ACTIVE");
    }

    boolean existsByIdAndStatus(UUID id, String status);

    @Query("""
            SELECT c FROM TrackComment c
            LEFT JOIN FETCH c.user
            WHERE c.id = :id
            """)
    Optional<TrackComment> findByIdWithUser(@Param("id") UUID id);

    @Query(value = """
            SELECT *
            FROM music.track_comment c
            WHERE c.track_id = :trackId
              AND c.parent_id IS NULL
              AND c.status = 'ACTIVE'
            ORDER BY c.created_at DESC, c.id DESC
            """, nativeQuery = true)
    List<TrackComment> findTopLevelCommentsPageFirst(
            @Param("trackId") UUID trackId,
            Pageable pageable);

    @Query(value = """
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
            """, nativeQuery = true)
    List<TrackComment> findTopLevelCommentsPageAfter(
            @Param("trackId") UUID trackId,
            @Param("cursorCreatedAt") OffsetDateTime cursorCreatedAt,
            @Param("cursorId") UUID cursorId,
            Pageable pageable);
}
