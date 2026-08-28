package com.vjiki.music.dto;

import java.time.OffsetDateTime;
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
public class PlaylistResponse {
    private UUID id;
    private UUID userId;
    private String userName;
    private String userNickname;
    private String name;
    private String description;
    private String coverUrl;
    private String type;

    @JsonProperty("isPublic")
    private Boolean isPublic;

    private OffsetDateTime createdAt;
    private OffsetDateTime modifiedAt;
}
