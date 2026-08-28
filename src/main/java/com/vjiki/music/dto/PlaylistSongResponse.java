package com.vjiki.music.dto;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaylistSongResponse {
    private UUID id;
    private UUID playlistId;
    private UUID songId;
    private String songTitle;
    private String songArtist;
    private String songAudioUrl;
    private String songCoverUrl;

    @Builder.Default
    private List<TagResponse> tags = new ArrayList<>();

    private Integer position;
    private OffsetDateTime addedAt;
    private UUID addedBy;
}
