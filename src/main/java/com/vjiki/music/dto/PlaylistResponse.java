package com.vjiki.music.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistResponse {
    private UUID id;
    private UUID userId;
    private String userName;
    private String userNickname;
    private String name;
    private String description;
    private String coverUrl;
    private String type;
    private Boolean isPublic;
    private OffsetDateTime createdAt;
    private OffsetDateTime modifiedAt;
}

