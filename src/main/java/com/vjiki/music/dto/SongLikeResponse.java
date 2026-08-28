package com.vjiki.music.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SongLikeResponse {

    @JsonProperty("isLiked")
    private Boolean isLiked;

    @JsonProperty("isDisliked")
    private Boolean isDisliked;

    private Long likesCount;
    private Long dislikesCount;
}
