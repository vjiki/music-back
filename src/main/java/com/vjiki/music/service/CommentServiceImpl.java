package com.vjiki.music.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.vjiki.music.dto.CommentReactionRequest;
import com.vjiki.music.dto.CommentRequest;
import com.vjiki.music.dto.CommentResponse;
import com.vjiki.music.entity.TrackComment;
import com.vjiki.music.mapper.CommentMapper;
import com.vjiki.music.pagination.CreatedAtIdCursorCodec;
import com.vjiki.music.pagination.CursorPageResponse;
import com.vjiki.music.repository.SongRepository;
import com.vjiki.music.repository.TrackCommentReactionRepository;
import com.vjiki.music.repository.TrackCommentRepository;
import com.vjiki.music.repository.UserRepository;

@Service
public class CommentServiceImpl implements CommentService {

    private static final String STATUS_ACTIVE = "ACTIVE";

    private final TrackCommentRepository trackCommentRepository;
    private final TrackCommentReactionRepository trackCommentReactionRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;

    public CommentServiceImpl(TrackCommentRepository trackCommentRepository,
                              TrackCommentReactionRepository trackCommentReactionRepository,
                              SongRepository songRepository,
                              UserRepository userRepository) {
        this.trackCommentRepository = trackCommentRepository;
        this.trackCommentReactionRepository = trackCommentReactionRepository;
        this.songRepository = songRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public CommentResponse addComment(CommentRequest request) {
        if (!songRepository.existsById(request.getTrackId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Track not found");
        }

        if (!userRepository.existsById(request.getUserId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        if (request.getParentId() != null) {
            TrackComment parentComment = trackCommentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Parent comment not found"));

            if (!parentComment.getTrackId().equals(request.getTrackId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Parent comment does not belong to this track");
            }

            if (!STATUS_ACTIVE.equals(parentComment.getStatus())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot reply to inactive comment");
            }

            trackCommentRepository.incrementRepliesCount(request.getParentId());
        }

        UUID commentId = UUID.randomUUID();

        trackCommentRepository.insertComment(
                commentId,
                request.getTrackId(),
                request.getUserId(),
                request.getParentId(),
                request.getContent(),
                STATUS_ACTIVE);

        TrackComment commentWithUser = trackCommentRepository.findByIdWithUser(commentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve saved comment"));

        return CommentMapper.toResponse(commentWithUser, false);
    }

    @Override
    public List<CommentResponse> getCommentsByTrackId(UUID trackId, UUID userId) {
        List<TrackComment> topLevelComments = trackCommentRepository.findTopLevelCommentsByTrackId(trackId);

        if (topLevelComments.isEmpty()) {
            return List.of();
        }

        return buildCommentTree(topLevelComments, userId);
    }

    @Override
    public CursorPageResponse<CommentResponse> getCommentsPage(UUID trackId, UUID userId, int limit, String cursor) {
        int safeLimit = Math.min(Math.max(limit, 1), 100);
        Pageable pageable = PageRequest.of(0, safeLimit + 1);

        CreatedAtIdCursorCodec.Cursor decoded = CreatedAtIdCursorCodec.decodeOrBadRequest(cursor);
        List<TrackComment> topLevelComments = decoded == null
                ? trackCommentRepository.findTopLevelCommentsPageFirst(trackId, pageable)
                : trackCommentRepository.findTopLevelCommentsPageAfter(
                        trackId, decoded.createdAt(), decoded.id(), pageable);

        if (topLevelComments.isEmpty()) {
            return new CursorPageResponse<>(List.of(), null, false);
        }

        boolean hasNext = topLevelComments.size() > safeLimit;
        List<TrackComment> slice = hasNext ? topLevelComments.subList(0, safeLimit) : topLevelComments;

        List<CommentResponse> items = buildCommentTree(slice, userId);

        TrackComment last = slice.getLast();
        String nextCursor = (hasNext && last.getCreatedAt() != null)
                ? CreatedAtIdCursorCodec.encode(last.getCreatedAt(), last.getId())
                : null;

        return new CursorPageResponse<>(items, nextCursor, hasNext);
    }

    /**
     * Resolves replies, reaction counts and the caller's own likes for a page of
     * top-level comments, then assembles the nested response.
     */
    private List<CommentResponse> buildCommentTree(List<TrackComment> topLevelComments, UUID userId) {
        List<UUID> allCommentIds = new ArrayList<>();
        for (TrackComment comment : topLevelComments) {
            allCommentIds.add(comment.getId());
        }

        Map<UUID, List<TrackComment>> repliesByParentId = new HashMap<>();
        for (TrackComment comment : topLevelComments) {
            List<TrackComment> replies = trackCommentRepository.findRepliesByParentId(comment.getId());
            repliesByParentId.put(comment.getId(), replies);
            for (TrackComment reply : replies) {
                allCommentIds.add(reply.getId());
            }
        }

        Map<UUID, Integer> likesCounts = new HashMap<>();
        if (!allCommentIds.isEmpty()) {
            for (var row : trackCommentReactionRepository.countReactionsByCommentIds(allCommentIds)) {
                likesCounts.put(row.getCommentId(), row.getCnt().intValue());
            }
        }

        Set<UUID> likedCommentIds = (userId != null && !allCommentIds.isEmpty())
                ? new HashSet<>(trackCommentReactionRepository.findLikedCommentIdsByUser(userId, allCommentIds))
                : Set.of();

        List<CommentResponse> items = new ArrayList<>(topLevelComments.size());
        for (TrackComment comment : topLevelComments) {
            List<CommentResponse> replies = repliesByParentId.getOrDefault(comment.getId(), List.of()).stream()
                    .map(reply -> toResponseWithCount(reply, likedCommentIds, likesCounts, List.of()))
                    .toList();

            items.add(toResponseWithCount(comment, likedCommentIds, likesCounts, replies));
        }
        return items;
    }

    private static CommentResponse toResponseWithCount(TrackComment comment,
                                                       Set<UUID> likedCommentIds,
                                                       Map<UUID, Integer> likesCounts,
                                                       List<CommentResponse> replies) {
        CommentResponse response =
                CommentMapper.toResponse(comment, likedCommentIds.contains(comment.getId()), replies);
        response.setLikesCount(likesCounts.getOrDefault(comment.getId(), comment.getLikesCount()));
        return response;
    }

    @Override
    @Transactional
    public void addReaction(CommentReactionRequest request) {
        if (!trackCommentRepository.existsById(request.getCommentId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found");
        }

        if (!userRepository.existsById(request.getUserId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        trackCommentReactionRepository.insertReaction(
                request.getCommentId(), request.getUserId(), request.getReaction());

        trackCommentRepository.updateLikesCount(request.getCommentId());
    }

    @Override
    @Transactional
    public void removeReaction(UUID commentId, UUID userId) {
        if (!trackCommentRepository.existsById(commentId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found");
        }

        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        trackCommentReactionRepository.deleteReaction(commentId, userId);

        trackCommentRepository.updateLikesCount(commentId);
    }

    @Override
    @Transactional
    public void deleteComment(UUID commentId) {
        TrackComment comment = trackCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        if (!STATUS_ACTIVE.equals(comment.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment is already deleted");
        }

        if (comment.getParentId() != null) {
            trackCommentRepository.decrementRepliesCount(comment.getParentId());
        }

        trackCommentRepository.deleteComment(commentId);
    }
}
