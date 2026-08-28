package com.vjiki.music.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortResponse {
    private String id;
    private String artist;

    @JsonProperty("audio_url")
    private String audioUrl;

    private String cover;
    private String title;

    @JsonProperty("video_url")
    private String videoUrl;

    private String type;

    @Builder.Default
    private List<TagResponse> tags = new ArrayList<>();

    @JsonProperty("isLiked")
    @Builder.Default
    private Boolean isLiked = false;

    @JsonProperty("isDisliked")
    @Builder.Default
    private Boolean isDisliked = false;

    @Builder.Default
    private Long likesCount = 0L;

    @Builder.Default
    private Long dislikesCount = 0L;
}
