package com.vjiki.music.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSongRequest {
    private String title;
    private List<String> artists = new ArrayList<>();

    @JsonProperty("audio_url")
    private String audioUrl;

    private String cover;

    @JsonProperty("video_url")
    private String videoUrl;

    /**
     * SONG or SHORT (stored as plain string in DB column `songs.type`)
     */
    private String type = "SONG";

    private Boolean active = true;

    private List<CreateSongTagRequest> tags = new ArrayList<>();
}
