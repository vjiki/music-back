package com.vjiki.music.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagResponse {
    private String name;

    @Builder.Default
    private Double weight = 1.0;

    public TagResponse(String name) {
        this.name = name;
        this.weight = 1.0;
    }
}
