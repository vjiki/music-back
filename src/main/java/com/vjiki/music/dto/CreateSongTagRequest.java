package com.vjiki.music.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSongTagRequest {
    private String name;

    private Double weight = 1.0;

    /**
     * Tag type stored in `tag.type` (e.g. GENRE / MOOD / DEFAULT)
     */
    private String type = "DEFAULT";

    /**
     * Stored in `track_tag.source` (e.g. MANUAL / ML / IMPORT)
     */
    private String source = "MANUAL";
}
