package com.vjiki.music.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongResponse {
    private String id;
    private String artist;
    
    @JsonProperty("audio_url")
    private String audioUrl;
    
    private String cover;
    private String title;
}

