package com.vjiki.music.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongLikeRequest {
    private UUID userId;
    private UUID songId;
}
