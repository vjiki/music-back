package com.vjiki.music.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vjiki.music.entity.TrackCommentReaction;
import com.vjiki.music.entity.TrackCommentReactionId;

@Repository
public interface TrackCommentReactionRepository
        extends JpaRepository<TrackCommentReaction, TrackCommentReactionId> {

    boolean existsByCommentIdAndUserId(UUID commentId, UUID userId);

    @Query("""
            SELECT COUNT(r) FROM TrackCommentReaction r
            WHERE r.commentId = :commentId
            """)
    long countByCommentId(@Param("commentId") UUID commentId);

    @Query("""
            SELECT r.commentId AS commentId, COUNT(r) AS cnt
            FROM TrackCommentReaction r
            WHERE r.commentId IN :commentIds
            GROUP BY r.commentId
            """)
    List<ReactionCountProjection> countReactionsByCommentIds(@Param("commentIds") List<UUID> commentIds);

    @Query("""
            SELECT r.commentId FROM TrackCommentReaction r
            WHERE r.userId = :userId AND r.commentId IN :commentIds
            """)
    List<UUID> findLikedCommentIdsByUser(@Param("userId") UUID userId, @Param("commentIds") List<UUID> commentIds);

    @Modifying
    @Query(value = """
            INSERT INTO music.track_comment_reaction (comment_id, user_id, reaction, created_at)
            VALUES (:commentId, :userId, :reaction, NOW())
            ON CONFLICT (comment_id, user_id) DO NOTHING
            """, nativeQuery = true)
    void insertReaction(
            @Param("commentId") UUID commentId,
            @Param("userId") UUID userId,
            @Param("reaction") String reaction);

    default void insertReaction(UUID commentId, UUID userId) {
        insertReaction(commentId, userId, "LIKE");
    }

    @Modifying
    @Query(value = """
            DELETE FROM music.track_comment_reaction
            WHERE comment_id = :commentId AND user_id = :userId
            """, nativeQuery = true)
    void deleteReaction(
            @Param("commentId") UUID commentId,
            @Param("userId") UUID userId);

    interface ReactionCountProjection {
        UUID getCommentId();

        Long getCnt();
    }
}
