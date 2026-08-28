package com.vjiki.music.mapper;

import java.util.List;

import com.vjiki.music.dto.CommentResponse;
import com.vjiki.music.entity.TrackComment;

public final class CommentMapper {

    private CommentMapper() {
    }

    public static CommentResponse toResponse(TrackComment comment) {
        return toResponse(comment, false, List.of());
    }

    public static CommentResponse toResponse(TrackComment comment, boolean isLiked) {
        return toResponse(comment, isLiked, List.of());
    }

    public static CommentResponse toResponse(TrackComment comment,
                                             boolean isLiked,
                                             List<CommentResponse> replies) {
        return CommentResponse.builder()
                .id(comment.getId())
                .trackId(comment.getTrackId())
                .userId(comment.getUserId())
                .userNickname(comment.getUser() == null ? null : comment.getUser().getNickname())
                .userAvatarUrl(comment.getUser() == null ? null : comment.getUser().getAvatarUrl())
                .parentId(comment.getParentId())
                .content(comment.getContent())
                .status(comment.getStatus())
                .likesCount(comment.getLikesCount())
                .repliesCount(comment.getRepliesCount())
                .isLiked(isLiked)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .replies(replies == null ? List.of() : replies)
                .build();
    }
}
