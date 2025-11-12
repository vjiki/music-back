package com.vjiki.music.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistSongResponse {
    private UUID id;
    private UUID playlistId;
    private UUID songId;
    private String songTitle;
    private String songArtist;
    private String songAudioUrl;
    private String songCoverUrl;
    private Integer position;
    private OffsetDateTime addedAt;
    private UUID addedBy;
}

