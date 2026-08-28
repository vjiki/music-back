package com.vjiki.music.dto;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {
    private UUID id;

    @JsonProperty("track_id")
    private UUID trackId;

    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("user_nickname")
    private String userNickname;

    @JsonProperty("user_avatar_url")
    private String userAvatarUrl;

    @JsonProperty("parent_id")
    private UUID parentId;

    private String content;
    private String status;

    @JsonProperty("likes_count")
    private Integer likesCount;

    @JsonProperty("replies_count")
    private Integer repliesCount;

    @JsonProperty("is_liked")
    @Builder.Default
    private Boolean isLiked = false;

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

    @JsonProperty("updated_at")
    private OffsetDateTime updatedAt;

    @Builder.Default
    private List<CommentResponse> replies = new ArrayList<>();
}
