package com.vjiki.music.dto;

import java.time.LocalDateTime;
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
public class BandResponse {
    private UUID id;
    private String name;

    @JsonProperty("sort_name")
    private String sortName;

    @JsonProperty("country_code")
    private String countryCode;

    @JsonProperty("is_band")
    private Boolean isBand;

    @JsonProperty("debut_year")
    private Short debutYear;

    private Integer popularity;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("cover_url")
    private String coverUrl;

    @Builder.Default
    private List<SongResponse> songs = new ArrayList<>();
}
